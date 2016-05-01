package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructVirtualGrowerTileEntity;
import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public class VirtualGlowerPart extends ConstructPartBase {

	public VirtualGlowerPart() {
		
	}

	@Override
	public void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public String getNameForNBT() {
		return ConstructVirtualGrowerTileEntity.nameForNBT;
	}

}
