package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructEnergyProviderTileEntity;
import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public class EnergyProviderPart extends ConstructPartBase {

	public EnergyProviderPart() {
	}

	@Override
	public void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public String getNameForNBT() {
		return ConstructEnergyProviderTileEntity.nameForNBT;
	}

}
