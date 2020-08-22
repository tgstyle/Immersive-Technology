package mctmods.immersivetechnology.common.blocks.metal.tileentities;

import blusunrize.immersiveengineering.api.IEEnums.SideConfig;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHasDummyBlocks;
import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import blusunrize.immersiveengineering.common.util.EnergyHelper.IEForgeEnergyWrapper;
import blusunrize.immersiveengineering.common.util.EnergyHelper.IIEInternalFluxHandler;
import mctmods.immersivetechnology.common.Config.ITConfig.Machines.CokeOvenPreheater;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class TileEntityCokeOvenPreheater extends TileEntityIEBase implements IIEInternalFluxHandler, IDirectionalTile, IHasDummyBlocks {
	public Direction facing = Direction.NORTH;

	private static int cokeOvenConsumption = CokeOvenPreheater.cokeOvenPreheater_energy_consumption;

	public FluxStorage energyStorage = new FluxStorage(8000);

	public boolean dummy = false;
	public boolean active = false;

	public BlockPos masterPos;

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
		dummy = nbt.getBoolean("dummy");
		facing = Direction.byIndex(nbt.getInt("facing"));
		energyStorage.readFromNBT(nbt);
		active = nbt.getBoolean("active");
		if(descPacket) this.markContainingBlockForUpdate(null);
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		nbt.putBoolean("dummy", dummy);
		nbt.putInt("facing", facing.ordinal());
		nbt.putBoolean("active", active);
		energyStorage.writeToNBT(nbt);
	}
	
	public boolean doSpeedup() {
		if(dummy) return false;
		int consumed = cokeOvenConsumption;
		if(this.energyStorage.extractEnergy(consumed, true) == consumed) {
			if(!active) {
				active = true;
				this.markContainingBlockForUpdate(null);
			}
			this.energyStorage.extractEnergy(consumed, false);
			return true;
		} else if(active) {
			active = false;
			this.markContainingBlockForUpdate(null);
		}
		return false;
	}

	@Nonnull
	@Override
	public SideConfig getEnergySideConfig(Direction facing) {
		return !dummy && facing == Direction.UP ? SideConfig.INPUT : SideConfig.NONE;
	}

	IEForgeEnergyWrapper wrapper = new IEForgeEnergyWrapper(this, Direction.UP);

	@Override
	public IEForgeEnergyWrapper getCapabilityWrapper(Direction facing) {
		if(!dummy && facing == Direction.UP) {
			return wrapper;
		}
		return null;
	}

	@Override
	public void placeDummies(BlockPos pos, BlockState state, Direction side, float hitX, float hitY, float hitZ) {
		BlockPos dummyPos = pos.offset(facing.rotateY());
		world.setBlockState(dummyPos, state);
		TileEntityCokeOvenPreheater dummyTE = (TileEntityCokeOvenPreheater) world.getTileEntity(dummyPos);
		dummyTE.dummy = true;
		dummyTE.facing = facing.rotateY();

		dummyPos = pos.offset(facing.rotateYCCW());
		world.setBlockState(dummyPos, state);
		dummyTE = (TileEntityCokeOvenPreheater) world.getTileEntity(dummyPos);
		dummyTE.dummy = true;
		dummyTE.facing = facing.rotateYCCW();
	}

	@Override
	public void breakDummies(BlockPos unused, BlockState unused2) {
		if(dummy) {
			if(masterPos == null) findMaster();
			TileEntity tile = world.getTileEntity(masterPos);
			if(tile instanceof TileEntityCokeOvenPreheater) ((TileEntityCokeOvenPreheater) tile).breakDummies(null, null);
		} else {
			BlockPos dummyPos0 = getPos().offset(facing.rotateY());
			TileEntityCokeOvenPreheater dummy0 = (TileEntityCokeOvenPreheater)world.getTileEntity(dummyPos0);
			BlockPos dummyPos1 = getPos().offset(facing.rotateYCCW());
			TileEntityCokeOvenPreheater dummy1 = (TileEntityCokeOvenPreheater)world.getTileEntity(dummyPos1);
			if(dummy0 != null) world.setBlockToAir(dummyPos0);
			if(dummy1 != null) world.setBlockToAir(dummyPos1);
			world.setBlockToAir(getPos());
		}
	}

	@Override
	public boolean isDummy() {
		return dummy;
	}

	@Override
	public Direction getFacing() {
		return facing;
	}

	@Override
	public void setFacing(Direction facing) {
		this.facing = facing;
	}

	@Override
	public int getFacingLimitation() {
		return 2;
	}

	@Override
	public boolean mirrorFacingOnPlacement(EntityLivingBase placer) {
		return false;
	}

	@Override
	public boolean canHammerRotate(Direction side, float hitX, float hitY, float hitZ, EntityLivingBase entity) {
		return false;
	}

	@Override
	public boolean canRotate(Direction axis) {
		return false;
	}

	private void findMaster() {
		if(!dummy) {
			masterPos = getPos();
			return;
		}
		TileEntity tile = world.getTileEntity(getPos().offset(facing, -1));
		if(tile instanceof TileEntityCokeOvenPreheater && ((TileEntityCokeOvenPreheater) tile).isMaster(this)) {
			masterPos = getPos().offset(facing, -1);
			return;
		}
		tile = world.getTileEntity(getPos().offset(facing.rotateY()));
		if(tile instanceof TileEntityCokeOvenPreheater && ((TileEntityCokeOvenPreheater) tile).isMaster(this)) { //DUMB SHIT PART 1
			masterPos = getPos().offset(facing.rotateY());
			facing = facing.rotateYCCW();
			this.markContainingBlockForUpdate(null);
			return;
		}
		tile = world.getTileEntity(getPos().offset(facing.rotateYCCW()));
		if(tile instanceof TileEntityCokeOvenPreheater && ((TileEntityCokeOvenPreheater) tile).isMaster(this)) { //DUMB SHIT PART 2
			masterPos = getPos().offset(facing.rotateYCCW());
			facing = facing.rotateY();
			this.markContainingBlockForUpdate(null);
			return;
		}
		masterPos = getPos();
	}

	private boolean isMaster(TileEntityCokeOvenPreheater requester) {
		if(dummy || requester == null) return false;
		BlockPos dummyPos = getPos().offset(facing.rotateY());
		if(requester == world.getTileEntity(dummyPos)) return true;
		dummyPos = getPos().offset(facing.rotateYCCW());
		return (requester == world.getTileEntity(dummyPos));
	}

	@Override
	public FluxStorage getFluxStorage() {
		if(dummy) {
			if(masterPos == null) findMaster();
			TileEntity tile = world.getTileEntity(masterPos);
			if(tile instanceof TileEntityCokeOvenPreheater) return ((TileEntityCokeOvenPreheater)tile).getFluxStorage();
		}
		return energyStorage;
	}

}