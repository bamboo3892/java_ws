package com.test.server.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class ConstructFurnaceContainer extends Container {

	private IFurnaceGuiUser tileFurnace;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;

	public ConstructFurnaceContainer(InventoryPlayer inventoryPlayer, IFurnaceGuiUser tile) {
		this.tileFurnace = tile;
		this.addSlotToContainer(new Slot(tile, 0, 56, 17));
		this.addSlotToContainer(new Slot(tile, 1, 56, 53));
		this.addSlotToContainer(new SlotFurnace(inventoryPlayer.player, tile, 2, 116, 35));
		int i;

		for (i = 0; i < 3; ++i){
			for (int j = 0; j < 9; ++j){
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i){
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
		crafting.sendProgressBarUpdate(this, 0, this.tileFurnace.getFurnaceCookTime());
		crafting.sendProgressBarUpdate(this, 1, this.tileFurnace.getFurnaceBurnTime());
		crafting.sendProgressBarUpdate(this, 2, this.tileFurnace.getCurrentItemBurnTime());
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); ++i){
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			if(this.lastCookTime != this.tileFurnace.getFurnaceCookTime()){
				icrafting.sendProgressBarUpdate(this, 0, this.tileFurnace.getFurnaceCookTime());
			}

			if(this.lastBurnTime != this.tileFurnace.getFurnaceBurnTime()){
				icrafting.sendProgressBarUpdate(this, 1, this.tileFurnace.getFurnaceBurnTime());
			}

			if(this.lastItemBurnTime != this.tileFurnace.getCurrentItemBurnTime()){
				icrafting.sendProgressBarUpdate(this, 2, this.tileFurnace.getCurrentItemBurnTime());
			}
		}

		this.lastCookTime = this.tileFurnace.getFurnaceCookTime();
		this.lastBurnTime = this.tileFurnace.getFurnaceBurnTime();
		this.lastItemBurnTime = this.tileFurnace.getCurrentItemBurnTime();
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int command, int time) {
		if(command == 0){
			this.tileFurnace.setFurnaceCookTime(time);
		}

		if(command == 1){
			this.tileFurnace.setFurnaceBurnTime(time);
		}

		if(command == 2){
			this.tileFurnace.setCurrentItemBurnTime(time);
		}
	}

	public boolean canInteractWith(EntityPlayer player) {
		return this.tileFurnace.isUseableByPlayer(player);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(index == 2){
				if(!this.mergeItemStack(itemstack1, 3, 39, true)){
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}else if(index != 1 && index != 0){
				if(FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null){
					if(!this.mergeItemStack(itemstack1, 0, 1, false)){
						return null;
					}
				}else if(TileEntityFurnace.isItemFuel(itemstack1)){
					if(!this.mergeItemStack(itemstack1, 1, 2, false)){
						return null;
					}
				}else if(index >= 3 && index < 30){
					if(!this.mergeItemStack(itemstack1, 30, 39, false)){
						return null;
					}
				}else if(index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)){
					return null;
				}
			}else if(!this.mergeItemStack(itemstack1, 3, 39, false)){
				return null;
			}

			if(itemstack1.stackSize == 0){
				slot.putStack((ItemStack) null);
			}else{
				slot.onSlotChanged();
			}

			if(itemstack1.stackSize == itemstack.stackSize){
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	public interface IFurnaceGuiUser extends ISidedInventory {

		int getFurnaceCookTime();
		int getFurnaceBurnTime();
		int getCurrentItemBurnTime();

		void setFurnaceCookTime(int time);
		void setFurnaceBurnTime(int time);
		void setCurrentItemBurnTime(int time);

		@SideOnly(Side.CLIENT)
		public int getCookProgressScaled(int scale);

		@SideOnly(Side.CLIENT)
		public int getBurnTimeRemainingScaled(int scale) ;

		public boolean isBurning();

	}

}
