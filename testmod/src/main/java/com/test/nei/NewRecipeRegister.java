package com.test.nei;

import java.util.HashMap;

import com.test.main.TestCore;

import net.minecraft.item.ItemStack;

public class NewRecipeRegister {

	public static HashMap<ItemStack, ItemStack> newRecipeList = new HashMap<ItemStack, ItemStack>();

	/*架空のレシピを登録*/
	public void setRecipeList() {
		newRecipeList.put(new ItemStack(TestCore.blockFrame, 1, 0), new ItemStack(TestCore.blockFrame, 1, 0));
	}

}
