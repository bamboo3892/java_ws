package com.test.item;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ISignalReceiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemHandySignalEmitter extends Item {

	public ItemHandySignalEmitter() {
		this.setTextureName(TestCore.MODID + ":connector");
		setUnlocalizedName("signalEmitter");
		setCreativeTab(TestCore.testCreativeTab);
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote){
			if(world.getTileEntity(x, y, z) instanceof ISignalReceiver){
				((ISignalReceiver) world.getTileEntity(x, y, z)).onSignalReceived();
			}
		}
		return !world.isRemote;
	}

}
