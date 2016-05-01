package com.test.multiblock.construct.parts;

import com.test.multiblock.construct.tileentity.ConstructStorageTileEntity;

public class StoragePart extends InventoryPartBase {

	public StoragePart() {

	}

	@Override
	public String getNameForNBT() {
		return ConstructStorageTileEntity.nameForNBT;
	}

}
