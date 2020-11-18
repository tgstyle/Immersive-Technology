package mctmods.immersivetechnology.common.blocks;

import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.ITContent;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ITBlockBase extends Block {

	public ITBlockBase(String name, Block.Properties props) {
		super(props);
		setRegistryName(new ResourceLocation(ImmersiveTechnology.MODID, name));
		ITContent.registeredITBlocks.add(this);
		BlockItem blockItem=createBlockItem();
		if(blockItem!=null) ITContent.registeredITItems.add(blockItem.setRegistryName(getRegistryName()));
	}

	protected BlockItem createBlockItem() {
		return new ITBlockItemBase(this, new Item.Properties().group(ImmersiveTechnology.creativeTab));
	}

}
