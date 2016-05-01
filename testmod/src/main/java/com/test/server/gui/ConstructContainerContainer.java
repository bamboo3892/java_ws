package com.test.server.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ConstructContainerContainer extends Container {

	InventoryPlayer playerInv;
	IInventory containerInv;

	public ConstructContainerContainer(InventoryPlayer playerInv, IInventory containerInv) {
		this.playerInv = playerInv;
		this.containerInv = containerInv;

		this.addSlotToContainer(new Slot(containerInv, 0, 62 + 1 * 18, 17 + 1 * 18));

		for (int col = 0; col < 3; ++col){
			for (int row = 0; row < 9; ++row){
				this.addSlotToContainer(new Slot(playerInv, row + col * 9 + 9, 8 + row * 18, 84 + col * 18));
			}
		}
		for (int row = 0; row < 9; ++row){
			this.addSlotToContainer(new Slot(playerInv, row, 8 + row * 18, 142));
		}
	}

	public boolean canInteractWith(EntityPlayer player) {
		return this.containerInv.isUseableByPlayer(player);
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(slotIndex == 0){
				if(!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true)){
					return null;
				}
			}else if(!this.mergeItemStack(itemstack1, 0, 0, false)){
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
		this.containerInv.closeInventory();
	}

}
