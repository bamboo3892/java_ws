package com.test.multiblock.construct.tileentity;

import com.test.main.TestCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ConstructStorageTileEntity extends ConstructFilterUserTileEntity{

	public static final String nameForNBT = "storage";

	public ConstructStorageTileEntity(){
		this(0);
	}

	public ConstructStorageTileEntity(int grade){
		super(grade);
	}

	public void onRightClickedNotFilterSide(EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		player.openGui(TestCore.instance, TestCore.STORAGE_GUI_ID, worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote) itemTransfer();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getSizeInventory() {
		return 27;
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		int[] slots = new int[27];
		for (int i = 0; i < 27; i++){
			slots[i] = i;
		}
		return slots;
	}
	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if(slotIndex >= 0 && slotIndex < this.items.length){
			return this.items[slotIndex];
		}
		return null;
	}
	@Override
	public ItemStack decrStackSize(int slotIndex, int splitStackSize) {
		if(this.items[slotIndex] != null){
			if(this.items[slotIndex].stackSize <= splitStackSize){
				ItemStack tmpItemStack = items[slotIndex];
				this.items[slotIndex] = null;
				this.markDirty();
				return tmpItemStack;
			}
			ItemStack splittedItemStack = this.items[slotIndex].splitStack(splitStackSize);
			if(this.items[slotIndex].stackSize == 0){
				this.items[slotIndex] = null;
			}
			this.markDirty();
			return splittedItemStack;
		}
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		return items[slotIndex];
	}
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		this.items[slotIndex] = itemStack;
		if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()){
			itemStack.stackSize = getInventoryStackLimit();
		}
		this.markDirty();
	}
	@Override
	public String getInventoryName() {
		return "Storage";
	}
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return true;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
	}

}
