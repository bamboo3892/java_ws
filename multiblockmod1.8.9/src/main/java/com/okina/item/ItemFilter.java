package com.okina.item;

import static com.okina.main.TestCore.*;

import com.okina.inventory.FilterInventory;
import com.okina.inventory.IFilterUser;
import com.okina.main.TestCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemFilter extends Item {

	public ItemFilter() {
		//TODO
		//		this.setTextureName(TestCore.MODID + ":filter");
		setUnlocalizedName(AUTHER + ".filter");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(pos) instanceof IFilterUser){
			IFilterUser tile = (IFilterUser) world.getTileEntity(pos);
			if(tile.setFilter(side, new FilterInventory(tile, side))){
				--stack.stackSize;
			}
		}
		return !world.isRemote;
	}

}
