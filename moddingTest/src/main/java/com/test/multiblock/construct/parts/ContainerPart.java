package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;
import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public class ContainerPart extends ConstructPartBase {

	public ContainerPart() {
	}

	@Override
	public void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public String getNameForNBT() {
		return ConstructContainerTileEntity.nameForNBT;
	}

}
