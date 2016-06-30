package com.okina.multiblock.construct.parts;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ConstructCrusherTileEntity;
import com.okina.multiblock.construct.tileentity.ISignalReceiver;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CrusherPart extends ConstructPartBase implements ISidedInventory, ISignalReceiver {

	public static int[] maxRemain = { 2, 10, 20, 40, 100 };
	public ItemStack cactus;
	public EnumFacing direction;
	public int remain = maxRemain[grade];
	public ContainerPart container = null;

	public CrusherPart() {

	}

	public boolean readyToCrush() {
		return cactus != null && remain > 0;
	}
	public void doCrash() {
		if(remain <= 1){
			remain = maxRemain[grade];
			cactus = null;
			markDirty();
		}else{
			remain -= 1;
		}
		dispatchEventOnNextTick();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(container != null && readyToCrush()){
			container.startCrush();
		}
	}

	@Override
	public void markDirty() {

	}
	@Override
	public int getSizeInventory() {
		return 1;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? cactus : null;
	}
	@Override
	public ItemStack decrStackSize(int slotIndex, int splitStackSize) {
		if(cactus != null){
			if(cactus.stackSize <= splitStackSize){
				ItemStack tmpItemStack = cactus;
				cactus = null;
				markDirty();
				return tmpItemStack;
			}
			ItemStack splittedItemStack = cactus.splitStack(splitStackSize);
			if(cactus.stackSize == 0){
				cactus = null;
			}
			markDirty();
			return splittedItemStack;
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		cactus = itemStack;
		markDirty();
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
		return "crusher";
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
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}
	@Override
	public void openInventory(EntityPlayer player) {}
	@Override
	public void closeInventory(EntityPlayer player) {}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return itemStack == null ? false : Block.getBlockFromItem(itemStack.getItem()) == Blocks.cactus;
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		return flagIO[side.getIndex()] == 0;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return flagIO[side.getIndex()] == 1;
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0 };
	}
	@Override
	public int getField(int id) {
		return 0;
	}
	@Override
	public void setField(int id, int value) {}
	@Override
	public int getFieldCount() {
		return 0;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return ConstructCrusherTileEntity.nameForNBT;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.readFromNBT(tag, solid);
		cactus = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("content"));
		direction = EnumFacing.getFront(tag.getInteger("dir"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.writeToNBT(tag, solid);
		if(cactus != null){
			NBTTagCompound itemnbt = new NBTTagCompound();
			cactus.writeToNBT(itemnbt);
			tag.setTag("content", itemnbt);
		}
		tag.setInteger("dir", direction.getIndex());
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructCrusher[grade];
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected int getRenderMeta() {
		return direction.getIndex();
	}

}
