package mctmods.immersivetechnology.api;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class ITTags {

	public static class Blocks {
		public static final ITag.INamedTag<Block> fluidValve=createBlockWrapper(forgeLoc("fluidvalve"));
	}

	public static class Items {
		public static final ITag.INamedTag<Item> salt=createItemWrapper(forgeLoc("salt"));
	}

	public static class Fluids {
		public static final ITag.INamedTag<Fluid> steam = createFluidWrapper(forgeLoc("steam"));
	}

	private static ITag.INamedTag<Item> createItemWrapper(ResourceLocation name) {
		return createGenericWrapper(ItemTags.getAllTags(), name, ItemTags::makeWrapperTag);
	}

	private static ITag.INamedTag<Block> createBlockWrapper(ResourceLocation name) {
		return createGenericWrapper(BlockTags.getAllTags(), name, BlockTags::makeWrapperTag);
	}

	private static ITag.INamedTag<Fluid> createFluidWrapper(ResourceLocation name) {
		return createGenericWrapper(FluidTags.getAllTags(), name, FluidTags::makeWrapperTag);
	}

	private static <T> INamedTag<T> createGenericWrapper(List<? extends INamedTag<T>> tags, ResourceLocation name, Function<String, INamedTag<T>> createNew) {
		Optional<? extends INamedTag<T>> existing = tags.stream().filter(tag -> tag.getName().equals(name)).findAny();
		if(existing.isPresent()) {
			return existing.get();
		} else {
			return createNew.apply(name.toString());
		}
	}

	private static ResourceLocation forgeLoc(String path) {
		return new ResourceLocation("forge", path);
	}

}
