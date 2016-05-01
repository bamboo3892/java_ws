package com.test.multiblock.construct.tileentity;

import java.util.ArrayList;
import java.util.Random;

import com.test.client.gui.ConstructContainerGui;
import com.test.inventory.AbstractFilter;
import com.test.inventory.CraftingFilterInventory;
import com.test.inventory.FilterInventory;
import com.test.inventory.IFilterUser;
import com.test.main.GuiHandler.IGuiTile;
import com.test.main.TestCore;
import com.test.multiblock.BlockPipeTileEntity;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.server.gui.ConstructContainerContainer;
import com.test.utils.ConnectionEntry;
import com.test.utils.InventoryHelper;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ConstructDispatcherTileEntity extends ConstructBaseTileEntity implements IPipeConnectionUser, IConstructInventory, IFilterUser, IGuiTile, ISignalReceiver {

	public static final String nameForNBT = "dispatcher";
	public static final int[] filterCapability = { 0, 2, 4, 5, 6 };

	public int stackLimit = 1;
	public ItemStack containingItem;

	private ArrayList<ConnectionEntry> invConnect = new ArrayList();
	private ArrayList<ConnectionEntry> signalReceivers = new ArrayList();
	private AbstractFilter[] filter = new AbstractFilter[6];
	private int indexPointer;

	private boolean needUpdateEntry = true;

	public ConstructDispatcherTileEntity() {
		this(0);
	}

	public ConstructDispatcherTileEntity(int grade) {
		super(grade);
		stackLimit = maxTransfer[grade];
	}

	@Override
	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && (player.getHeldItem().getItem() == TestCore.filter || player.getHeldItem().getItem() == TestCore.craftingFilter)) return false;
		if(filter[side] != null){
			filter[side].onRightClicked(worldObj, xCoord, yCoord, zCoord, side, player);
		}else{
			player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side, worldObj, xCoord, yCoord, zCoord);
		}
		return true;
	}

	@Override
	public boolean onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			this.spawnCennectionParticle();
		}
		return true;
	}

	@Override
	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		//checkConnection();
		if(!player.isSneaking()){
			int n = changeIO(side);
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
				((BlockPipeTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
			}
			TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
			if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(n == 0 ? "input" : n == 1 ? "output" : "disabled"));
		}else{
			if(worldObj.isRemote){
				//do nothing
			}else{
				//update all connection
				this.checkAllConnection();
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, 0));
				player.addChatMessage(new ChatComponentText("Update All the Connections"));
			}
		}
		return false;
	}

	@Override
	public void onLeftClicked(EntityPlayer player, int side, double hitX, double hitY, double hitZ) {
		if(!worldObj.isRemote){
			if(player.isSneaking() && filter[side] != null){
				ItemStack filter = this.removeFilter(side);
				if(filter != null){
					ForgeDirection dir = ForgeDirection.getOrientation(side);
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, filter));
				}
			}
		}
	}

	public int changeIO(int side) {
		if(side < 0 || side >= 6) return 3;
		flagIO[side] = flagIO[side] == 2 ? 0 : flagIO[side] + 1;
		if(!worldObj.isRemote){
			if(flagIO[side] != 0 && filter[side] != null){
				ItemStack filter = this.removeFilter(side);
				if(filter != null){
					ForgeDirection dir = ForgeDirection.getOrientation(side);
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, filter));
				}
			}
			checkAllConnection();
		}
		return flagIO[side];
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(worldObj.isRemote){
			//do nothing
		}else{
			if(needUpdateEntry){
				checkAllConnection();
				needUpdateEntry = false;
			}
			for (int i = 0; i < maxTransfer[grade]; i++){
				if(!itemTransfer()) break;
			}
		}
	}

	private void checkAllConnection() {
		ArrayList<BlockPipeTileEntity> checkedPipes = new ArrayList<BlockPipeTileEntity>();
		invConnect.clear();
		for (int side = 0; side < 6; side++){
			ArrayList<ConnectionEntry> invs = new ArrayList<ConnectionEntry>();
			invs.add(new ConnectionEntry(this, ForgeDirection.getOrientation(side).ordinal()));
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			int newX = xCoord + dir.offsetX;
			int newY = yCoord + dir.offsetY;
			int newZ = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(newX, newY, newZ);
			if(tile instanceof BlockPipeTileEntity && !tile.isInvalid()){
				BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
				pipe.findConnection(checkedPipes, invs, IConstructInventory.class, true);
			}else if(tile != null && tile instanceof IConstructInventory){
				if(tile instanceof ConstructBaseTileEntity){
					ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) tile;
					if(baseTile.flagIO[dir.getOpposite().ordinal()] == 0){
						invs.add(new ConnectionEntry((IConstructInventory) baseTile, ForgeDirection.getOrientation(side).getOpposite().ordinal()));
					}
				}
			}
			invs.remove(0);
			invConnect.addAll(invs);
		}

		signalReceivers.clear();
		signalReceivers.add(new ConnectionEntry(this));
		checkedPipes.clear();
		for (int side = 0; side < 6; side++){
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			int newX = xCoord + dir.offsetX;
			int newY = yCoord + dir.offsetY;
			int newZ = zCoord + dir.offsetZ;
			TileEntity tile = worldObj.getTileEntity(newX, newY, newZ);
			if(tile instanceof BlockPipeTileEntity && !tile.isInvalid()){
				BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
				pipe.findConnection(checkedPipes, signalReceivers, ISignalReceiver.class, false);
			}else if(tile != null && tile instanceof ISignalReceiver){
				if(tile instanceof ConstructBaseTileEntity){
					ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) tile;
					if(baseTile.flagIO[dir.getOpposite().ordinal()] == 0){
						signalReceivers.add(new ConnectionEntry((ISignalReceiver) baseTile));
					}
				}
			}
		}
		signalReceivers.remove(0);
	}

	private boolean itemTransfer() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));

		if(invConnect.size() > 0 && this.containingItem != null){
			ItemStack targetStack = containingItem.copy();
			targetStack.stackSize = 1;

			if(invConnect.size() - 1 < indexPointer){
				indexPointer = 0;
			}
			int startIndex = indexPointer;
			do{
				ConnectionEntry<IConstructInventory> entry = invConnect.get(indexPointer);
				if(entry != null && entry.hasTile()){
					ItemStack rest = InventoryHelper.tryPushItem(entry.getTile(), ForgeDirection.getOrientation(entry.side), targetStack, InventoryHelper.WHOLE);
					if(rest == null || rest.stackSize <= 0){
						this.containingItem.stackSize--;
						if(containingItem.stackSize == 0){
							containingItem = null;
						}
						this.markDirty();
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

	@Override
	public void onTileRemoved() {
		Random rand = worldObj.rand;
		for (int i = 0; i < 6; i++){
			if(filter[i] != null){
				ItemStack itemStack = filter[i].getFilterItem();
				double x = (double) ((float) xCoord + rand.nextFloat() * 0.8F + 0.1F);
				double y = (double) ((float) yCoord + rand.nextFloat() * 0.8F + 0.1F);
				double z = (double) ((float) zCoord + rand.nextFloat() * 0.8F + 0.1F);
				EntityItem entityitem = new EntityItem(worldObj, x, y, z, itemStack);
				entityitem.motionX = (double) ((float) rand.nextGaussian() * 0.05F);
				entityitem.motionY = (double) ((float) rand.nextGaussian() * 0.05F + 0.2F);
				entityitem.motionZ = (double) ((float) rand.nextGaussian() * 0.05F);
				worldObj.spawnEntityInWorld(entityitem);
			}
		}
		for (int i1 = 0; i1 < this.getSizeInventory(); ++i1){
			ItemStack itemstack = this.getStackInSlot(i1);

			if(itemstack != null){
				float f = rand.nextFloat() * 0.8F + 0.1F;
				float f1 = rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem;

				for (float f2 = rand.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; worldObj.spawnEntityInWorld(entityitem)){
					int j1 = rand.nextInt(21) + 10;

					if(j1 > itemstack.stackSize){
						j1 = itemstack.stackSize;
					}

					itemstack.stackSize -= j1;
					entityitem = new EntityItem(worldObj, (double) ((float) xCoord + f), (double) ((float) yCoord + f1), (double) ((float) zCoord + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
					float f3 = 0.05F;
					entityitem.motionX = (double) ((float) rand.nextGaussian() * f3);
					entityitem.motionY = (double) ((float) rand.nextGaussian() * f3 + 0.2F);
					entityitem.motionZ = (double) ((float) rand.nextGaussian() * f3);

					if(itemstack.hasTagCompound()){
						entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
					}
				}
			}
		}
	}

	/**client only*/
	private void spawnCennectionParticle() {
		if(!worldObj.isRemote) return;
		this.checkAllConnection();
		for (ConnectionEntry entry : signalReceivers){
			if(worldObj.getTileEntity(entry.x, entry.y, entry.z) instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) worldObj.getTileEntity(entry.x, entry.y, entry.z);
				baseTile.restRenderTicks = 100;
				baseTile.renderSide = entry.side;
			}
		}
		for (ConnectionEntry entry : invConnect){
			if(worldObj.getTileEntity(entry.x, entry.y, entry.z) instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) worldObj.getTileEntity(entry.x, entry.y, entry.z);
				baseTile.restRenderTicks = 100;
				baseTile.renderSide = entry.side;
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		for (ConnectionEntry<ISignalReceiver> receiver : signalReceivers){
			if(receiver != null && receiver.hasTile()){
				receiver.getTile().onSignalReceived();
			}
		}
	}

	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.NBT_CONTENT){
			NBTTagCompound tag = new NBTTagCompound();
			if(this.containingItem != null){
				this.containingItem.writeToNBT(tag);
			}
			return new SimpleTilePacket(this, PacketType.NBT_CONTENT, tag);
		}else if(type == PacketType.FILTER2){
			NBTTagCompound tag = new NBTTagCompound();
			for (int side = 0; side < 6; side++){
				if(filter[side] == null) continue;
				NBTTagCompound filterTag = new NBTTagCompound();
				filter[side].writeToNBT(filterTag);
				tag.setTag("filter" + side, filterTag);
			}
			return new SimpleTilePacket(this, PacketType.FILTER2, tag);
		}
		return super.getPacket(type);
	}
	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.EFFECT){
			this.spawnCennectionParticle();
		}else if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			this.containingItem = ItemStack.loadItemStackFromNBT(tag);
		}else if(type == PacketType.FILTER && value instanceof String){
			String str = (String) value;
			int side = Character.getNumericValue(str.charAt(0));
			if(filter[side] instanceof FilterInventory){
				FilterInventory f = (FilterInventory) filter[side];
				int damage = Character.getNumericValue(str.charAt(1));
				int nbt = Character.getNumericValue(str.charAt(2));
				int oreDict = Character.getNumericValue(str.charAt(3));
				int ban = Character.getNumericValue(str.charAt(4));
				int priority = Character.getNumericValue(str.charAt(5));
				if(side >= 0 && side <= 5 && damage != -1 && nbt != -1 && oreDict != -1 && ban != -1 && priority != -1){
					f.useDamage = damage == 1;
					f.useNBT = nbt == 1;
					f.useOreDictionary = oreDict == 1;
					f.filterBan = ban == 1;
					f.priority = priority;
				}
			}
		}else if(type == PacketType.FILTER2 && value instanceof NBTTagCompound){
			for (int side = 0; side < 6; side++){
				NBTTagCompound sideTag = ((NBTTagCompound) value).getCompoundTag("filter" + side);
				filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
			}
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}
	}

	public void updateFilter() {
		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.FILTER2);
	}
	public boolean setFilter(int side, AbstractFilter filter) {
		if(this.filter[side] != null || flagIO[side] != 0 || filter instanceof CraftingFilterInventory) return false;
		int n = 0;
		for (int i = 0; i < 6; i++){
			if(this.filter[i] != null) n++;
		}
		if(n >= filterCapability[grade]) return false;
		this.filter[side] = filter;
		this.updateFilter();
		return true;
	}
	public AbstractFilter getFilter(int side) {
		if(side >= 0 && side < 6){
			return this.filter[side];
		}
		return null;
	}
	public ItemStack removeFilter(int side) {
		if(filter[side] == null){
			return null;
		}else{
			ItemStack itemStack = filter[side].getFilterItem();
			filter[side] = null;
			this.updateFilter();
			return itemStack;
		}
	}
	public World getWorldObject() {
		return this.worldObj;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int slotIndex) {
		return new int[] { 0 };
	}
	@Override
	public int getSizeInventory() {
		return 1;
	}
	@Override
	public String getInventoryName() {
		return "Dispatcher";
	}
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	@Override
	public int getInventoryStackLimit() {
		return this.stackLimit;
	}
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}
	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if(slotIndex == 0) return this.containingItem;
		return null;
	}
	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if(slotIndex == 0 && this.containingItem != null){
			if(this.containingItem.stackSize <= amount){
				ItemStack tmpItemStack = this.containingItem.copy();
				this.containingItem = null;
				this.markDirty();
				return tmpItemStack;
			}
			ItemStack splittedItemStack = this.containingItem.splitStack(amount);
			if(this.containingItem.stackSize == 0){
				this.containingItem = null;
			}
			this.markDirty();
			return splittedItemStack;
		}
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		if(slotIndex == 0){
			return this.containingItem;
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		if(slotIndex == 0){
			this.containingItem = itemStack;
			if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()){
				itemStack.stackSize = getInventoryStackLimit();
			}
		}
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(worldObj.getTileEntity(xCoord, yCoord, zCoord) != this){
			return false;
		}
		return player.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 50000.0D;
	}
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if(slotIndex == 0 && !worldObj.isRemote){
			ItemStack itemCopy = itemStack.copy();
			itemCopy.stackSize = 1;
			for (ConnectionEntry entry : invConnect){
				if(entry != null && entry.getTile() != null){
					IConstructInventory inv = (IConstructInventory) entry.getTile();
					for (int slot : inv.getAccessibleSlotsFromSide(entry.side)){
						if(inv.isItemValidForSlot(slot, itemCopy) && inv.canInsertItem(slot, itemCopy, entry.side)){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemStack, int side) {
		if(flagIO[side] != 0){
			return false;
		}else{
			if(filter[side] != null){
				if(filter[side].canTransferItem(itemStack)){
					return true;
				}
				return false;
			}else{
				return true;
			}
		}
	}
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		return flagIO[side] == 1;
	}

	@Override
	public void needUpdateEntry() {
		needUpdateEntry = true;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return filter[side] != null ? filter[side].getGuiElement(player, side, serverSide) : (serverSide ? new ConstructContainerContainer(player.inventory, this) : new ConstructContainerGui(player.inventory, this));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		this.checkAllConnection();

		tag.setInteger("indexPointer", indexPointer);

		if(this.containingItem != null){
			NBTTagCompound itemTag = new NBTTagCompound();
			this.containingItem.writeToNBT(itemTag);
			tag.setTag("item", itemTag);
		}

		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound sideTag = new NBTTagCompound();
			filter[side].writeToNBT(sideTag);
			tag.setTag("filter" + side, sideTag);
		}

		NBTTagList invTagList = new NBTTagList();
		for (int i = 0; i < invConnect.size(); i++){
			ConnectionEntry entry = invConnect.get(i);
			if(entry != null && solid.isInclude(entry.getPosition())){
				NBTTagCompound invTag = new NBTTagCompound();
				invTag.setInteger("x", entry.x - solid.minX);
				invTag.setInteger("y", entry.y - solid.minY);
				invTag.setInteger("z", entry.z - solid.minZ);
				invTag.setInteger("side", entry.side);
				invTagList.appendTag(invTag);
			}
		}
		tag.setTag("invs", invTagList);

		NBTTagCompound receiverTagList = new NBTTagCompound();
		receiverTagList.setInteger("size", signalReceivers.size());
		for (int i = 0; i < signalReceivers.size(); i++){
			ConnectionEntry entry = signalReceivers.get(i);
			if(entry != null && solid.isInclude(entry.getPosition())){
				NBTTagCompound invTag = new NBTTagCompound();
				invTag.setInteger("x", entry.x - solid.minX);
				invTag.setInteger("y", entry.y - solid.minY);
				invTag.setInteger("z", entry.z - solid.minZ);
				invTag.setInteger("side", entry.side);
				invTagList.appendTag(invTag);
			}
		}
		tag.setTag("receivers", receiverTagList);
	}

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);

		stackLimit = maxTransfer[grade];
		indexPointer = nbtTagCompound.getInteger("indexPointer");

		NBTTagCompound itemTag = nbtTagCompound.getCompoundTag("item");
		if(itemTag != null){
			this.containingItem = ItemStack.loadItemStackFromNBT(itemTag);
		}

		NBTTagCompound sideTag;
		for (int side = 0; side < 6; side++){
			sideTag = nbtTagCompound.getCompoundTag("filter" + side);
			filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setInteger("indexPointer", indexPointer);
		if(this.containingItem != null){
			NBTTagCompound itemTag = new NBTTagCompound();
			this.containingItem.writeToNBT(itemTag);
			nbtTagCompound.setTag("item", itemTag);
		}
		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound sideTag = new NBTTagCompound();
			filter[side].writeToNBT(sideTag);
			nbtTagCompound.setTag("filter" + side, sideTag);
		}
	}

}
