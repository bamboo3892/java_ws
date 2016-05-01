package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructClockPulserTileEntity;
import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public class ClockPulserPart extends ConstructPartBase {

	public ClockPulserPart() {
	}

	@Override
	public void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public String getNameForNBT() {
		return ConstructClockPulserTileEntity.nameForNBT;
	}

}
