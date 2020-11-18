package mctmods.immersivetechnology.common.blocks;

import java.util.function.Supplier;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import mctmods.immersivetechnology.ImmersiveTechnology;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public class ITMetalMultiblock<T extends MultiblockPartTileEntity<T>> extends MetalMultiblockBlock<T> {

	public ITMetalMultiblock(String name, Supplier<TileEntityType<T>> tileEntity, Property<?>... additionalProperties) {
		super(name, tileEntity, additionalProperties);
	}

	@Override
	public ResourceLocation createRegistryName() {
		return new ResourceLocation(ImmersiveTechnology.MODID, name);
	}

}
