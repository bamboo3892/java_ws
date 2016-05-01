package com.test.tileentity;

import com.test.utils.Position;

public interface IGuiSliderUser extends ISimpleTilePacketUser{

	int getValue();
	int getMinValue();
	int getMaxValue();
	Position getPosition();
	String getContainerName();

}
