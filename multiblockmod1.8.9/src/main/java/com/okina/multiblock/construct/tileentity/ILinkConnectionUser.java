package com.okina.multiblock.construct.tileentity;

import net.minecraft.util.EnumFacing;

public interface ILinkConnectionUser {

	boolean canStartAt(EnumFacing side);

	boolean tryConnect(ConstructBaseTileEntity tile, EnumFacing side, EnumFacing linkUserSide);

}
