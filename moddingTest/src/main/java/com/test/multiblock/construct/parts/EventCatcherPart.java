package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructEventCatcherTileEntity;
import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public class EventCatcherPart extends ConstructPartBase {

	public EventCatcherPart() {

	}

	@Override
	public void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public String getNameForNBT() {
		return ConstructEventCatcherTileEntity.nameForNBT;
	}

}
