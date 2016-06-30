package com.okina.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IFilterUser extends ISidedInventory {

	boolean setFilter(EnumFacing side, AbstractFilter filter);

	AbstractFilter getFilter(EnumFacing side);

	ItemStack removeFilter(EnumFacing side);

	BlockPos getPosition();

	World getWorldObject();

	void updateFilter();

}
