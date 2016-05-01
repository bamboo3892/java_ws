package com.test.inventory;

import com.test.multiblock.construct.tileentity.ConstructFilterUserTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class AbstractFilter {

	public abstract void onRightClicked(World world, int x, int y, int z, int side, EntityPlayer player);

	public abstract ItemStack getFilterItem();

	public abstract boolean tranferItem(ConstructFilterUserTileEntity start, IInventory goal, int startSide, int goalSide);

	public abstract boolean canTransferItem(ItemStack itemStack);

	public abstract void readFromNBT(NBTTagCompound tag);

	public abstract void writeToNBT(NBTTagCompound tag);

	public static AbstractFilter createFromNBT(ConstructFilterUserTileEntity tile, int side, NBTTagCompound tag) {
		if(tag == null || !tag.hasKey("filterId")) return null;
		int id = tag.getInteger("filterId");
		if(id == 1){
			FilterInventory filter = new FilterInventory(tile, side);
			filter.readFromNBT(tag);
			return filter;
		}else if(id == 2){
			CraftingFilterInventory filter = new CraftingFilterInventory();
			filter.readFromNBT(tag);
			return filter;
		}
		return null;
	}

}
