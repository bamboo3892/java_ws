package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructInterfaceTileEntity;
import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public class InterfacePart extends ConstructPartBase {

	public InterfacePart() {

	}

	@Override
	public void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public String getNameForNBT() {
		return ConstructInterfaceTileEntity.nameForNBT;
	}

}
