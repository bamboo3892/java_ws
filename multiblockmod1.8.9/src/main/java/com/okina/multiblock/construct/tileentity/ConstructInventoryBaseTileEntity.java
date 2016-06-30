package com.okina.multiblock.construct.tileentity;

import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.InventoryHelper;
import com.okina.utils.RectangularSolid;
import com.okina.utils.UtilMethods;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

public abstract class ConstructInventoryBaseTileEntity extends ConstructSidedOutputerTileEntity<IConstructInventory> implements IConstructInventory {

	public ItemStack[] items;

	public ConstructInventoryBaseTileEntity(int grade) {
		super(grade);
		items = new ItemStack[getSizeInventory()];
	}

	protected boolean itemTransfer(int maxTransfer) {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		int[] order = UtilMethods.getRandomArray(new int[] { 0, 1, 2, 3, 4, 5 });
		for (int i : order){
			if(connection[i] != null && flagIO[i] == 1){
				if(InventoryHelper.tryPushItemEX(this, connection[i].getTile(), EnumFacing.getFront(i), EnumFacing.getFront(connection[i].side), maxTransfer)){
					return true;
				}
			}
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.NBT_CONTENT){
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
			return new SimpleTilePacket(this, PacketType.NBT_CONTENT, tag);
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			NBTTagList itemsTagList = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
			items = new ItemStack[getSizeInventory()];
			for (int tagCounter = 0; tagCounter < itemsTagList.tagCount(); ++tagCounter){
				NBTTagCompound itemTagCompound = itemsTagList.getCompoundTagAt(tagCounter);
				byte slotIndex = itemTagCompound.getByte("Slot");
				if(slotIndex >= 0 && slotIndex < items.length){
					items[slotIndex] = ItemStack.loadItemStackFromNBT(itemTagCompound);
				}
			}
		}
	}

	@Override
	protected Class<IConstructInventory> getTargetClass() {
		return IConstructInventory.class;
	}
	@Override
	protected boolean shouldDistinguishSide() {
		return true;
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
		if(slotIndex < 0 && slotIndex >= items.length || splitStackSize <= 0) return null;
		if(items[slotIndex] != null){
			if(items[slotIndex].stackSize <= splitStackSize){
				ItemStack tmpItemStack = items[slotIndex].copy();
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
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if(worldObj.getTileEntity(pos) != this){
			return false;
		}
		return entityplayer.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 50000.0D;
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		return flagIO[side.getIndex()] == 0;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return flagIO[side.getIndex()] == 1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		updateEntry();
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
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
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
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
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
