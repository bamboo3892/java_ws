package com.test.item;

import com.test.inventory.CraftingFilterInventory;
import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructFilterUserTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCraftingFilter extends Item {

	public ItemCraftingFilter() {
		this.setTextureName(TestCore.MODID + ":crafting_filter");
		this.setUnlocalizedName("craftingFilter");
		this.setCreativeTab(TestCore.testCreativeTab);
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof ConstructFilterUserTileEntity){
			if(((ConstructFilterUserTileEntity)world.getTileEntity(x, y, z)).setFilter(side, new CraftingFilterInventory(itemStack))){
				--itemStack.stackSize;
				world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			}
		}
		return !world.isRemote;
	}

}
