package com.okina.multiblock.construct.parts;

import com.okina.client.gui.ConstructStorageGui;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ConstructStorageTileEntity;
import com.okina.server.gui.ConstructStorageContainer;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public int[] getSlotsForFace(EnumFacing side) {
		int[] slots = new int[27];
		for (int i = 0; i < 27; i++){
			slots[i] = i;
		}
		return slots;
	}
	@Override
	public String getName() {
		return "Storage";
	}
	@Override
	public int getInventoryStackLimit() {
		return 64;
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
	public BlockPos getPosition() {
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
