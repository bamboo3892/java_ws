package com.okina.register;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CrusherRecipeRegister {

	public static final String RecipeId = "TestModCrusher";

	public static CrusherRecipeRegister instance = new CrusherRecipeRegister();
	private static ArrayList<CrusherRecipe> recipes = new ArrayList<CrusherRecipe>();

	public void registerRecipe(ItemStack input, ItemStack output) {
		instance.recipes.add(new CrusherRecipe(input, output));
	}

	public void registerRecipe(int inputId, ItemStack output) {
		instance.recipes.add(new CrusherRecipe(inputId, output));
	}

	public CrusherRecipe findRecipe(ItemStack stack) {
		if(stack == null) return null;
		for (CrusherRecipe recipe : recipes){
			if(recipe.isMatch(stack)){
				return recipe;
			}
		}
		return null;
	}

	public CrusherRecipe findRecipeFromProduct(ItemStack product) {
		for (CrusherRecipe recipe : recipes){
			if(recipe.getProduct().isItemEqual(product)){
				return recipe;
			}
		}
		return null;
	}

	public CrusherRecipe getRecipeFromKey(int key) {
		for (CrusherRecipe recipe : recipes){
			if(recipe.key == key){
				return recipe;
			}
		}
		return null;
	}

	public ArrayList<CrusherRecipe> getAllRecipes() {
		return recipes;
	}

	public static class CrusherRecipe {

		public static int KeyIndex = 0;

		/**null : use ore dictionary*/
		private final ItemStack material;
		/**-1 : not using ore dictionary*/
		private final int materialId;
		public final int key;
		private final ItemStack product;

		public CrusherRecipe(ItemStack material, ItemStack product) {
			this.material = material;
			materialId = -1;
			this.product = product;
			key = KeyIndex++;
		}

		public CrusherRecipe(int materialId, ItemStack product) {
			material = null;
			this.materialId = materialId;
			this.product = product;
			key = KeyIndex++;
		}

		public ItemStack getMaterial() {
			if(material == null){
				List<ItemStack> list = OreDictionary.getOres(OreDictionary.getOreName(materialId));
				if(!list.isEmpty()) return list.get(0).copy();
			}
			return material.copy();
		}

		public ItemStack getProduct() {
			return product.copy();
		}

		public boolean isMatch(ItemStack stack) {
			if(material != null){
				return OreDictionary.itemMatches(material, stack, false);
			}else{
				int[] ids = OreDictionary.getOreIDs(stack);
				if(ids != null){
					for (int id : ids){
						return id == materialId;
					}
				}
				return false;
			}
		}

	}


}
