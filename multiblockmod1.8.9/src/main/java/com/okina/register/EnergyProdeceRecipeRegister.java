package com.okina.register;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class EnergyProdeceRecipeRegister {

	public static EnergyProdeceRecipeRegister instance = new EnergyProdeceRecipeRegister();
	private static ArrayList<EnergyProduceRecipe> recipes = new ArrayList<EnergyProduceRecipe>();

	public void registerRecipe(ItemStack input, int prodeceEnergy, int time) {
		instance.recipes.add(new EnergyProduceRecipe(input, prodeceEnergy, time));
	}

	public void registerRecipe(int inputId, int prodeceEnergy, int time) {
		instance.recipes.add(new EnergyProduceRecipe(inputId, prodeceEnergy, time));
	}

	public EnergyProduceRecipe findRecipe(ItemStack stack) {
		if(stack == null) return null;
		for (EnergyProduceRecipe recipe : recipes){
			if(recipe.isMatch(stack)){
				return recipe;
			}
		}
		return null;
	}

	public EnergyProduceRecipe getRecipeFromKey(int key) {
		for (EnergyProduceRecipe recipe : recipes){
			if(recipe.key == key){
				return recipe;
			}
		}
		return null;
	}

	public ArrayList<EnergyProduceRecipe> getAllRecipes() {
		return recipes;
	}

	public static class EnergyProduceRecipe {

		public static int KeyIndex = 0;
		/**null : use ore dictionary*/
		public final ItemStack material;
		/**-1 : not using ore dictionary*/
		public final int materialId;
		public final int produceEnergy;
		private final int time;
		public final int key;

		public EnergyProduceRecipe(ItemStack material, int prodeceEnergy, int time) {
			this.material = material;
			materialId = -1;
			produceEnergy = prodeceEnergy;
			this.time = time;
			key = KeyIndex++;
		}

		public EnergyProduceRecipe(int materialId, int prodeceEnergy, int time) {
			material = null;
			this.materialId = materialId;
			produceEnergy = prodeceEnergy;
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

		@Override
		public String toString() {
			if(material != null){
				return material.getDisplayName() + " : " + produceEnergy + "RF";
			}else{
				return OreDictionary.getOreName(materialId) + " : " + produceEnergy + "RF";
			}
		}

	}

}
