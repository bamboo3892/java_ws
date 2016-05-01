package com.test.item;

import com.test.main.TestCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SightStealer extends Item {

	public SightStealer() {
		setTextureName("apple");
		setUnlocalizedName("sightStealer");
		setCreativeTab(TestCore.testCreativeTab);
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(player.isSneaking()){
			world.markBlockForUpdate(x, y, z);
		}else{
			world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
		}
		return true;
	}

}
