package com.okina.server.gui;

import com.okina.multiblock.construct.tileentity.ConstructClockPulserTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ClockPulserContainer extends Container {

	public ClockPulserContainer(ConstructClockPulserTileEntity tile) {

	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

}
