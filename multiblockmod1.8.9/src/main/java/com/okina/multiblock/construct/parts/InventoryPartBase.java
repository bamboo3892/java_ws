package com.okina.multiblock.construct.parts;

import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.InventoryHelper;
import com.okina.utils.RectangularSolid;
import com.okina.utils.UtilMethods;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants;

public abstract class InventoryPartBase extends SidedOutPuterPart<ISidedInventory> implements ISidedInventory {

	public static final int[] maxTransfer = { 1, 4, 16, 32, 64 };

	public ItemStack[] items;

	public InventoryPartBase() {
		items = new ItemStack[getSizeInventory()];
		for (int i = 0; i < 6; i++){
			connection[i] = null;
			flagIO[i] = 2;
		}
	}

	protected boolean itemTransfer() {
		return this.itemTransfer(maxTransfer[grade]);
	}

	protected boolean itemTransfer(int maxTransfer) {
		int[] order = UtilMethods.getRandomArray(new int[] { 0, 1, 2, 3, 4, 5 });
		for (int i : order){
			if(connection[i] != null && flagIO[i] == 1){
				if(InventoryHelper.tryPushItemEX(this, connection[i].getTile(), EnumFacing.getFront(i), connection[i].getFront(), maxTransfer)){
					sendConnectionParticlePacket(i, 0x00ff7f);
					return true;
				}
			}
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public NBTTagCompound getContentUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList itemsTagList = new NBTTagList();
		for (int slotIndex = 0; slotIndex < items.length; ++slotIndex){
			if(items[slotIndex] != null){
				NBTTagCompound itemTagCompound = new NBTTagCompound();
				itemTagCompound.setByte("Slot", (byte) slotIndex);
				items[slotIndex].writeToNBT(itemTagCompound);
				itemsTagList.appendTag(itemTagCompound);
			}
		}
		tag.setTag("Items", itemsTagList);
		return tag;
	}

	@Override
	public void processCommand(PacketType type, NBTTagCompound tag) {
		if(type == PacketType.NBT_CONTENT){//should client
			NBTTagList itemsTagList = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
			items = new ItemStack[getSizeInventory()];
			for (int tagCounter = 0; tagCounter < itemsTagList.tagCount(); ++tagCounter){
				NBTTagCompound itemTagCompound = itemsTagList.getCompoundTagAt(tagCounter);
				byte slotIndex = itemTagCompound.getByte("Slot");
				if(slotIndex >= 0 && slotIndex < items.length){
					items[slotIndex] = ItemStack.loadItemStackFromNBT(itemTagCompound);
					//if(items[slotIndex].stackSize == 0) items[slotIndex] = null;
				}
			}
		}
		super.processCommand(type, tag);
	}

	@Override
	protected Class<ISidedInventory> getTargetClass() {
		return ISidedInventory.class;
	}
	@Override
	protected boolean shouldDistinguishSide() {
		return true;
	}

	@Override
	public void markDirty() {
		coreTile.markForContentUpdate(xCoord, yCoord, zCoord);
	}
	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		if(slotIndex >= 0 && slotIndex < items.length){
			return items[slotIndex];
		}
		return null;
	}
	@Override
	public final void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		items[slotIndex] = itemStack;
		if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()){
			itemStack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}
	@Override
	public final ItemStack decrStackSize(int slotIndex, int splitStackSize) {
		if(items[slotIndex] != null){
			if(items[slotIndex].stackSize <= splitStackSize){
				ItemStack tmpItemStack = items[slotIndex];
				items[slotIndex] = null;
				markDirty();
				return tmpItemStack;
			}
			ItemStack splittedItemStack = items[slotIndex].splitStack(splitStackSize);
			if(items[slotIndex].stackSize == 0){
				items[slotIndex] = null;
			}
			markDirty();
			return splittedItemStack;
		}
		return null;
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
	public boolean hasCustomName() {
		return false;
	}
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
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
	public int getField(int id) {
		return 0;
	}
	@Override
	public void setField(int id, int value) {}
	@Override
	public int getFieldCount() {
		return 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.readFromNBT(nbtTagCompound, solid);
		NBTTagList itemsTagList = nbtTagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		items = new ItemStack[getSizeInventory()];
		for (int tagCounter = 0; tagCounter < itemsTagList.tagCount(); ++tagCounter){
			NBTTagCompound itemTagCompound = itemsTagList.getCompoundTagAt(tagCounter);
			byte slotIndex = itemTagCompound.getByte("Slot");
			if(slotIndex >= 0 && slotIndex < items.length){
				items[slotIndex] = ItemStack.loadItemStackFromNBT(itemTagCompound);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.writeToNBT(nbtTagCompound, solid);
		NBTTagList itemsTagList = new NBTTagList();
		for (int slotIndex = 0; slotIndex < items.length; ++slotIndex){
			if(items[slotIndex] != null){
				NBTTagCompound itemTagCompound = new NBTTagCompound();
				itemTagCompound.setByte("Slot", (byte) slotIndex);
				items[slotIndex].writeToNBT(itemTagCompound);
				itemsTagList.appendTag(itemTagCompound);
			}
		}
		nbtTagCompound.setTag("Items", itemsTagList);
	}

}
