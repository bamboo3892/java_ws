package com.test.item.itemBlock;

import com.test.block.ITestModBlock;
import com.test.main.TestCore;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockTestMod extends ItemBlock {

	public ItemBlockTestMod(Block block) {
		super(block);
		this.setCreativeTab(TestCore.testCreativeTab);
		this.setMaxStackSize(64);
	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return ((ITestModBlock)field_150939_a).getRarity(itemStack);
	}

}
