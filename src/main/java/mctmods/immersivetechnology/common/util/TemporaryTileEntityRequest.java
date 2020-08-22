package mctmods.immersivetechnology.common.util;

import blusunrize.immersiveengineering.api.MultiblockHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TemporaryTileEntityRequest {

	public BlockPos position;
	public Direction facing;
	public CompoundNBT nbtTag;
	public World world;
	public MultiblockHandler.IMultiblock multiblock;
	public BlockPos formationPosition;

}