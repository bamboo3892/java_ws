package com.okina.item;

import static com.okina.main.TestCore.*;

import java.util.List;

import com.okina.client.IToolTipUser;
import com.okina.inventory.CraftingFilterInventory;
import com.okina.inventory.IFilterUser;
import com.okina.main.TestCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ItemCraftingFilter extends Item implements IToolTipUser {

	public ItemCraftingFilter() {
		//TODO
		//		this.setTextureName(TestCore.MODID + ":crafting_filter");
		setUnlocalizedName(AUTHER + ".craftingFilter");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(pos) instanceof IFilterUser){
			if(((IFilterUser) world.getTileEntity(pos)).setFilter(side, new CraftingFilterInventory((IFilterUser) world.getTileEntity(pos), side, stack))){
				--stack.stackSize;
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
						NBTTagCompound materialTag = materialTagList.getCompoundTagAt(tagCounter);
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
	@Override
	public int getNeutralLines() {
		return 0;
	}
	@Override
	public int getShiftLines() {
		return 0;
	}

}
