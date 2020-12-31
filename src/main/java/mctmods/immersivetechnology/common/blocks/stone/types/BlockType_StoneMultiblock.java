package mctmods.immersivetechnology.common.blocks.stone.types;

import mctmods.immersivetechnology.api.ITUtils;
import mctmods.immersivetechnology.common.blocks.BlockITBase;
import mctmods.immersivetechnology.common.blocks.stone.tileentities.TileEntityCokeOvenAdvancedMaster;
import mctmods.immersivetechnology.common.blocks.stone.tileentities.TileEntityCokeOvenAdvancedSlave;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;

import java.util.Locale;
import java.util.function.Function;

public enum BlockType_StoneMultiblock implements IStringSerializable, BlockITBase.IBlockEnum {
	COKE_OVEN_ADVANCED(ITUtils.appendModName("multiblocks/coke_oven_advanced"), x -> new TileEntityCokeOvenAdvancedMaster()),
	COKE_OVEN_ADVANCED_SLAVE(ITUtils.appendModName("multiblocks/coke_oven_advanced"), x -> new TileEntityCokeOvenAdvancedSlave());

	private final String path;
	private final Function<?, TileEntity> func;

	BlockType_StoneMultiblock(String path, Function<?, TileEntity> func) {
		this.path = path;
		this.func = func;
	}

	public TileEntity createTE() {
		return func.apply(null);
	}

	@Override
	public String getName() {
		return this.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	public int getMeta() {
		return ordinal();
	}

	@Override
	public boolean listForCreative() {
		return false;
	}

	@Override
	public String getPath() {
		return path;
	}

}