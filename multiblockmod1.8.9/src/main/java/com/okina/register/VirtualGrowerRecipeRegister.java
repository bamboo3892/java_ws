package com.okina.register;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class VirtualGrowerRecipeRegister {

	public static VirtualGrowerRecipeRegister instance = new VirtualGrowerRecipeRegister();
	private static ArrayList<VirtualGrowerRecipe> recipes = new ArrayList<VirtualGrowerRecipe>();

	public void registerRecipe(ItemStack material, int energy, int time) {
		recipes.add(new VirtualGrowerRecipe(material, energy, time));
	}

	public void registerRecipe(int materialId, int energy, int time) {
		recipes.add(new VirtualGrowerRecipe(materialId, energy, time));
	}

	public VirtualGrowerRecipe findRecipe(ItemStack stack) {
		if(stack == null) return null;
		for (VirtualGrowerRecipe recipe : recipes){
			if(recipe.isMatch(stack)){
				return recipe;
			}
		}
		return null;
	}

	public VirtualGrowerRecipe getRecipeFromKey(int key) {
		for (VirtualGrowerRecipe recipe : recipes){
			if(recipe.key == key){
				return recipe;
			}
		}
		return null;
	}

	public ArrayList<VirtualGrowerRecipe> getAllRecipes() {
		return recipes;
	}

	public static class VirtualGrowerRecipe {

		public static int KeyIndex = 0;
		/**null : use ore dictionary*/
		public final ItemStack material;
		/**-1 : not using ore dictionary*/
		public final int materialId;
		public final int energy;
		private final int time;
		public final int key;

		public VirtualGrowerRecipe(ItemStack material, int energy, int time) {
			this.material = material;
			materialId = -1;
			this.energy = energy;
			this.time = time;
			key = KeyIndex++;
		}

		public VirtualGrowerRecipe(int materialId, int energy, int time) {
			material = null;
			this.materialId = materialId;
			this.energy = energy;
			this.time = time;
			key = KeyIndex++;
		}

		public ItemStack getMaterial() {
			if(material == null){
				List<ItemStack> list = OreDictionary.getOres(OreDictionary.getOreName(materialId));
				if(!list.isEmpty()) return list.get(0).copy();
			}
			return material.copy();
		}

		public int getTime(int grade) {
			grade = grade == 0 ? 1 : grade;
			return time / grade;
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
