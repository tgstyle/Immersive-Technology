package mctmods.immersivetechnology.common.gui;

import mctmods.immersivetechnology.common.blocks.connectors.tileentities.TileEntityTimer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerTimer extends Container {
	TileEntityTimer tile;

	public ContainerTimer(PlayerInventory playerInventory, TileEntityTimer tile) {
		this.tile=tile;

		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 85 + i * 18));
			}
		}
		for(int i = 0; i < 9; i++) addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 143));
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return tile != null && tile.getWorld().getTileEntity(tile.getPos()) == tile && player.getDistanceSq(tile.getPos().getX() + .5, tile.getPos().getY() + .5, tile.getPos().getZ() + .5) <= 64;
	}

}