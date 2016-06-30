package com.okina.multiblock.construct.tileentity;

import java.util.ArrayList;
import java.util.Random;

import com.okina.client.gui.ConstructContainerGui;
import com.okina.inventory.AbstractFilter;
import com.okina.inventory.CraftingFilterInventory;
import com.okina.inventory.FilterInventory;
import com.okina.inventory.IFilterUser;
import com.okina.main.GuiHandler.IGuiTile;
import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.server.gui.ConstructContainerContainer;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;
import com.okina.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

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
	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && (player.getHeldItem().getItem() == TestCore.filter || player.getHeldItem().getItem() == TestCore.craftingFilter)) return false;
		if(filter[side.getIndex()] != null){
			filter[side.getIndex()].onRightClicked(worldObj, pos, side, player);
		}else{
			player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side.getIndex(), worldObj, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	@Override
	public boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			spawnCennectionParticle();
		}
		return true;
	}
	@Override
	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		//checkConnection();
		if(!player.isSneaking()){
			int n = changeIO(side);
			if(worldObj.getTileEntity(pos.add(side.getDirectionVec())) instanceof BlockPipeTileEntity){
				((BlockPipeTileEntity) worldObj.getTileEntity(pos.add(side.getDirectionVec()))).checkConnection();
			}
			TestCore.proxy.markForTileUpdate(pos, PacketType.FLAG_IO);
			if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(n == 0 ? "input" : n == 1 ? "output" : "disabled"));
		}else{
			if(worldObj.isRemote){
				//do nothing
			}else{
				//update all connection
				checkAllConnection();
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, 0));
				player.addChatMessage(new ChatComponentText("Update All the Connections"));
			}
		}
		return false;
	}

	@Override
	public void onLeftClicked(EntityPlayer player, MovingObjectPosition mop) {
		if(!worldObj.isRemote){
			if(player.isSneaking() && filter[mop.sideHit.getIndex()] != null){
				ItemStack filter = removeFilter(mop.sideHit);
				if(filter != null){
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + mop.sideHit.getFrontOffsetX() + 0.5, pos.getY() + mop.sideHit.getFrontOffsetY() + 0.5, pos.getZ() + mop.sideHit.getFrontOffsetZ() + 0.5, filter));
				}
			}
		}
	}

	public int changeIO(EnumFacing side) {
		flagIO[side.getIndex()] = flagIO[side.getIndex()] == 2 ? 0 : flagIO[side.getIndex()] + 1;
		if(!worldObj.isRemote){
			if(flagIO[side.getIndex()] != 0 && filter[side.getIndex()] != null){
				ItemStack filter = removeFilter(side);
				if(filter != null){
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + side.getFrontOffsetX() + 0.5, pos.getY() + side.getFrontOffsetY() + 0.5, pos.getZ() + side.getFrontOffsetZ() + 0.5, filter));
				}
			}
			checkAllConnection();
		}
		return flagIO[side.getIndex()];
	}

	@Override
	public void update() {
		super.update();
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
		for (EnumFacing side : EnumFacing.VALUES){
			ArrayList<ConnectionEntry> invs = new ArrayList<ConnectionEntry>();
			invs.add(new ConnectionEntry(this, side));
			BlockPos newPos = pos.add(side.getDirectionVec());
			TileEntity tile = worldObj.getTileEntity(newPos);
			if(tile instanceof BlockPipeTileEntity && !tile.isInvalid()){
				BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
				pipe.findConnection(checkedPipes, invs, IConstructInventory.class, true);
			}else if(tile != null && tile instanceof IConstructInventory){
				if(tile instanceof ConstructBaseTileEntity){
					ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) tile;
					if(baseTile.flagIO[side.getOpposite().getIndex()] == 0){
						invs.add(new ConnectionEntry(baseTile, side.getOpposite().getIndex()));
					}
				}
			}
			invs.remove(0);
			invConnect.addAll(invs);
		}

		signalReceivers.clear();
		signalReceivers.add(new ConnectionEntry(this));
		checkedPipes.clear();
		for (EnumFacing side : EnumFacing.VALUES){
			BlockPos newPos = pos.add(side.getDirectionVec());
			TileEntity tile = worldObj.getTileEntity(newPos);
			if(tile instanceof BlockPipeTileEntity && !tile.isInvalid()){
				BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
				pipe.findConnection(checkedPipes, signalReceivers, ISignalReceiver.class, false);
			}else if(tile != null && tile instanceof ISignalReceiver){
				if(tile instanceof ConstructBaseTileEntity){
					ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) tile;
					if(baseTile.flagIO[side.getOpposite().getIndex()] == 0){
						signalReceivers.add(new ConnectionEntry(baseTile));
					}
				}
			}
		}
		signalReceivers.remove(0);
	}

	private boolean itemTransfer() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));

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

	@Override
	public void onTileRemoved() {
		Random rand = worldObj.rand;
		for (int i = 0; i < 6; i++){
			if(filter[i] != null){
				ItemStack itemStack = filter[i].getFilterItem();
				double x = pos.getX() + rand.nextFloat() * 0.8F + 0.1F;
				double y = pos.getY() + rand.nextFloat() * 0.8F + 0.1F;
				double z = pos.getZ() + rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem = new EntityItem(worldObj, x, y, z, itemStack);
				entityitem.motionX = (float) rand.nextGaussian() * 0.05F;
				entityitem.motionY = (float) rand.nextGaussian() * 0.05F + 0.2F;
				entityitem.motionZ = (float) rand.nextGaussian() * 0.05F;
				worldObj.spawnEntityInWorld(entityitem);
			}
		}
		for (int i1 = 0; i1 < getSizeInventory(); ++i1){
			ItemStack itemstack = getStackInSlot(i1);

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
					entityitem = new EntityItem(worldObj, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
					float f3 = 0.05F;
					entityitem.motionX = (float) rand.nextGaussian() * f3;
					entityitem.motionY = (float) rand.nextGaussian() * f3 + 0.2F;
					entityitem.motionZ = (float) rand.nextGaussian() * f3;

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
		checkAllConnection();
		for (ConnectionEntry entry : signalReceivers){
			if(worldObj.getTileEntity(entry.getPosition()) instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) worldObj.getTileEntity(entry.getPosition());
				baseTile.restRenderTicks = 100;
				baseTile.renderSide = entry.side;
			}
		}
		for (ConnectionEntry entry : invConnect){
			if(worldObj.getTileEntity(entry.getPosition()) instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) worldObj.getTileEntity(entry.getPosition());
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
			if(containingItem != null){
				containingItem.writeToNBT(tag);
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
			spawnCennectionParticle();
		}else if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			containingItem = ItemStack.loadItemStackFromNBT(tag);
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
				filter[side] = AbstractFilter.createFromNBT(this, EnumFacing.getFront(side), sideTag);
			}
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	public void updateFilter() {
		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.FILTER2);
	}
	@Override
	public boolean setFilter(EnumFacing side, AbstractFilter filter) {
		if(this.filter[side.getIndex()] != null || flagIO[side.getIndex()] != 0 || filter instanceof CraftingFilterInventory) return false;
		int n = 0;
		for (int i = 0; i < 6; i++){
			if(this.filter[i] != null) n++;
		}
		if(n >= filterCapability[grade]) return false;
		this.filter[side.getIndex()] = filter;
		updateFilter();
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
			updateFilter();
			return itemStack;
		}
	}
	@Override
	public World getWorldObject() {
		return worldObj;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}
	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}
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
	public void openInventory(EntityPlayer player) {}
	@Override
	public void closeInventory(EntityPlayer player) {}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(worldObj.getTileEntity(pos) != this){
			return false;
		}
		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 50000.0D;
	}
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if(slotIndex == 0 && !worldObj.isRemote){
			ItemStack itemCopy = itemStack.copy();
			itemCopy.stackSize = 1;
			for (ConnectionEntry entry : invConnect){
				if(entry != null && entry.getTile() != null){
					IConstructInventory inv = (IConstructInventory) entry.getTile();
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
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0 };
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
		checkAllConnection();

		tag.setInteger("indexPointer", indexPointer);

		if(containingItem != null){
			NBTTagCompound itemTag = new NBTTagCompound();
			containingItem.writeToNBT(itemTag);
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
			containingItem = ItemStack.loadItemStackFromNBT(itemTag);
		}

		NBTTagCompound sideTag;
		for (int side = 0; side < 6; side++){
			sideTag = nbtTagCompound.getCompoundTag("filter" + side);
			filter[side] = AbstractFilter.createFromNBT(this, EnumFacing.getFront(side), sideTag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
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
	}

}
