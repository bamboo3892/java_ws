package com.test.multiblock.construct.tileentity;

import net.minecraft.inventory.ISidedInventory;

public interface IConstructInventory extends ISidedInventory {
	
	public static final int[] maxTransfer = { 1, 4, 16, 32, 64 };

}
