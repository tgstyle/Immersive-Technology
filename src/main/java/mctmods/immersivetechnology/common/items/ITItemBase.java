package mctmods.immersivetechnology.common.items;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces.IColouredItem;
import mctmods.immersivetechnology.ImmersiveTechnology;
import mctmods.immersivetechnology.common.ITContent;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ITItemBase extends Item implements IColouredItem {

	public ITItemBase(String name) {
		this(name, new Item.Properties());
	}

	public ITItemBase(String name, Item.Properties properties) {
		super(properties.group(ImmersiveTechnology.creativeTab));
		setRegistryName(new ResourceLocation(ImmersiveTechnology.MODID, name));
		ITContent.registeredITItems.add(this);
	}

}
