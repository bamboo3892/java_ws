package com.test.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class TestCreativeTab extends CreativeTabs{

	public TestCreativeTab(String name){
		super(name);
	}

	@Override
	public Item getTabIconItem() {
		return Items.diamond_hoe;
	}

}
