package mctmods.immersivetechnology.common.util.fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.ITContent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.StateHolder;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class ITFluid extends FlowingFluid {

	public static final List<ITFluid> FLUIDS = new ArrayList<>();
	protected final String fluidName;
	protected final ResourceLocation stillTexture;
	protected final ResourceLocation flowingTexture;
	protected ITFluid source;
	protected ITFluid flowing;
	public Block block;
	protected Item bucket;
	@Nullable
	protected final Consumer<FluidAttributes.Builder> buildAttributes;

	public ITFluid(String name, int density, int viscosity) {
		this(name,
				new ResourceLocation(ImmersiveTechnology.MODID, "block/fluid/" + name + "_still"),
				new ResourceLocation(ImmersiveTechnology.MODID, "block/fluid/" + name + "_flow"), ITFluid.createBuilder(density, viscosity));
	}

	protected ITFluid(String name, ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable Consumer<FluidAttributes.Builder> buildAttributes) {
		this(name, stillTexture, flowingTexture, buildAttributes, true);
	}

	protected ITFluid(String name, ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable Consumer<FluidAttributes.Builder> buildAttributes, boolean isSource) {
		this.fluidName = name;
		this.stillTexture = stillTexture;
		this.flowingTexture = flowingTexture;
		this.buildAttributes = buildAttributes;
		ITContent.registeredITFluids.add(this);
		if(!isSource) {
			flowing = this;
			setRegistryName(ImmersiveTechnology.MODID, this.fluidName + "_flowing");
		} else {
			this.source = this;
			this.block = createFluidBlock();
			this.bucket = createBucketItem();
			this.flowing = createFlowingFluid();
			setRegistryName(new ResourceLocation(ImmersiveTechnology.MODID, this.fluidName));
			FLUIDS.add(this);
			ITContent.registeredITBlocks.add(this.block);
			ITContent.registeredITItems.add(this.bucket);
		}
	}

	protected ITFluidFlowing createFlowingFluid() {
		return new ITFluidFlowing(this);
	}

	protected ITFluidBlock createFluidBlock() {
		return new ITFluidBlock(this.source, this.fluidName);
	}

	protected ITBucketItem createBucketItem() {
		return new ITBucketItem(this.source, this.fluidName);
	}

	@Override
	protected FluidAttributes createAttributes() {
		FluidAttributes.Builder builder = FluidAttributes.builder(this.stillTexture, this.flowingTexture);
		if(this.buildAttributes != null) this.buildAttributes.accept(builder);
		return builder.build(this);
	}

	@Override
	protected void beforeReplacingBlock(IWorld arg0, BlockPos arg1, BlockState arg2) {
	}

	@Override
	protected boolean canSourcesMultiply() {
		return false;
	}

	@Override
	public Fluid getFlowingFluid() {
		return this.flowing;
	}

	@Override
	public Fluid getStillFluid() {
		return this.source;
	}

	@Override
	public Item getFilledBucket() {
		return this.bucket;
	}

	@Override
	protected int getLevelDecreasePerBlock(IWorldReader arg0) {
		return 1;
	}

	@Override
	protected int getSlopeFindDistance(IWorldReader arg0) {
		return 4;
	}

	@Override
	protected boolean canDisplace(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
		return p_215665_5_ == Direction.DOWN && !isEquivalentTo(p_215665_4_);
	}

	@Override
	public int getTickRate(IWorldReader p_205569_1_) {
		return 5;
	}

	@Override
	protected float getExplosionResistance() {
		return 100;
	}

	@Override
	protected BlockState getBlockState(FluidState state) {
		return this.block.getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state));
	}

	@Override
	public boolean isSource(FluidState state) {
		return state.getFluid() == this.source;
	}

	@Override
	public int getLevel(FluidState state) {
		return isSource(state) ? 8 : state.get(LEVEL_1_8);
	}

	@Override
	public boolean isEquivalentTo(Fluid fluidIn) {
		return fluidIn == this.source || fluidIn == this.flowing;
	}


	public static Consumer<FluidAttributes.Builder> createBuilder(int density, int viscosity) {
		return builder -> builder.viscosity(viscosity).density(density);
	}

	public static class ITFluidBlock extends FlowingFluidBlock {
		private static ITFluid tmp = null;

		private ITFluid fluid;
		public ITFluidBlock(ITFluid fluid, String fluidName) {
			super(supplier(fluid), AbstractBlock.Properties.create(Material.WATER));
			this.fluid = fluid;
			setRegistryName(new ResourceLocation(ImmersiveTechnology.MODID, fluidName + "_fluid_block"));
		}

		@Override
		protected void fillStateContainer(Builder<Block, BlockState> builder) {
			super.fillStateContainer(builder);
			ITFluid fluid = this.fluid != null ? this.fluid : tmp;
			builder.add(fluid.getStateContainer().getProperties().toArray(new Property[0]));
		}

		@Override
		public FluidState getFluidState(BlockState state) {
			FluidState baseState = super.getFluidState(state);
			for(Property<?> property:this.fluid.getStateContainer().getProperties())
				if(property != FlowingFluidBlock.LEVEL) baseState = withCopiedValue(property, baseState, state);
			return baseState;
		}

		private <T extends StateHolder<?, T>, S extends Comparable<S>> T withCopiedValue(Property<S> prop, T oldState, StateHolder<?, ?> copyFrom) {
			return oldState.with(prop, copyFrom.get(prop));
		}

		private static Supplier<ITFluid> supplier(ITFluid fluid) {
			tmp = fluid;
			return () -> fluid;
		}
	}

	public static class ITBucketItem extends BucketItem {
		private static final Item.Properties PROPS = new Item.Properties().maxStackSize(1).group(ImmersiveTechnology.creativeTab);

		public ITBucketItem(ITFluid fluid, String fluidName) {
			super(() -> fluid, PROPS);
			setRegistryName(new ResourceLocation(ImmersiveTechnology.MODID, fluidName + "_bucket"));
		}

		@Override
		public ItemStack getContainerItem(ItemStack itemStack) {
			return new ItemStack(Items.BUCKET);
		}

		@Override
		public boolean hasContainerItem(ItemStack stack) {
			return true;
		}

		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
			return new FluidBucketWrapper(stack);
		}
	}

	public static class ITFluidFlowing extends ITFluid {
		public ITFluidFlowing(ITFluid source) {
			super(source.fluidName, source.stillTexture, source.flowingTexture, source.buildAttributes, false);
			this.source = source;
			this.bucket = source.bucket;
			this.block = source.block;
			setDefaultState(this.getStateContainer().getBaseState().with(LEVEL_1_8, 7));
		}

		@Override
		protected void fillStateContainer(Builder<Fluid, FluidState> builder) {
			super.fillStateContainer(builder);
			builder.add(LEVEL_1_8);
		}
	}

}
