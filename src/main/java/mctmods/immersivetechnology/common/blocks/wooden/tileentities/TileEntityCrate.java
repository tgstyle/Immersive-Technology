package mctmods.immersivetechnology.common.blocks.wooden.tileentities;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IPlayerInteraction;
import mctmods.immersivetechnology.common.tileentities.TileEntityCommonOSD;
import mctmods.immersivetechnology.common.util.TranslationKey;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class TileEntityCrate extends TileEntityCommonOSD implements IItemHandler, IPlayerInteraction {

	public ItemStack visibleItemStack = ItemStack.EMPTY;
	public ItemStack interactiveItemStack = ItemStack.EMPTY;

	public void setItemStack(ItemStack toSet) {
		interactiveItemStack = toSet;
		interactiveItemStack.setCount(interactiveItemStack.getMaxStackSize());
		visibleItemStack = toSet.copy();
		visibleItemStack.setCount(visibleItemStack.getMaxStackSize());
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.readCustomNBT(nbt, descPacket);
		setItemStack(new ItemStack(nbt.getCompound("item")));
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
		nbt.put("item", interactiveItemStack.writeToNBT(new CompoundNBT()));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T)this;
		return super.getCapability(capability, facing);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int i) {
		return visibleItemStack;
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int i, @Nonnull ItemStack itemStack, boolean simulate) {
		return itemStack;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if(interactiveItemStack.isEmpty()) return ItemStack.EMPTY;
		ItemStack toReturn = interactiveItemStack.copy();
		toReturn.setCount(amount);
		if(!simulate) acceptedAmount += amount;
		return toReturn;
	}

	@Override
	public int getSlotLimit(int slot) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public boolean interact(Direction side, PlayerEntity player, Hand hand, ItemStack heldItem, float hitX, float hitY, float hitZ) {
		if(heldItem.isEmpty()) {
			if(player.isSneaking()) {
				visibleItemStack = ItemStack.EMPTY;
				interactiveItemStack = ItemStack.EMPTY;
				return true;
			} else if(!interactiveItemStack.isEmpty()) {
				player.inventory.addItemStackToInventory(interactiveItemStack);
				return true;
			}
		} else if(interactiveItemStack.isEmpty()) {
			setItemStack(heldItem.copy());
			return true;
		}
		return false;
	}

	@Override
	public void receiveMessageFromServer(CompoundNBT message) {
		setItemStack(new ItemStack(message.getCompound("item")));
		super.receiveMessageFromServer(message);
	}

	@Override
	public void notifyNearbyClients(CompoundNBT nbt) {
		nbt.put("item", interactiveItemStack.writeToNBT(new CompoundNBT()));
		super.notifyNearbyClients(nbt);
	}

	@Override
	public String[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer) {
		return new String[]{ !interactiveItemStack.isEmpty()? text().format(interactiveItemStack.getDisplayName(), lastAcceptedAmount) : TranslationKey.GUI_EMPTY.text() };
	}
	
	@Override
	public TranslationKey text() {
		return TranslationKey.OVERLAY_OSD_CREATIVE_CRATE_NORMAL_FIRST_LINE;
	}

}