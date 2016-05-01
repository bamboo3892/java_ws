package com.test.server.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ConstructStorageContainer extends Container {

	private IInventory storageInv;
	private int numRows;

	public ConstructStorageContainer(IInventory invPlayer, IInventory invChest) {
		this.storageInv = invChest;
		this.numRows = invChest.getSizeInventory() / 9;
		invChest.openInventory();
		int i = (this.numRows - 4) * 18;
		int j;
		int k;

		for (j = 0; j < this.numRows; ++j){
			for (k = 0; k < 9; ++k){
				this.addSlotToContainer(new Slot(invChest, k + j * 9, 8 + k * 18, 18 + j * 18));
			}
		}

		for (j = 0; j < 3; ++j){
			for (k = 0; k < 9; ++k){
				this.addSlotToContainer(new Slot(invPlayer, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
			}
		}

		for (j = 0; j < 9; ++j){
			this.addSlotToContainer(new Slot(invPlayer, j, 8 + j * 18, 161 + i));
		}
	}

	public boolean canInteractWith(EntityPlayer player) {
		return this.storageInv.isUseableByPlayer(player);
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(slotIndex < this.numRows * 9){
				if(!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true)){
					return null;
				}
			}else if(!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false)){
				return null;
			}

			if(itemstack1.stackSize == 0){
				slot.putStack((ItemStack) null);
			}else{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		this.storageInv.closeInventory();
	}

}
