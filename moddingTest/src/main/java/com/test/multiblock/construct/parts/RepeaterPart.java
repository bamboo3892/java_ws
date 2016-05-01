package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructRepeaterTileEntity;
import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public class RepeaterPart extends ConstructPartBase {

	public RepeaterPart() {

	}

	@Override
	public void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public String getNameForNBT() {
		return ConstructRepeaterTileEntity.nameForNBT;
	}

}
