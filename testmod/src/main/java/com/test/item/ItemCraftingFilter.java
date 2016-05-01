package com.test.item;

import java.util.List;

import com.test.client.IToolTipUser;
import com.test.inventory.CraftingFilterInventory;
import com.test.inventory.IFilterUser;
import com.test.main.TestCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ItemCraftingFilter extends Item implements IToolTipUser {

	public ItemCraftingFilter() {
		this.setTextureName(TestCore.MODID + ":crafting_filter");
		this.setUnlocalizedName("craftingFilter");
		this.setCreativeTab(TestCore.testCreativeTab);
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof IFilterUser){
			if(((IFilterUser) world.getTileEntity(x, y, z)).setFilter(side, new CraftingFilterInventory((IFilterUser) world.getTileEntity(x, y, z), side, itemStack))){
				--itemStack.stackSize;
			}
		}
		return !world.isRemote;
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		if(shiftPressed){
			NBTTagCompound tag = itemStack.getTagCompound();
			if(tag != null){
				NBTTagCompound productTag = tag.getCompoundTag("product");
				NBTTagList materialTagList = tag.getTagList("material", Constants.NBT.TAG_COMPOUND);
				if(productTag != null && materialTagList != null){
					ItemStack product = ItemStack.loadItemStackFromNBT(productTag);
					ItemStack[] material = new ItemStack[materialTagList.tagCount()];
					for (int tagCounter = 0; tagCounter < materialTagList.tagCount(); ++tagCounter){
						NBTTagCompound materialTag = (NBTTagCompound) materialTagList.getCompoundTagAt(tagCounter);
						material[tagCounter] = ItemStack.loadItemStackFromNBT(materialTag);
					}
					if(product != null){
						toolTip.add("Product : " + product.getDisplayName() + " x" + product.stackSize);
						toolTip.add("Material :");
						for (int i = 0; i < material.length; i++){
							if(material[i] != null){
								toolTip.add("   " + material[i].getDisplayName() + " x" + material[i].stackSize);
							}
						}
					}
				}
			}
		}
	}

}