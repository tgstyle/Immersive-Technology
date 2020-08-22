package mctmods.immersivetechnology.common.blocks.metal.tileentities;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.Lists;
import mctmods.immersivetechnology.api.ITUtils;
import mctmods.immersivetechnology.common.blocks.metal.multiblocks.MultiblockSolarReflector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.ArrayList;
import java.util.List;

public class TileEntitySolarReflectorSlave extends TileEntityMultiblockPart<TileEntitySolarReflectorSlave> implements IEBlockInterfaces.IAdvancedSelectionBounds, IEBlockInterfaces.IAdvancedCollisionBounds {

	private static final int[] size = { 5, 1, 3 };
	public TileEntitySolarReflectorSlave() {
		super(size);
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.readCustomNBT(nbt, descPacket);
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
	}

	@Override
	public void update() {
		ITUtils.RemoveDummyFromTicking(this);
	}

	@Override
	public boolean isDummy() {
		return true;
	}

	TileEntitySolarReflectorMaster master;

	public TileEntitySolarReflectorMaster master() {
		if(master != null && !master.tileEntityInvalid) return master;
		BlockPos masterPos = getPos().add(-offset[0], -offset[1], -offset[2]);
		TileEntity te = Utils.getExistingTileEntity(world, masterPos);
		master = te instanceof TileEntitySolarReflectorMaster?(TileEntitySolarReflectorMaster)te: null;
		return master;
	}

	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
		return new IFluidTank[0];
	}

	@Override
	protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource) {
		return false;
	}

	@Override
	protected boolean canDrainTankFrom(int iTank, Direction side) {
		return false;
	}

	@Override
	public ItemStack getOriginalBlock() {
		if(pos < 0) return ItemStack.EMPTY;
		ItemStack s = ItemStack.EMPTY;
		try {
			s = MultiblockSolarReflector.instance.getStructureManual()[pos / 3][0][pos % 3];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s.copy();
	}

	@Override
	public float[] getBlockBounds() {
		return null;
	}

	@Override
	public List<AxisAlignedBB> getAdvancedColisionBounds() {
		return getAdvancedSelectionBounds();
	}

	@Override
	public List <AxisAlignedBB> getAdvancedSelectionBounds() {
		Direction fl = facing;
		Direction fw = facing.rotateY();
		if(pos == 0 || pos == 2) return Lists.newArrayList(new AxisAlignedBB(.25f, 0, .25f, .75f, 1, .75f).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
		if(pos == 3 || pos == 5) return Lists.newArrayList(new AxisAlignedBB(.375f, 0, .375f, .625f, 1, .625f).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
		if(pos == 6 || pos == 8) {
			float minX = fl == Direction.NORTH ? .25f : fl == Direction.SOUTH ? .25f : 0;
			float minZ = fw == Direction.EAST ? 0 : fw == Direction.WEST ? 0 : .25f;
			float maxX = fl == Direction.NORTH ? .75f : fl == Direction.SOUTH ? .75f : 1;
			float maxZ = fw == Direction.EAST ? 1 : fw == Direction.WEST ? 1 : .75f;
			List <AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(minX, 0, minZ, maxX, .5f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			minX = fl == Direction.NORTH ? .375f : fl == Direction.SOUTH ? .375f : .125f;
			minZ = fw == Direction.EAST ? .125f : fw == Direction.WEST ? .125f : .375F;
			maxX = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .625f : .375f;
			maxZ = fw == Direction.EAST ? .375f : fw == Direction.WEST ? .375f : .625f;
			list.add(new AxisAlignedBB(minX, .5f, minZ, maxX, 1, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			minX = fl == Direction.NORTH ? .375f : fl == Direction.SOUTH ? .375f : .625f;
			minZ = fw == Direction.EAST ? .625f : fw == Direction.WEST ? .625f : .375F;
			maxX = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .625f : .875f;
			maxZ = fw == Direction.EAST ? .875f : fw == Direction.WEST ? .875f : .625f;
			list.add(new AxisAlignedBB(minX, .5f, minZ, maxX, 1, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			if(pos == 8) fw = fw.getOpposite();
			minX = fw == Direction.EAST ? .75f : 0;
			maxX = fw == Direction.WEST ? .25f : 1;
			minZ = fw == Direction.SOUTH ? .75f : 0;
			maxZ = fw == Direction.NORTH ? .25f : 1;
			list.add(new AxisAlignedBB(minX, .25f, minZ, maxX, 1, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			if(pos == 8) fl = fl.getOpposite();
			minX = fl == Direction.NORTH ? .75f : fl == Direction.SOUTH ? 0 : fl == Direction.EAST ? .125f : .625f;
			maxX = fl == Direction.NORTH ? 1 : fl == Direction.SOUTH ? .25f : fl == Direction.EAST ? .375f : .875f;
			minZ = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .125f : fl == Direction.EAST ? .75f : 0;
			maxZ = fl == Direction.NORTH ? .875f : fl == Direction.SOUTH ? .375f : fl == Direction.EAST ? 1 : .25f;
			list.add(new AxisAlignedBB(minX, .125f, minZ, maxX, .25f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			minX = fl == Direction.NORTH ? .75f : fl == Direction.SOUTH ? 0 : fl == Direction.EAST ? .625f : .125f;
			maxX = fl == Direction.NORTH ? 1 : fl == Direction.SOUTH ? .25f : fl == Direction.EAST ? .875f : .375f;
			minZ = fl == Direction.NORTH ? .125f : fl == Direction.SOUTH ? .625f : fl == Direction.EAST ? .75f : 0;
			maxZ = fl == Direction.NORTH ? .375f : fl == Direction.SOUTH ? .875f : fl == Direction.EAST ? 1 : .25f;
			list.add(new AxisAlignedBB(minX, .125f, minZ, maxX, .25f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			return list;
		}
		if(pos == 9 || pos == 11) {
			float minX = fl == Direction.NORTH ? .375f : fl == Direction.SOUTH ? .375f : .125f;
			float minZ = fw == Direction.EAST ? .125f : fw == Direction.WEST ? .125f : .375F;
			float maxX = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .625f : .375f;
			float maxZ = fw == Direction.EAST ? .375f : fw == Direction.WEST ? .375f : .625f;
			List <AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(minX, 0, minZ, maxX, 1, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			minX = fl == Direction.NORTH ? .375f : fl == Direction.SOUTH ? .375f : .625f;
			minZ = fw == Direction.EAST ? .625f : fw == Direction.WEST ? .625f : .375F;
			maxX = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .625f : .875f;
			maxZ = fw == Direction.EAST ? .875f : fw == Direction.WEST ? .875f : .625f;
			list.add(new AxisAlignedBB(minX, 0, minZ, maxX, 1, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			list.add(new AxisAlignedBB(.375f, .375f, .375f, .625f, .625f, .625f).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			if(pos == 11) fw = fw.getOpposite();
			minX = fw == Direction.EAST ? .75f : 0;
			maxX = fw == Direction.WEST ? .25f : 1;
			minZ = fw == Direction.SOUTH ? .75f : 0;
			maxZ = fw == Direction.NORTH ? .25f : 1;
			list.add(new AxisAlignedBB(minX, 0, minZ, maxX, 1, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			if(pos == 11) fl = fl.getOpposite();
			minX = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .25f : fl == Direction.EAST ? .125f : .625f;
			maxX = fl == Direction.NORTH ? .75f : fl == Direction.SOUTH ? .375f : fl == Direction.EAST ? .375f : .875f;
			minZ = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .125f : fl == Direction.EAST ? .625f : .25f;
			maxZ = fl == Direction.NORTH ? .875f : fl == Direction.SOUTH ? .375f : fl == Direction.EAST ? .75f : .375f;
			list.add(new AxisAlignedBB(minX, .375f, minZ, maxX, .625f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			minX = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .25f : fl == Direction.EAST ? .625f : .125f;
			maxX = fl == Direction.NORTH ? .75f : fl == Direction.SOUTH ? .375f : fl == Direction.EAST ? .875f : .375f;
			minZ = fl == Direction.NORTH ? .125f : fl == Direction.SOUTH ? .625f : fl == Direction.EAST ? .625f : .25f;
			maxZ = fl == Direction.NORTH ? .375f : fl == Direction.SOUTH ? .875f : fl == Direction.EAST ? .75f : .375f;
			list.add(new AxisAlignedBB(minX, .375f, minZ, maxX, .625f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			return list;
		}
		if(pos == 12 || pos == 14) {
			float minX = fl == Direction.NORTH ? .25f : fl == Direction.SOUTH ? .25f : 0;
			float minZ = fw == Direction.EAST ? 0 : fw == Direction.WEST ? 0 : .25f;
			float maxX = fl == Direction.NORTH ? .75f : fl == Direction.SOUTH ? .75f : 1;
			float maxZ = fw == Direction.EAST ? 1 : fw == Direction.WEST ? 1 : .75f;
			List <AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(minX, .5f, minZ, maxX, 1, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			minX = fl == Direction.NORTH ? .375f : fl == Direction.SOUTH ? .375f : .125f;
			minZ = fw == Direction.EAST ? .125f : fw == Direction.WEST ? .125f : .375F;
			maxX = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .625f : .375f;
			maxZ = fw == Direction.EAST ? .375f : fw == Direction.WEST ? .375f : .625f;
			list.add(new AxisAlignedBB(minX, 0, minZ, maxX, .5f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			minX = fl == Direction.NORTH ? .375f : fl == Direction.SOUTH ? .375f : .625f;
			minZ = fw == Direction.EAST ? .625f : fw == Direction.WEST ? .625f : .375F;
			maxX = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .625f : .875f;
			maxZ = fw == Direction.EAST ? .875f : fw == Direction.WEST ? .875f : .625f;
			list.add(new AxisAlignedBB(minX, 0, minZ, maxX, .5f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			if(pos == 14) fw = fw.getOpposite();
			minX = fw == Direction.EAST ? .75f : 0;
			maxX = fw == Direction.WEST ? .25f : 1;
			minZ = fw == Direction.SOUTH ? .75f : 0;
			maxZ = fw == Direction.NORTH ? .25f : 1;
			list.add(new AxisAlignedBB(minX, 0, minZ, maxX, .5f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			if(pos == 14) fl = fl.getOpposite();
			minX = fl == Direction.NORTH ? .75f : fl == Direction.SOUTH ? 0 : fl == Direction.EAST ? .125f : .625f;
			maxX = fl == Direction.NORTH ? 1 : fl == Direction.SOUTH ? .25f : fl == Direction.EAST ? .375f : .875f;
			minZ = fl == Direction.NORTH ? .625f : fl == Direction.SOUTH ? .125f : fl == Direction.EAST ? .75f : 0;
			maxZ = fl == Direction.NORTH ? .875f : fl == Direction.SOUTH ? .375f : fl == Direction.EAST ? 1 : .25f;
			list.add(new AxisAlignedBB(minX, .625f, minZ, maxX, .875f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			minX = fl == Direction.NORTH ? .75f : fl == Direction.SOUTH ? 0 : fl == Direction.EAST ? .625f : .125f;
			maxX = fl == Direction.NORTH ? 1 : fl == Direction.SOUTH ? .25f : fl == Direction.EAST ? .875f : .375f;
			minZ = fl == Direction.NORTH ? .125f : fl == Direction.SOUTH ? .625f : fl == Direction.EAST ? .75f : 0;
			maxZ = fl == Direction.NORTH ? .375f : fl == Direction.SOUTH ? .875f : fl == Direction.EAST ? 1 : .25f;
			list.add(new AxisAlignedBB(minX, .625f, minZ, maxX, .875f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			return list;
		}
		if(pos == 7) {
			List <AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(0, .25f, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			float minX = fl == Direction.EAST ? .125f : fl == Direction.WEST ? .125f : 0;
			float minZ = fw == Direction.EAST ? .125f : fw == Direction.WEST ? .125f : 0;
			float maxX = fl == Direction.EAST ? .375f : fl == Direction.WEST ? .375f : 1;
			float maxZ = fw == Direction.EAST ? .375f : fw == Direction.WEST ? .375f : 1;
			list.add(new AxisAlignedBB(minX, .125f, minZ, maxX, .25f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			minX = fl == Direction.EAST ? .625f : fl == Direction.WEST ? .625f : 0;
			minZ = fw == Direction.EAST ? .625f : fw == Direction.WEST ? .625f : 0;
			maxX = fl == Direction.EAST ? .875f : fl == Direction.WEST ? .875f : 1;
			maxZ = fw == Direction.EAST ? .875f : fw == Direction.WEST ? .875f : 1;
			list.add(new AxisAlignedBB(minX, .125f, minZ, maxX, .25f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			return list;
		}
		if(pos == 10) {
			float minX = fl == Direction.EAST ? .125f : fl == Direction.WEST ? .125f : 0;
			float minZ = fw == Direction.EAST ? .125f : fw == Direction.WEST ? .125f : 0;
			float maxX = fl == Direction.EAST ? .875f : fl == Direction.WEST ? .875f : 1;
			float maxZ = fw == Direction.EAST ? .875f : fw == Direction.WEST ? .875f : 1;
			return Lists.newArrayList(new AxisAlignedBB(minX, 0, minZ, maxX, 1, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
		}
		if(pos == 13) {
			List <AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(0, .25f, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			float minX = fl == Direction.EAST ? .125f : fl == Direction.WEST ? .125f : 0;
			float minZ = fw == Direction.EAST ? .125f : fw == Direction.WEST ? .125f : 0;
			float maxX = fl == Direction.EAST ? .875f : fl == Direction.WEST ? .875f : 1;
			float maxZ = fw == Direction.EAST ? .875f : fw == Direction.WEST ? .875f : 1;
			list.add(new AxisAlignedBB(minX, 0, minZ, maxX, .25f, maxZ).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
			return list;
		}
		return null;
	}

	@Override
	public boolean isOverrideBox(AxisAlignedBB box, PlayerEntity player, RayTraceResult mop, ArrayList<AxisAlignedBB> list) {
		return false;
	}

}