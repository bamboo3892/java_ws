package com.okina.inventory;

import com.okina.client.gui.ConstructFilterGui;
import com.okina.main.TestCore;
import com.okina.register.CrusherRecipeRegister;
import com.okina.register.EnergyProdeceRecipeRegister;
import com.okina.register.VirtualGrowerRecipeRegister;
import com.okina.server.gui.ConstructFilterContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

public class FilterInventory extends AbstractFilter implements IInventory {

	private ItemStack[] items = new ItemStack[9];
	/**true : black list false : white list*/
	public boolean filterBan;
	public boolean useNBT;
	public boolean useDamage;
	public boolean useOreDictionary;
	/**1 - 6*/
	public int priority = 1;
	private EnumFacing side;
	private IFilterUser tile;

	public FilterInventory(IFilterUser tile, EnumFacing side) {
		this.tile = tile;
		this.side = side;
	}

	@Override
	public boolean onRightClicked(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
		player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + side.getIndex(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new ConstructFilterContainer(player.inventory, this) : new ConstructFilterGui(player.inventory, tile, side);
	}

	@Override
	public boolean canTransferItem(ItemStack itemStack) {
		if(itemStack == null) return false;
		for (int i = 0; i < 9; i++){
			if(items[i] == null) continue;
			Item item = items[i].getItem();
			if(item == TestCore.filtering[0]){
				if(CrusherRecipeRegister.instance.findRecipe(itemStack) != null) return !filterBan;
			}else if(item == TestCore.filtering[1]){
				if(VirtualGrowerRecipeRegister.instance.findRecipe(itemStack) != null) return !filterBan;
			}else if(item == TestCore.filtering[2]){
				if(EnergyProdeceRecipeRegister.instance.findRecipe(itemStack) != null) return !filterBan;
			}else if(item == TestCore.filtering[3]){
				if(FurnaceRecipes.instance().getSmeltingResult(itemStack) != null) return !filterBan;
			}else{
				if(useOreDictionary){
					if(isMatches(itemStack, items[i])) return !filterBan;
				}
				boolean flag = itemStack.getItem() == items[i].getItem();
				if(useNBT && flag){
					flag = ItemStack.areItemStackTagsEqual(itemStack, items[i]);
				}
				if(useDamage && flag){
					flag = itemStack.getItemDamage() == items[i].getItemDamage();
				}
				if(flag) return !filterBan;
			}
		}
		return filterBan;
	}

	private boolean isMatches(ItemStack item1, ItemStack item2) {
		if(item1 == null || item2 == null) return false;
		int[] id1 = OreDictionary.getOreIDs(item1);
		int[] id2 = OreDictionary.getOreIDs(item2);
		if(id1 == null || id2 == null) return false;
		for (int i1 : id1){
			for (int i2 : id2){
				if(i1 == i2) return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getFilterItem() {
		return new ItemStack(TestCore.filter, 1);
	}
	@Override
	public int getPriority() {
		return priority;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getSizeInventory() {
		return 9;
	}
	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if(slotIndex < 0 || slotIndex >= 9) return null;
		return items[slotIndex];
	}
	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		items[slotIndex] = null;
		markDirty();
		return null;
	}
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack item) {
		items[slotIndex] = item;
		if(item != null && item.stackSize > getInventoryStackLimit()){
			item.stackSize = getInventoryStackLimit();
		}
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
		return "Filter " + side;
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
	public void markDirty() {
		tile.updateFilter();
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}
	@Override
	public void openInventory(EntityPlayer player) {}
	@Override
	public void closeInventory(EntityPlayer player) {}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return slot >= 0 && slot <= 8;
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
	public void readFromNBT(NBTTagCompound tag) {
		if(tag.getInteger("filterId") != 1) return;

		NBTTagList itemsTagList = tag.getTagList("items", Constants.NBT.TAG_COMPOUND);
		items = new ItemStack[getSizeInventory()];
		for (int tagCounter = 0; tagCounter < itemsTagList.tagCount(); ++tagCounter){
			NBTTagCompound itemTagCompound = itemsTagList.getCompoundTagAt(tagCounter);
			byte slotIndex = itemTagCompound.getByte("slot");
			if(slotIndex >= 0 && slotIndex < items.length){
				items[slotIndex] = ItemStack.loadItemStackFromNBT(itemTagCompound);
			}
		}

		NBTTagCompound flags = tag.getCompoundTag("flags");
		filterBan = flags.getBoolean("ban");
		useNBT = flags.getBoolean("nbt");
		useDamage = flags.getBoolean("damage");
		useOreDictionary = flags.getBoolean("ore");
		priority = flags.getInteger("priority");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("filterId", 1);

		NBTTagList itemsTagList = new NBTTagList();
		for (int slotIndex = 0; slotIndex < items.length; ++slotIndex){
			if(items[slotIndex] != null){
				NBTTagCompound itemTagCompound = new NBTTagCompound();
				itemTagCompound.setByte("slot", (byte) slotIndex);
				items[slotIndex].writeToNBT(itemTagCompound);
				itemsTagList.appendTag(itemTagCompound);
			}
		}
		tag.setTag("items", itemsTagList);

		NBTTagCompound flags = new NBTTagCompound();
		flags.setBoolean("ban", filterBan);
		flags.setBoolean("nbt", useNBT);
		flags.setBoolean("damage", useDamage);
		flags.setBoolean("ore", useOreDictionary);
		flags.setInteger("priority", priority);
		tag.setTag("flags", flags);
	}

}
