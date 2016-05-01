package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructCrusherTileEntity;
import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public class CrusherPart extends ConstructPartBase {

	public CrusherPart() {
	}

	@Override
	public void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public String getNameForNBT() {
		return ConstructCrusherTileEntity.nameForNBT;
	}

}
