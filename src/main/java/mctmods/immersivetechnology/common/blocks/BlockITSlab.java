package mctmods.immersivetechnology.common.blocks;

import mctmods.immersivetechnology.common.tileentities.TileEntityITSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.BlockFaceShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockITSlab<E extends Enum<E> & BlockITBase.IBlockEnum> extends BlockITTileProvider<E> {
	public static final PropertyInteger prop_SlabType = PropertyInteger.create("slabtype", 0, 2);

	public BlockITSlab(String name, Material material, PropertyEnum<E> property) {
		super(name, material, property, ItemBlockITSlabs.class, prop_SlabType);
		this.setAllNotNormalBlock();
		this.useNeighborBrightness = true;
	}

	@Override
	public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos) {
		state = super.getActualState(state, world, pos);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityITSlab) return state.withProperty(prop_SlabType, ((TileEntityITSlab)tile).slabType);
		return state;
	}

	@Override
	public TileEntity createBasicTE(World worldIn, E meta) {
		return new TileEntityITSlab();
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
	}

	@Override
	public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity tile, ItemStack stack) {
		if(tile instanceof TileEntityITSlab && !player.capabilities.isCreativeMode) {
			spawnAsEntity(world, pos, new ItemStack(this, ((TileEntityITSlab)tile).slabType == 2 ? 2 : 1, this.getMetaFromState(state)));
			return;
		}
		super.harvestBlock(world, player, pos, state, tile, stack);
	}

	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityITSlab) {
			int type = ((TileEntityITSlab)te).slabType;
			if(type  ==  0)	return side == Direction.DOWN;
			else if(type == 1)
				return side == Direction.UP;
		}
		return true;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, BlockState state, BlockPos pos, Direction side) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityITSlab) {
			int type = ((TileEntityITSlab)te).slabType;
			if(type == 2) return BlockFaceShape.SOLID;
			else if((type == 0 && side == Direction.DOWN) || (type == 1 && side == Direction.UP)) return BlockFaceShape.SOLID;
			else return BlockFaceShape.UNDEFINED;
		}
		return BlockFaceShape.SOLID;
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityITSlab) {
			int type = ((TileEntityITSlab)te).slabType;
			if(type == 0) return new AxisAlignedBB(0, 0, 0, 1, .5f, 1);
			else if(type == 1) return new AxisAlignedBB(0, .5f, 0, 1, 1, 1);
			else return FULL_BLOCK_AABB;
		}
		else return new AxisAlignedBB(0, 0, 0, 1, .5f, 1);
	}

}