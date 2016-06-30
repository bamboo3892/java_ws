package com.okina.tileentity;

import com.okina.main.GuiHandler.IGuiTile;

public interface IGuiSliderUser extends ISimpleTilePacketUser, IGuiTile {

	int getValue();
	int getMinValue();
	int getMaxValue();
	String getContainerName();

}
