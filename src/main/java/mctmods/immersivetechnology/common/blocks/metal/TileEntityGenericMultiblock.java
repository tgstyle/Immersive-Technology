package mctmods.immersivetechnology.common.blocks.metal;

import blusunrize.immersiveengineering.api.MultiblockHandler;
import blusunrize.immersiveengineering.api.crafting.IMultiblockRecipe;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityMultiblockMetal;
import blusunrize.immersiveengineering.common.util.Utils;
import mctmods.immersivetechnology.api.ITProperties;
import mctmods.immersivetechnology.api.ITUtils;
import mctmods.immersivetechnology.client.render.IMultiblockRender;
import mctmods.immersivetechnology.common.util.multiblock.IMultipart;
import mctmods.immersivetechnology.common.util.multiblock.ITMultiblock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class TileEntityGenericMultiblock<T extends TileEntityGenericMultiblock<T, R, M>, R extends IMultiblockRecipe, M extends T> extends TileEntityMultiblockMetal<T,R> implements IMultiblockRender {

	public TileEntityGenericMultiblock(MultiblockHandler.IMultiblock instance, int[] structureDimensions, int energyCapacity, boolean redstoneControl) {
		super(instance, structureDimensions, energyCapacity, redstoneControl);
	}

	public TileEntityGenericMultiblock(ITMultiblock<?> instance, int energyCapacity, boolean redstoneControl) {
		super(instance, new int[] { instance.height, instance.length, instance.width }, energyCapacity, redstoneControl);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public T getTileForPos(int targetPos) {
		BlockPos target = getBlockPosForPos(targetPos);
		TileEntity tile = Utils.getExistingTileEntity(world, target);
		if(tile instanceof TileEntityGenericMultiblock && tile.getClass().isInstance(this))
			return (T)tile;
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if(capability== CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY&&this.getAccessibleFluidTanks(facing).length > 0)
			return true;
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <C> C getCapability(Capability<C> capability, @Nullable EnumFacing facing) {
		if(capability==CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY&&this.getAccessibleFluidTanks(facing).length > 0)
			return (C)new MultiblockFluidWrapper(this, facing);
		return super.getCapability(capability, facing);
	}

	public M master;

	@Nullable
	@Override
	public M master() {
		if (!isDummy()) return master = (M)this;
		if(master != null && !master.tileEntityInvalid) return master;
		BlockPos masterPos = getPos().add(-offset[0], -offset[1], -offset[2]);
		TileEntity te = Utils.getExistingTileEntity(world, masterPos);
		if (te != null) master = (M)te;
		return master;
	}

	@Override
	protected IFluidTank[] getAccessibleFluidTanks(EnumFacing side) {
		if(master() == null) return ITUtils.emptyIFluidTankList;
		return master.getAccessibleFluidTanks(side, pos);
	}

	@Override
	protected boolean canFillTankFrom(int iTank, EnumFacing side, FluidStack resource) {
		if(master() == null || side == null) return false;
		return master.canFillTankFrom(iTank, side, resource, pos);
	}

	@Override
	protected boolean canDrainTankFrom(int iTank, EnumFacing side) {
		if(master() == null || side == null) return false;
		return master.canDrainTankFrom(iTank, side, pos);
	}

	protected IFluidTank[] getAccessibleFluidTanks(EnumFacing side, int pos) {
		return ITUtils.emptyIFluidTankList;
	}

	protected boolean canFillTankFrom(int iTank, EnumFacing side, FluidStack resource, int pos) {
		return false;
	}

	protected boolean canDrainTankFrom(int iTank, EnumFacing side, int pos) {
		return false;
	}

	@Override
	public boolean isDummy() {
		return true;
	}

	public void efficientMarkDirty() { // !!!!!!! only use it within update() function !!!!!!!
		world.getChunkFromBlockCoords(this.getPos()).markDirty();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onLoad() {
		if (!isDummy() && world.isRemote) registerRenderer();
		super.onLoad();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderAABB() {
		if (isDummy()) return null;
		return new AxisAlignedBB(getBlockPosForPos(0), getBlockPosForPos(structureDimensions[0]*structureDimensions[1]*structureDimensions[2]-1)).grow(1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
		IBlockState state = world.getBlockState(getPos());
		return state.getBlock().canRenderInLayer(state, layer);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IBlockState renderState() {
		IBlockState state = world.getBlockState(getPos());
		if (state == null || !state.getPropertyKeys().contains(ITProperties.RENDER)) return null;
		return state.withProperty(ITProperties.RENDER, isDummy()? ITProperties.Render_Type.HIDDEN : mirrored? ITProperties.Render_Type.MIRRORED : ITProperties.Render_Type.NORMAL);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getLightLevel() {
		Chunk chunk = world.getChunkFromBlockCoords(getPos());
		int block = chunk.getLightFor(EnumSkyBlock.BLOCK, getPos());
		int sky = chunk.getLightFor(EnumSkyBlock.SKY, getPos()) - world.calculateSkylightSubtracted(1f);
		return Math.max(block,sky);
	}

	@Override
	public BlockPos getBlockPos() {
		return getPos();
	}

	@Override
	public TileEntityMultiblockPart<?> This() {
		return this;
	}
}