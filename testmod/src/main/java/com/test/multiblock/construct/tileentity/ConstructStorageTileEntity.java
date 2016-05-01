package com.test.multiblock.construct.tileentity;

import com.test.client.gui.ConstructStorageGui;
import com.test.main.TestCore;
import com.test.server.gui.ConstructStorageContainer;
import com.test.utils.RectangularSolid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ConstructStorageTileEntity extends ConstructFilterUserTileEntity {

	public static final String nameForNBT = "storage";

	public ConstructStorageTileEntity() {
		this(0);
	}

	public ConstructStorageTileEntity(int grade) {
		super(grade);
	}

	public boolean onRightClickedNotFilterSide(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote) itemTransfer(maxTransfer[grade]);
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
