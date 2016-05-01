package com.test.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GhostSlot extends Slot {

	private final int slotIndex;

	public GhostSlot(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
		this.slotIndex = slotIndex;
	}

	public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
		putStack(null);
		this.onSlotChanged();
	}

	public boolean isItemValid(ItemStack itemStack) {
		return getStack() == null;
	}

	public ItemStack getStack() {
		return this.inventory.getStackInSlot(this.slotIndex);
	}

	public boolean getHasStack() {
		return this.getStack() != null;
	}

	public void putStack(ItemStack itemStack) {
		this.inventory.setInventorySlotContents(this.slotIndex, itemStack);
		this.onSlotChanged();
	}

	public void onSlotChanged() {
		this.inventory.markDirty();
	}

	public int getSlotStackLimit() {
		return this.inventory.getInventoryStackLimit();
	}

	public ItemStack decrStackSize(int amount) {
		return this.inventory.decrStackSize(this.slotIndex, amount);
	}

	public boolean isSlotInInventory(IInventory inventory, int slotIndex) {
		return inventory == this.inventory && slotIndex == this.slotIndex;
	}

	public boolean canTakeStack(EntityPlayer player) {
		putStack(null);
		return false;
	}

}
