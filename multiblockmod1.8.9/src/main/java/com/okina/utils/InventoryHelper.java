package com.okina.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class InventoryHelper {

	public static final int SIMULATE = 0;
	public static final int AS_MANY_AS_POSSIBLE = 1;
	public static final int WHOLE = 2;

	/**return true if any item moved*/
	public static boolean tryPushItemEX(IInventory start, IInventory goal, EnumFacing startSide, EnumFacing goalSide, int amount) {
		if(start == null) return false;
		ItemStack item;
		int n = amount;
		if(start instanceof ISidedInventory){
			ISidedInventory backSidedInv = (ISidedInventory) start;
			int[] accessible = backSidedInv.getSlotsForFace(startSide);
			for (int slot : accessible){
				if(backSidedInv.getStackInSlot(slot) != null){
					item = backSidedInv.getStackInSlot(slot).copy();
					int size = Math.min(amount, item.stackSize);
					item.stackSize = size;
					if(backSidedInv.canExtractItem(slot, item, startSide)){
						int originalStackSize = item.stackSize;
						ItemStack itemstack2 = InventoryHelper.tryPushItem(goal, goalSide, item, AS_MANY_AS_POSSIBLE);
						backSidedInv.decrStackSize(slot, itemstack2 == null ? size : originalStackSize - itemstack2.stackSize);
						amount -= itemstack2 == null ? size : originalStackSize - itemstack2.stackSize;
						if(amount <= 0){
							backSidedInv.markDirty();
							return true;
						}
					}
				}
			}
		}else{
			for (int slot = 0; slot < start.getSizeInventory(); slot++){
				if(start.getStackInSlot(slot) != null){
					item = start.getStackInSlot(slot).copy();
					int size = Math.min(amount, item.stackSize);
					item.stackSize = size;
					int originalStackSize = item.stackSize;
					ItemStack itemstack2 = InventoryHelper.tryPushItem(goal, goalSide, item, AS_MANY_AS_POSSIBLE);
					start.decrStackSize(slot, itemstack2 == null ? size : originalStackSize - itemstack2.stackSize);
					amount -= itemstack2 == null ? size : originalStackSize - itemstack2.stackSize;
					if(amount <= 0){
						start.markDirty();
						return true;
					}
				}
			}
		}
		if(amount != n){
			start.markDirty();
			return true;
		}
		return false;
	}

	/**command
	 * 0 : simulate
	 * 1 : try push item as many as possible
	 * 2 : try push item if can transfer whole itemstack
	 *
	 * return the rest of param itemstack
	 */
	public static ItemStack tryPushItem(IInventory goal, EnumFacing side, ItemStack item, int command) {
		if(goal == null || item == null){
			return item;
		}
		ItemStack original = item.copy();
		int initSize = item.stackSize;
		if(goal instanceof ISidedInventory){
			ISidedInventory sided = (ISidedInventory) goal;
			int[] accessible = sided.getSlotsForFace(side);
			if(accessible != null){
				ItemStack[] goalInv = new ItemStack[accessible.length];
				for (int i = 0; i < accessible.length; i++){
					if(!sided.isItemValidForSlot(accessible[i], item)) continue;
					if(!sided.canInsertItem(accessible[i], item, side)) continue;
					goalInv[i] = sided.getStackInSlot(accessible[i]) == null ? null : sided.getStackInSlot(accessible[i]).copy();
					int stackable = getStackableSize(item, goalInv[i], sided.getInventoryStackLimit());
					if(stackable > 0){
						item.stackSize -= stackable;
						if(goalInv[i] != null){
							goalInv[i].stackSize += stackable;
						}else{
							goalInv[i] = item.copy();
							goalInv[i].stackSize = stackable;
						}
						if(item.stackSize <= 0){//transfer complete
							if(command != SIMULATE){
								for (int j = 0; j <= i; j++){
									sided.setInventorySlotContents(accessible[j], goalInv[j]);
								}
								sided.markDirty();
								return null;
							}else{
								return null;
							}
						}
					}
				}
				//transfer imcomplete
				if(command == AS_MANY_AS_POSSIBLE && item.stackSize < initSize){
					for (int i = 0; i < accessible.length; i++){
						sided.setInventorySlotContents(accessible[i], goalInv[i]);
					}
					sided.markDirty();
					return item;
				}
			}
			return original;
		}else{
			IInventory inv = goal;
			ItemStack[] goalInv = new ItemStack[inv.getSizeInventory()];
			for (int i = 0; i < inv.getSizeInventory(); i++){
				if(!inv.isItemValidForSlot(i, item)) continue;
				goalInv[i] = inv.getStackInSlot(i) == null ? null : inv.getStackInSlot(i).copy();
				int stackable = getStackableSize(item, goalInv[i], inv.getInventoryStackLimit());
				if(stackable > 0){
					item.stackSize -= stackable;
					if(goalInv[i] != null){
						goalInv[i].stackSize += stackable;
					}else{
						goalInv[i] = item.copy();
						goalInv[i].stackSize = stackable;
					}
					if(item.stackSize <= 0){//transfer complete
						if(command != SIMULATE){
							for (int j = 0; j <= i; j++){
								inv.setInventorySlotContents(j, goalInv[j]);
							}
							inv.markDirty();
							return null;
						}else{
							return null;
						}
					}
				}
			}
			//transfer imcomplete
			if(command == AS_MANY_AS_POSSIBLE && item.stackSize < initSize){
				for (int i = 0; i < inv.getSizeInventory(); i++){
					inv.setInventorySlotContents(i, goalInv[i]);
				}
				inv.markDirty();
				return item;
			}
			return original;
		}
	}

	public static int getStackableSize(ItemStack target, ItemStack current, int invMaxStackSize) {
		if(target != null){
			if(current != null){
				if(target.isItemEqual(current) && ItemStack.areItemStackTagsEqual(target, current)){
					return Math.min(target.stackSize, Math.min(invMaxStackSize, target.getMaxStackSize()) - current.stackSize);
				}
			}else{
				return Math.min(target.stackSize, Math.min(invMaxStackSize, target.getMaxStackSize()));
			}
		}
		return 0;
	}

	public static boolean isItemStackable(ItemStack target, ItemStack current, int invMaxStackSize) {
		if(target == null || current == null) return false;
		if(target.getItem() == current.getItem() && target.getItemDamage() == current.getItemDamage()){
			return (current.stackSize + target.stackSize) <= current.getMaxStackSize() && (current.stackSize + target.stackSize) <= invMaxStackSize;
		}
		return false;
	}
}
