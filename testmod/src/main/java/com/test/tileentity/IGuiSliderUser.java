package com.test.tileentity;

import com.test.main.GuiHandler.IGuiTile;

public interface IGuiSliderUser extends ISimpleTilePacketUser, IGuiTile{

	int getValue();
	int getMinValue();
	int getMaxValue();
	String getContainerName();

}
