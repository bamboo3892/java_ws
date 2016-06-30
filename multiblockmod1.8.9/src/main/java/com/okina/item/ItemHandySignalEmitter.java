package com.okina.item;

import static com.okina.main.TestCore.*;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ISignalReceiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemHandySignalEmitter extends Item {

	public ItemHandySignalEmitter() {
		//TODO
		//		this.setTextureName(TestCore.MODID + ":handy_signal_emitter");
		setUnlocalizedName(AUTHER + ".signalEmitter");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote){
			if(world.getTileEntity(pos) instanceof ISignalReceiver){
				((ISignalReceiver) world.getTileEntity(pos)).onSignalReceived();
			}
		}
		return !world.isRemote;
	}

}
