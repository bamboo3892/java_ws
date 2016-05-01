package com.test.block;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public interface ITestModBlock {

	public EnumRarity getRarity(ItemStack itemStack);

}
