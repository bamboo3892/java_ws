package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructFurnaceTileEntity;

public class FurnacePart extends InventoryPartBase {

	public FurnacePart() {

	}

	@Override
	public String getNameForNBT() {
		return ConstructFurnaceTileEntity.nameForNBT;
	}

}
