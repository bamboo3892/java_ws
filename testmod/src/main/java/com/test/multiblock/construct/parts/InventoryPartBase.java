package com.test.multiblock.construct.parts;

import com.test.network.SimpleTilePacket.PacketType;
import com.test.utils.InventoryHelper;
import com.test.utils.RectangularSolid;
import com.test.utils.UtilMethods;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class InventoryPartBase extends SidedOutPuterPart<ISidedInventory> implements ISidedInventory {

	public static final int[] maxTransfer = { 1, 4, 16, 32, 64 };

	public ItemStack[] items;

	public InventoryPartBase() {
		this.items = new ItemStack[this.getSizeInventory()];
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
				if(InventoryHelper.tryPushItemEX(this, connection[i].getTile(), ForgeDirection.getOrientation(i), ForgeDirection.getOrientation(connection[i].side), maxTransfer)){
					this.sendConnectionParticlePacket(i, 0x00ff7f);
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
		for (int slotIndex = 0; slotIndex < this.items.length; ++slotIndex){
			if(this.items[slotIndex] != null){
				NBTTagCompound itemTagCompound = new NBTTagCompound();
				itemTagCompound.setByte("Slot", (byte) slotIndex);
				this.items[slotIndex].writeToNBT(itemTagCompound);
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
			this.items = new ItemStack[this.getSizeInventory()];
			for (int tagCounter = 0; tagCounter < itemsTagList.tagCount(); ++tagCounter){
				NBTTagCompound itemTagCompound = (NBTTagCompound) itemsTagList.getCompoundTagAt(tagCounter);
				byte slotIndex = itemTagCompound.getByte("Slot");
				if(slotIndex >= 0 && slotIndex < this.items.length){
					this.items[slotIndex] = ItemStack.loadItemStackFromNBT(itemTagCompound);
					//if(items[slotIndex].stackSize == 0) items[slotIndex] = null;
				}
			}
		}
		super.processCommand(type, tag);
	}

	protected Class<ISidedInventory> getTargetClass() {
		return ISidedInventory.class;
	}
	protected boolean shouldDistinguishSide() {
		return true;
	}

	@Override
	public void markDirty() {
		coreTile.markForContentUpdate(xCoord, yCoord, zCoord);
	}
	@Override
	public final ItemStack getStackInSlot(int slotIndex) {
		if(slotIndex >= 0 && slotIndex < this.items.length){
			return this.items[slotIndex];
		}
		return null;
	}
	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		if(slotIndex >= 0 && slotIndex < this.items.length){
			return this.items[slotIndex];
		}
		return null;
	}
	@Override
	public final void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		this.items[slotIndex] = itemStack;
		if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()){
			itemStack.stackSize = getInventoryStackLimit();
		}
		this.markDirty();
	}
	@Override
	public final ItemStack decrStackSize(int slotIndex, int splitStackSize) {
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
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return flagIO[side] == 0 ;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return flagIO[side] == 1 ;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.readFromNBT(nbtTagCompound, solid);
		NBTTagList itemsTagList = nbtTagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		this.items = new ItemStack[this.getSizeInventory()];
		for (int tagCounter = 0; tagCounter < itemsTagList.tagCount(); ++tagCounter){
			NBTTagCompound itemTagCompound = (NBTTagCompound) itemsTagList.getCompoundTagAt(tagCounter);
			byte slotIndex = itemTagCompound.getByte("Slot");
			if(slotIndex >= 0 && slotIndex < this.items.length){
				this.items[slotIndex] = ItemStack.loadItemStackFromNBT(itemTagCompound);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.writeToNBT(nbtTagCompound, solid);
		NBTTagList itemsTagList = new NBTTagList();
		for (int slotIndex = 0; slotIndex < this.items.length; ++slotIndex){
			if(this.items[slotIndex] != null){
				NBTTagCompound itemTagCompound = new NBTTagCompound();
				itemTagCompound.setByte("Slot", (byte) slotIndex);
				this.items[slotIndex].writeToNBT(itemTagCompound);
				itemsTagList.appendTag(itemTagCompound);
			}
		}
		nbtTagCompound.setTag("Items", itemsTagList);
	}

}