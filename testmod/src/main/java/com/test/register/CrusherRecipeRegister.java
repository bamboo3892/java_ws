package com.test.register;

import java.util.ArrayList;

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

	public class CrusherRecipe {

		/**null : use ore dictionary*/
		public final ItemStack material;
		/**-1 : not using ore dictionary*/
		public final int materialId;
		public final ItemStack product;

		public CrusherRecipe(ItemStack material, ItemStack product) {
			this.material = material;
			this.materialId = -1;
			this.product = product;
		}

		public CrusherRecipe(int materialId, ItemStack product) {
			material = null;
			this.materialId = materialId;
			this.product = product;
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
