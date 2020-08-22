package mctmods.immersivetechnology.common.blocks.metal;

import mctmods.immersivetechnology.common.blocks.BlockITTileProvider;
import mctmods.immersivetechnology.common.blocks.ItemBlockITBase;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntityTrashEnergy;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntityTrashFluid;
import mctmods.immersivetechnology.common.blocks.metal.tileentities.TileEntityTrashItem;
import mctmods.immersivetechnology.common.blocks.metal.types.BlockType_MetalTrash;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMetalTrash extends BlockITTileProvider<BlockType_MetalTrash> {
	public BlockMetalTrash() {
		super("metal_trash", Material.IRON, PropertyEnum.create("type", BlockType_MetalTrash.class), ItemBlockITBase.class);
		this.setHardness(3.0F);
		this.setResistance(15.0F);
		lightOpacity = 0;
		this.setAllNotNormalBlock();
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos) {
		state = super.getExtendedState(state, world, pos);
		return state;
	}

	@Override
	public boolean allowHammerHarvest(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createBasicTE(World worldIn, BlockType_MetalTrash type) {
		switch(type) {
		case TRASH_ITEM:
			return new TileEntityTrashItem();
		case TRASH_FLUID:
			return new TileEntityTrashFluid();
		case TRASH_ENERGY:
			return new TileEntityTrashEnergy();
		default:
			break;
		}
		return null;
	}

}