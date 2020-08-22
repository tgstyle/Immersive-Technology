package mctmods.immersivetechnology.common.tileentities;

import blusunrize.immersiveengineering.common.blocks.TileEntityIEBase;
import net.minecraft.nbt.CompoundNBT;

public class TileEntityITSlab extends TileEntityIEBase {
	public int slabType = 0;

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
		slabType = nbt.getInt("slabType");
		if(descPacket && world != null) this.markContainingBlockForUpdate(null);
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		nbt.putInt("slabType", slabType);
	}

}