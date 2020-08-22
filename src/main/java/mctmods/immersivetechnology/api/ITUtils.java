package mctmods.immersivetechnology.api;

import mctmods.immersivetechnology.api.client.MechanicalEnergyAnimation;
import mctmods.immersivetechnology.common.blocks.ITBlockInterfaces.IMechanicalEnergy;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntityAlternatorMaster;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ITUtils {
	public static IFluidTank[] emptyIFluidTankList = new IFluidTank[0];

	public static final Set<TileEntity> REMOVE_FROM_TICKING = new HashSet<>();

	public static void RemoveDummyFromTicking(TileEntity te) {
		REMOVE_FROM_TICKING.add(te);
	}
	
	public static boolean AreBlockPosIdentical(BlockPos a, BlockPos b) {
		return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
	}
	
	public static BlockPos LocalOffsetToWorldBlockPos(BlockPos origin, int x, int y, int z, Direction facing) {
		return LocalOffsetToWorldBlockPos(origin, x, y, z, facing, Direction.UP);
	}

	public static BlockPos LocalOffsetToWorldBlockPos(BlockPos origin, int x, int y, int z, Direction facing, boolean mirrored) {
        return LocalOffsetToWorldBlockPos(origin, mirrored? -x : x, y, z, facing, Direction.UP);
    }

	public static BlockPos LocalOffsetToWorldBlockPos(BlockPos origin, int x, int y, int z, Direction facing, Direction up) {
		if(facing.getAxis() == up.getAxis()) throw new IllegalArgumentException("'facing' and 'up' must be perpendicular to each other!");
		switch(up) {
			case UP:
				switch(facing) {
					case SOUTH:
						return origin.add(-x, y, z);
					case NORTH:
						return origin.add(x, y, -z);
					case EAST:
						return origin.add(z, y, x);
					case WEST:
						return origin.add(-z, y, -x);
					default:
						break;
				}
				break;
			case DOWN:
				switch(facing) {
					case SOUTH:
						return origin.add(x, -y, z);
					case NORTH:
						return origin.add(-x, -y, -z);
					case EAST:
						return origin.add(z, -y, -x);
					case WEST:
						return origin.add(-z, -y, x);
					default:
						break;
				}
				break;
			case NORTH:
				switch(facing) {
					case UP:
						return origin.add(-x, z, -y);
					case DOWN:
						return origin.add(x, -z, -y);
					case EAST:
						return origin.add(z, x, -y);
					case WEST:
						return origin.add(-z, -x, -y);
					default:
						break;
				}
				break;
			case SOUTH:
				switch(facing) {
					case UP:
						return origin.add(x, z, y);
					case DOWN:
						return origin.add(-x, -z, y);
					case EAST:
						return origin.add(z, -x, y);
					case WEST:
						return origin.add(-z, x, y);
					default:
						break;
				}
				break;
			case EAST:
				switch(facing) {
					case UP:
						return origin.add(y, z, -x);
					case DOWN:
						return origin.add(y, -z, x);
					case SOUTH:
						return origin.add(y, x, z);
					case NORTH:
						return origin.add(y, -x, -z);
					default:
						break;
				}
				break;
			case WEST:
				switch(facing) {
					case UP:
						return origin.add(-y, z, x);
					case DOWN:
						return origin.add(-y, -z, -x);
					case SOUTH:
						return origin.add(-y, -x, z);
					case NORTH:
						return origin.add(-y, x, -z);
					default:
						break;
				}
				break;
		}
		throw new IllegalArgumentException("This part of the code should never be reached! Has Direction changed ? ");
	}

	public static <T> T First(ArrayList<T> list, Object o) {
		for(T item : list) {
			if(item.equals(o)) return item;
		}
		return null;
	}

	public static double[] smartBoundingBox(double A, double B, double C, double D, double minY, double maxY, Direction fl, Direction fw) {
		double[] boundingArray = new double[6];
		boundingArray[0] = fl == Direction.WEST ? A : fl == Direction.EAST ? B : fw == Direction.EAST ? C : D;
		boundingArray[1] = minY;
		boundingArray[2] = fl == Direction.NORTH ? A : fl == Direction.SOUTH ? B : fw == Direction.SOUTH ? C : D;
		boundingArray[3] = fl == Direction.EAST ? 1 - A : fl == Direction.WEST ? 1 - B : fw == Direction.EAST ? 1 - D : 1 - C;
		boundingArray[4] = maxY;
		boundingArray[5] = fl == Direction.SOUTH ? 1 - A : fl == Direction.NORTH ? 1 - B : fw == Direction.SOUTH ? 1 - D : 1 - C;
		return boundingArray;
	}

	public static double[] alternativeSmartBoundingBox(double A, double B, double C, double D, double minY, double maxY, Direction fl, Direction fw) {
		double[] boundingArray = new double[6];

		boundingArray[0] = fl == Direction.WEST ? 1 - A : fl == Direction.EAST ? A : fw == Direction.EAST ? 1 - C : C;
		boundingArray[1] = minY;
		boundingArray[2] = fl == Direction.NORTH ? 1 - A : fl == Direction.SOUTH ? A : fw == Direction.SOUTH ? 1 - C : C;
		boundingArray[3] = fl == Direction.EAST ? B : fl == Direction.WEST ? 1 - B : fw == Direction.EAST ? 1 - D : D;
		boundingArray[4] = maxY;
		boundingArray[5] = fl == Direction.SOUTH ? B : fl == Direction.NORTH ? 1 - B : fw == Direction.SOUTH ? 1 - D : D;

		return boundingArray;
	}

	public static boolean checkMechanicalEnergyTransmitter(World world, BlockPos startPos) {
		TileEntity tile = world.getTileEntity(startPos);
		if(tile instanceof IMechanicalEnergy) {
			if(((IMechanicalEnergy) tile).isMechanicalEnergyReceiver()) {
				Direction inputFacing = ((IMechanicalEnergy) tile).getMechanicalEnergyInputFacing();
				BlockPos pos = startPos.offset(inputFacing, ((IMechanicalEnergy) tile).inputToCenterDistance() + 1);
				TileEntity tileTransmitter = world.getTileEntity(pos);
				if(tileTransmitter instanceof IMechanicalEnergy && ((IMechanicalEnergy) tileTransmitter).isMechanicalEnergyTransmitter() && (((IMechanicalEnergy) tileTransmitter).getMechanicalEnergyOutputFacing() == inputFacing.getOpposite())) return true;
			}
		}
		return false;
	}

	public static boolean checkMechanicalEnergyReceiver(World world, BlockPos startPos) {
		TileEntity tile = world.getTileEntity(startPos);
		if(tile instanceof IMechanicalEnergy) {
			if(((IMechanicalEnergy) tile).isMechanicalEnergyTransmitter()) {
				Direction outputFacing = ((IMechanicalEnergy) tile).getMechanicalEnergyOutputFacing();
				BlockPos pos = startPos.offset(outputFacing, ((IMechanicalEnergy) tile).outputToCenterDistance() + 1);
				TileEntity tileReceiver = world.getTileEntity(pos);
				if(tileReceiver instanceof IMechanicalEnergy && ((IMechanicalEnergy) tileReceiver).isMechanicalEnergyReceiver() && ((IMechanicalEnergy) tileReceiver).getMechanicalEnergyInputFacing() == outputFacing.getOpposite()) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getMechanicalEnergy(World world, BlockPos startPos) {
		TileEntity tile = world.getTileEntity(startPos);
		Direction inputFacing = ((IMechanicalEnergy) tile).getMechanicalEnergyInputFacing();
		BlockPos pos = startPos.offset(inputFacing, ((IMechanicalEnergy) tile).inputToCenterDistance() + 1);
		TileEntity tileInfo = world.getTileEntity(pos);
		if(!(tileInfo instanceof IMechanicalEnergy)) return 0;
		TileEntity tileTransmitter = world.getTileEntity(pos.offset(inputFacing, ((IMechanicalEnergy) tileInfo).outputToCenterDistance()));
		if(tileTransmitter instanceof IMechanicalEnergy) {
			return ((IMechanicalEnergy) tileTransmitter).getEnergy();
		} else {
			return 0;
		}
	}

	public static boolean checkAlternatorStatus(World world, BlockPos startPos) {
		TileEntity tile = world.getTileEntity(startPos);
		Direction outputFacing = ((IMechanicalEnergy) tile).getMechanicalEnergyOutputFacing();
		BlockPos pos = startPos.offset(outputFacing, ((IMechanicalEnergy) tile).outputToCenterDistance() + 1);
		TileEntity tileInfo = world.getTileEntity(pos);
		TileEntity tileReceiver = world.getTileEntity(pos.offset(outputFacing, ((IMechanicalEnergy) tileInfo).inputToCenterDistance()));
		if(tileReceiver instanceof TileEntityAlternatorMaster) {
			if(((TileEntityAlternatorMaster) tileReceiver).canRunMechanicalEnergy()) {
				return true;
			}
		}
		return false;
	}

	public static boolean setRotationAngle(MechanicalEnergyAnimation animation, float rotationSpeed) {
		float oldMomentum = animation.getAnimationMomentum();
		float rotateTo = (animation.getAnimationRotation() + rotationSpeed) % 360;
		animation.setAnimationRotation(rotateTo);
		animation.setAnimationMomentum(rotationSpeed);
		return (oldMomentum != rotationSpeed);
	}

	public static MechanicalEnergyAnimation getMechanicalEnergyAnimation(World world, BlockPos startPos) {
		TileEntity tile = world.getTileEntity(startPos);
		Direction inputFacing = ((IMechanicalEnergy) tile).getMechanicalEnergyInputFacing();
		BlockPos pos = startPos.offset(inputFacing, ((IMechanicalEnergy) tile).inputToCenterDistance() + 1);
		TileEntity tileInfo = world.getTileEntity(pos);
		TileEntity tileTransmitter = world.getTileEntity(pos.offset(inputFacing, ((IMechanicalEnergy) tileInfo).outputToCenterDistance()));

		if(tileTransmitter instanceof IMechanicalEnergy) {
			return ((IMechanicalEnergy) tileTransmitter).getAnimation();
		} else {
			return new MechanicalEnergyAnimation();
		}
	}

	public static Direction getInputFacing(World world, BlockPos startPos) {
		TileEntity tileTransmitter;
		BlockPos pos;
		for(Direction f : Direction.HORIZONTALS) {
			pos = startPos.offset(f, 1);
			tileTransmitter = world.getTileEntity(pos);

			if(tileTransmitter instanceof IMechanicalEnergy) {
				if(((IMechanicalEnergy) tileTransmitter).isMechanicalEnergyTransmitter() && ((IMechanicalEnergy) tileTransmitter).getMechanicalEnergyOutputFacing() == f.getOpposite()) {
					return f;
				}
			}
		}
		return null;
	}

	public static EnumSet<Direction> allSides = EnumSet.allOf(Direction.class);
	public static void improvedMarkBlockForUpdate(World world, BlockPos pos, @Nullable BlockState newState) {
		improvedMarkBlockForUpdate(world, pos, newState, allSides);
	}

	public static void improvedMarkBlockForUpdate(World world, BlockPos pos, @Nullable BlockState newState, EnumSet<Direction> directions) {
		BlockState state = world.getBlockState(pos);
		if(newState == null) newState = state;
		world.notifyBlockUpdate(pos, state, newState, 3);
		if(!ForgeEventFactory.onNeighborNotify(world, pos, newState, EnumSet.allOf(Direction.class), true).isCanceled()) {
			Block blockType = newState.getBlock();
			for(Direction facing : directions) {
				BlockPos toNotify = pos.offset(facing);
				if(world.isBlockLoaded(toNotify)) world.neighborChanged(toNotify, blockType, pos);
			}
			world.updateObservingBlocksAt(pos, blockType);
		}
	}

}