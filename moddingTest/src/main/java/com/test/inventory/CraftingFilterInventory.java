package com.test.inventory;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructFilterUserTileEntity;
import com.test.utils.InventoryHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class CraftingFilterInventory extends AbstractFilter {

	private ItemStack product;
	private ItemStack[] materials;

	public CraftingFilterInventory() {

	}

	public CraftingFilterInventory(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if(item == TestCore.craftingFilter){
			NBTTagCompound tag = itemStack.getTagCompound();
			if(tag != null){
				NBTTagCompound productTag = tag.getCompoundTag("product");
				NBTTagList materialTagList = tag.getTagList("material", Constants.NBT.TAG_COMPOUND);
				if(productTag != null && materialTagList != null){
					product = ItemStack.loadItemStackFromNBT(productTag);
					materials = new ItemStack[materialTagList.tagCount()];
					for (int tagCounter = 0; tagCounter < materialTagList.tagCount(); ++tagCounter){
						NBTTagCompound materialTag = (NBTTagCompound) materialTagList.getCompoundTagAt(tagCounter);
						materials[tagCounter] = ItemStack.loadItemStackFromNBT(materialTag);
					}
				}
			}
		}
	}

	@Override
	public void onRightClicked(World world, int x, int y, int z, int side, EntityPlayer player) {

	}

	@Override
	public boolean tranferItem(ConstructFilterUserTileEntity start, IInventory goal, int startSide, int goalSide) {
		if(materials != null && product != null){
			ItemStack[] mat = new ItemStack[materials.length];
			for (int j = 0; j < mat.length; j++){
				mat[j] = materials[j].copy();
			}
			int[] accessible = start.getAccessibleSlotsFromSide(startSide);
			if(accessible != null){
				ItemStack[] startInv = new ItemStack[accessible.length];
				for (int i = 0; i < accessible.length; i++){
					if(start.getStackInSlot(accessible[i]) != null){
						startInv[i] = start.getStackInSlot(accessible[i]).copy();
						for (int j = 0; j < mat.length; j++){
							if(startInv[i].isItemEqual(mat[j]) && ItemStack.areItemStackTagsEqual(startInv[i], mat[j]) && mat[j].stackSize > 0){
								int amountToUse = Math.min(mat[j].stackSize, startInv[i].stackSize);
								mat[j].stackSize -= amountToUse;
								startInv[i].stackSize -= amountToUse;
							}
						}
					}
				}
				for (int j = 0; j < mat.length; j++){
					if(mat[j].stackSize > 0) return false;
				}

				//can craft
				if(InventoryHelper.tryPushItem(start.getWorldObj(), goal, ForgeDirection.getOrientation(goalSide), product.copy(), true)){
					for (int i = 0; i < accessible.length; i++){
						if(startInv[i] != null && startInv[i].stackSize == 0) startInv[i] = null;
						start.setInventorySlotContents(accessible[i], startInv[i]);
					}
					InventoryHelper.tryPushItem(start.getWorldObj(), goal, ForgeDirection.getOrientation(goalSide), product.copy(), false);
					start.markDirty();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ItemStack getFilterItem() {
		ItemStack itemStack = new ItemStack(TestCore.craftingFilter, 1);
		NBTTagCompound tag = new NBTTagCompound();

		if(product != null){
			NBTTagCompound productTag = new NBTTagCompound();
			product.writeToNBT(productTag);
			tag.setTag("product", productTag);
		}

		if(materials != null){
			NBTTagList itemsTagList = new NBTTagList();
			for (int i = 0; i < materials.length; i++){
				if(materials[i] != null){
					NBTTagCompound itemTag = new NBTTagCompound();
					materials[i].writeToNBT(itemTag);
					itemsTagList.appendTag(itemTag);
				}
			}
			tag.setTag("material", itemsTagList);
		}
		itemStack.setTagCompound(tag);
		return itemStack;
	}

	@Override
	public boolean canTransferItem(ItemStack itemStack) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if(tag.getInteger("filterId") != 2) return;

		NBTTagCompound productTag = tag.getCompoundTag("product");
		NBTTagList materialTagList = tag.getTagList("material", Constants.NBT.TAG_COMPOUND);
		if(productTag != null && materialTagList != null){
			product = ItemStack.loadItemStackFromNBT(productTag);
			materials = new ItemStack[materialTagList.tagCount()];
			for (int tagCounter = 0; tagCounter < materialTagList.tagCount(); ++tagCounter){
				NBTTagCompound materialTag = (NBTTagCompound) materialTagList.getCompoundTagAt(tagCounter);
				materials[tagCounter] = ItemStack.loadItemStackFromNBT(materialTag);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("filterId", 2);

		if(product != null){
			NBTTagCompound productTag = new NBTTagCompound();
			product.writeToNBT(productTag);
			tag.setTag("product", productTag);
		}

		if(materials != null){
			NBTTagList itemsTagList = new NBTTagList();
			for (int i = 0; i < materials.length; i++){
				if(materials[i] != null){
					NBTTagCompound itemTag = new NBTTagCompound();
					materials[i].writeToNBT(itemTag);
					itemsTagList.appendTag(itemTag);
				}
			}
			tag.setTag("material", itemsTagList);
		}

	}

}
