package com.test.inventory;

import com.test.utils.Position;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IFilterUser extends ISidedInventory {

	boolean setFilter(int side, AbstractFilter filter);

	AbstractFilter getFilter(int side);

	ItemStack removeFilter(int side);

	Position getPosition();

	World getWorldObject();

	void updateFilter();

}
