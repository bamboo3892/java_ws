package com.test.multiblock.construct.parts;

import com.test.client.gui.ConstructStorageGui;
import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructStorageTileEntity;
import com.test.server.gui.ConstructStorageContainer;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StoragePart extends FilterUserPart {

	public StoragePart() {

	}

	@Override
	public boolean isOpenGuiOnClicked() {
		return true;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, boolean serverSide) {
		if(serverSide){
			return new ConstructStorageContainer(player.inventory, this);
		}else{
			return new ConstructStorageGui(player.inventory, this);
		}
	}

	@Override
	public void updatePart() {
		super.updatePart();
		itemTransfer();
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
	public Position getPosition() {
		return coreTile.getPosition();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.readFromNBT(nbtTagCompound, solid);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.writeToNBT(nbtTagCompound, solid);
	}

	@Override
	public String getNameForNBT() {
		return ConstructStorageTileEntity.nameForNBT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructStorage[grade];
	}

}
