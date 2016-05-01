package com.test.multiblock.construct.parts;

import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public abstract class ConstructPartBase {

	public Position position;
	public int[] side = new int[6];
	/**
	 * 0 : input
	 * 1 : output
	 * 2 : disabled
	 */
	public int flagIO[] = new int[6];

	public ConstructPartBase() {
		for(int i=0;i<6;i++){
			side[i] = -1;
			flagIO[i] = 2;
		}
	}

	public abstract void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid);

	public abstract void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid);

	public abstract String getNameForNBT();

}
