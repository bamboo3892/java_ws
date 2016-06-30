package com.okina.multiblock.construct.tileentity;

import com.okina.client.gui.ConstructStorageGui;
import com.okina.main.TestCore;
import com.okina.server.gui.ConstructStorageContainer;
import com.okina.utils.RectangularSolid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

public class ConstructStorageTileEntity extends ConstructFilterUserTileEntity {

	public static final String nameForNBT = "storage";

	public ConstructStorageTileEntity() {
		this(0);
	}

	public ConstructStorageTileEntity(int grade) {
		super(grade);
	}

	@Override
	public boolean onRightClickedNotFilterSide(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side.getIndex(), worldObj, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void update() {
		super.update();
		if(!worldObj.isRemote){
			itemTransfer(maxTransfer[grade]);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	@Override
	public int getSizeInventory() {
		return 27;
	}
	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(getStackInSlot(index) != null){
			ItemStack itemstack = getStackInSlot(index);
			setInventorySlotContents(index, null);
			return itemstack;
		}else{
			return null;
		}
	}
	@Override
	public void clear() {
		for (int index = 0; index < getSizeInventory(); index++){
			setInventorySlotContents(index, null);
		}
	}
	@Override
	public String getName() {
		return "Storage";
	}
	@Override
	public boolean hasCustomName() {
		return false;
	}
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
	}

	@Override
	public void openInventory(EntityPlayer player) {}
	@Override
	public void closeInventory(EntityPlayer player) {}
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return true;
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] slots = new int[27];
		for (int i = 0; i < 27; i++){
			slots[i] = i;
		}
		return slots;
	}
	@Override
	public int getField(int id) {
		return 0;
	}
	@Override
	public void setField(int id, int value) {

	}
	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		Object elem = super.getGuiElement(player, side, serverSide);
		if(elem == null){
			return serverSide ? new ConstructStorageContainer(player.inventory, this) : new ConstructStorageGui(player.inventory, this);
		}else{
			return elem;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
	}

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
