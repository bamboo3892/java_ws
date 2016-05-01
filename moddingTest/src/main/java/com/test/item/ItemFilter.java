package com.test.item;

import com.test.inventory.FilterInventory;
import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructFilterUserTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFilter extends Item {

	public ItemFilter() {
		this.setTextureName(TestCore.MODID + ":filter");
		setUnlocalizedName("filter");
		setCreativeTab(TestCore.testCreativeTab);
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof ConstructFilterUserTileEntity){
			ConstructFilterUserTileEntity tile = (ConstructFilterUserTileEntity) world.getTileEntity(x, y, z);
			if(tile.setFilter(side, new FilterInventory(tile, side))){
				--itemStack.stackSize;
				world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			}
		}
		return !world.isRemote;
	}

}