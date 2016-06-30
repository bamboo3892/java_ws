package com.okina.multiblock.construct.parts;

import java.util.ArrayList;

import com.okina.client.gui.ConstructContainerGui;
import com.okina.inventory.AbstractFilter;
import com.okina.inventory.IFilterUser;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ConstructDispatcherTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructFilterUserTileEntity;
import com.okina.multiblock.construct.tileentity.IConstructInventory;
import com.okina.multiblock.construct.tileentity.ISignalReceiver;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.server.gui.ConstructContainerContainer;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLLog;

public class DispatcherPart extends ConstructPartBase implements IConstructInventory, IFilterUser, ISignalReceiver {

	public static final int[] filterCapability = { 0, 2, 4, 5, 6 };

	public int stackLimit = 1;
	public ItemStack containingItem;

	private ArrayList<ConnectionEntry> invConnect = new ArrayList();
	private ArrayList<ConnectionEntry> signalReceivers = new ArrayList();
	private AbstractFilter[] filter = new AbstractFilter[6];
	private int indexPointer;

	public DispatcherPart() {

	}

	@Override
	public boolean isOpenGuiOnClicked() {
		return true;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, boolean serverSide) {
		if(serverSide){
			return new ConstructContainerContainer(player.inventory, this);
		}else{
			return new ConstructContainerGui(player.inventory, this);
		}
	}

	private boolean needUpdateEntry = true;

	@Override
	public void updatePart() {
		super.updatePart();
		if(needUpdateEntry){
			updateAllConnection();
			needUpdateEntry = false;
		}
		for (int i = 0; i < maxTransfer[grade]; i++){
			if(!itemTransfer()) break;
		}
	}

	private void updateAllConnection() {
		for (int i = 0; i < invConnect.size(); i++){
			ConnectionEntry entry = invConnect.get(i);
			if(entry != null){
				ConstructPartBase part = coreTile.getPart(entry.x, entry.y, entry.z);
				if(part != null && part instanceof ISidedInventory){
					entry.setTile(part);
					continue;
				}
			}
			System.err.println("Multi block content has been illegaly changed");
			invConnect.remove(i);
		}
		for (int i = 0; i < signalReceivers.size(); i++){
			ConnectionEntry entry = signalReceivers.get(i);
			if(entry != null){
				ConstructPartBase part = coreTile.getPart(entry.x, entry.y, entry.z);
				if(part != null && part instanceof ISignalReceiver){
					entry.setTile(part);
					continue;
				}
			}
			System.err.println("Multi block content has been illegaly changed");
			invConnect.remove(i);
		}
	}

	private boolean itemTransfer() {
		if(invConnect.size() > 0 && containingItem != null){
			ItemStack targetStack = containingItem.copy();
			targetStack.stackSize = 1;

			if(invConnect.size() - 1 < indexPointer){
				indexPointer = 0;
			}
			int startIndex = indexPointer;
			do{
				ConnectionEntry<IConstructInventory> entry = invConnect.get(indexPointer);
				if(entry != null && entry.hasTile()){
					ItemStack rest = InventoryHelper.tryPushItem(entry.getTile(), entry.getFront(), targetStack, InventoryHelper.WHOLE);
					if(rest == null || rest.stackSize <= 0){
						containingItem.stackSize--;
						if(containingItem.stackSize == 0){
							containingItem = null;
						}
						markDirty();
						indexPointer = invConnect.size() - 1 <= indexPointer ? 0 : indexPointer + 1;
						return true;
					}
				}
				indexPointer = invConnect.size() - 1 <= indexPointer ? 0 : indexPointer + 1;
			}while (indexPointer != startIndex);
			return false;
		}
		return false;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		for (ConnectionEntry<ISignalReceiver> receiver : signalReceivers){
			if(receiver != null && receiver.hasTile()){
				receiver.getTile().onSignalReceived();
			}
		}
	}

	@Override
	public NBTTagCompound getContentUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();
		if(containingItem != null){
			containingItem.writeToNBT(tag);
		}
		return tag;
	}
	@Override
	public void processCommand(PacketType type, NBTTagCompound tag) {
		super.processCommand(type, tag);
		if(type == PacketType.NBT_CONTENT && tag instanceof NBTTagCompound){
			containingItem = ItemStack.loadItemStackFromNBT(tag);
		}
	}

	@Override
	public void updateFilter() {

	}
	@Override
	public boolean setFilter(EnumFacing side, AbstractFilter filter) {
		if(this.filter[side.getIndex()] != null) return false;
		int n = 0;
		for (int i = 0; i < 6; i++){
			if(this.filter[i] != null) n++;
		}
		if(n >= ConstructFilterUserTileEntity.filterCapability[grade]) return false;
		this.filter[side.getIndex()] = filter;
		return true;
	}
	@Override
	public AbstractFilter getFilter(EnumFacing side) {
		return filter[side.getIndex()];
	}
	@Override
	public ItemStack removeFilter(EnumFacing side) {
		if(filter[side.getIndex()] == null){
			return null;
		}else{
			ItemStack itemStack = filter[side.getIndex()].getFilterItem();
			filter[side.getIndex()] = null;
			return itemStack;
		}
	}
	@Override
	public World getWorldObject() {
		return coreTile.getWorld();
	}
	@Override
	public BlockPos getPosition() {
		return coreTile.getPosition();
	}

	@Override
	public void markDirty() {
		coreTile.markForContentUpdate(xCoord, yCoord, zCoord);
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0 };
	}
	@Override
	public int getSizeInventory() {
		return 1;
	}
	@Override
	public String getName() {
		return "Dispatcher";
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
		return stackLimit;
	}
	@Override
	public void openInventory(EntityPlayer player) {}
	@Override
	public void closeInventory(EntityPlayer player) {}
	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if(slotIndex == 0) return containingItem;
		return null;
	}
	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if(slotIndex == 0 && containingItem != null){
			if(containingItem.stackSize <= amount){
				ItemStack tmpItemStack = containingItem.copy();
				containingItem = null;
				markDirty();
				return tmpItemStack;
			}
			ItemStack splittedItemStack = containingItem.splitStack(amount);
			if(containingItem.stackSize == 0){
				containingItem = null;
			}
			markDirty();
			return splittedItemStack;
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		if(slotIndex == 0){
			containingItem = itemStack;
			if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()){
				itemStack.stackSize = getInventoryStackLimit();
			}
		}
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if(slotIndex == 0){
			ItemStack itemCopy = itemStack.copy();
			itemCopy.stackSize = 1;
			for (ConnectionEntry entry : invConnect){
				if(entry != null && entry.getTile() != null){
					ISidedInventory inv = (ISidedInventory) entry.getTile();
					for (int slot : inv.getSlotsForFace(entry.getFront())){
						if(inv.isItemValidForSlot(slot, itemCopy) && inv.canInsertItem(slot, itemCopy, entry.getFront())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if(flagIO[side.getIndex()] != 0){
			return false;
		}else{
			if(filter[side.getIndex()] != null){
				if(filter[side.getIndex()].canTransferItem(itemStack)){
					return true;
				}
				return false;
			}else{
				return true;
			}
		}
	}
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return ConstructDispatcherTileEntity.nameForNBT;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.readFromNBT(nbtTagCompound, solid);

		stackLimit = maxTransfer[grade];
		indexPointer = nbtTagCompound.getInteger("indexPointer");

		NBTTagCompound itemTag = nbtTagCompound.getCompoundTag("item");
		if(itemTag != null){
			containingItem = ItemStack.loadItemStackFromNBT(itemTag);
		}

		NBTTagCompound sideTag;
		for (int side = 0; side < 6; side++){
			sideTag = nbtTagCompound.getCompoundTag("filter" + side);
			filter[side] = AbstractFilter.createFromNBT(this, EnumFacing.getFront(side), sideTag);
		}

		invConnect.clear();
		NBTTagList invTagList = nbtTagCompound.getTagList("invs", Constants.NBT.TAG_COMPOUND);
		if(invTagList != null){
			for (int tagCounter = 0; tagCounter < invTagList.tagCount(); ++tagCounter){
				ConnectionEntry entry = ConnectionEntry.createFromNBT(invTagList.getCompoundTagAt(tagCounter));
				if(entry.side != -1){
					invConnect.add(entry);
				}else{
					FMLLog.severe("NBT Error", new Object[] {});
				}
			}
		}

		signalReceivers.clear();
		NBTTagList receiverTagList = nbtTagCompound.getTagList("receivers", Constants.NBT.TAG_COMPOUND);
		if(receiverTagList != null){
			for (int tagCounter = 0; tagCounter < receiverTagList.tagCount(); ++tagCounter){
				signalReceivers.add(ConnectionEntry.createFromNBT(receiverTagList.getCompoundTagAt(tagCounter)));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.writeToNBT(nbtTagCompound, solid);

		nbtTagCompound.setInteger("indexPointer", indexPointer);

		if(containingItem != null){
			NBTTagCompound itemTag = new NBTTagCompound();
			containingItem.writeToNBT(itemTag);
			nbtTagCompound.setTag("item", itemTag);
		}

		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound sideTag = new NBTTagCompound();
			filter[side].writeToNBT(sideTag);
			nbtTagCompound.setTag("filter" + side, sideTag);
		}

		NBTTagList invTagList = new NBTTagList();
		for (int i = 0; i < invConnect.size(); i++){
			ConnectionEntry entry = invConnect.get(i);
			if(entry != null && entry.side != -1 && solid.isInclude(entry.getPosition())){
				NBTTagCompound invTag = new NBTTagCompound();
				entry.writeToNBT(invTag);
				invTagList.appendTag(invTag);
			}
		}
		nbtTagCompound.setTag("invs", invTagList);

		NBTTagList receiverTagList = new NBTTagList();
		for (int i = 0; i < signalReceivers.size(); i++){
			ConnectionEntry entry = signalReceivers.get(i);
			if(entry != null && solid.isInclude(entry.getPosition())){
				NBTTagCompound receiverTag = new NBTTagCompound();
				entry.writeToNBT(receiverTag);
				receiverTagList.appendTag(receiverTag);
			}
		}
		nbtTagCompound.setTag("receivers", receiverTagList);

		needUpdateEntry = true;
	}

	@Override
	protected Block getRenderBlock() {
		return TestCore.constructDispatcher[grade];
	}

}
