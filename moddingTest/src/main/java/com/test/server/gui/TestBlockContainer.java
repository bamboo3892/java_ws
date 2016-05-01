package com.test.server.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TestBlockContainer extends Container{

	public TestBlockContainer(InventoryPlayer inventoryPlayer) {
		for (int slotCol = 0; slotCol < 3; ++slotCol) {
			for (int slotRow = 0; slotRow < 9; ++slotRow) {
				this.addSlotToContainer(new Slot(inventoryPlayer, slotRow + slotCol * 9 + 9, 8 + slotRow * 18, 84 + slotCol * 18));
			}
		}

		for (int slotRow = 0; slotRow < 9; ++slotRow) {
			this.addSlotToContainer(new Slot(inventoryPlayer, slotRow, 8 + slotRow * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

	//shift left click
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		return player.inventory.getStackInSlot(slotIndex);
	}

}
