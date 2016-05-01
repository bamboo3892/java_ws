package com.test.server.gui;

import com.test.tileentity.IGuiSliderUser;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class SliderInputContainer extends Container{

	public SliderInputContainer(IGuiSliderUser tile) {

	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

}
