package com.test.multiblock.construct.tileentity;

public interface ILinkConnectionUser {

	boolean canStartAt(int side);
	
	boolean tryConnect(ConstructBaseTileEntity tile, int clickedSide, int linkUserSide);

}
