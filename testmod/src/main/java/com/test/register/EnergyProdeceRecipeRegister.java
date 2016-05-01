package com.test.register;

import java.util.ArrayList;

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

	public class EnergyProduceRecipe {

		/**null : use ore dictionary*/
		public final ItemStack material;
		/**-1 : not using ore dictionary*/
		public final int materialId;
		public final int produceEnergy;
		private final int time;

		public EnergyProduceRecipe(ItemStack material, int prodeceEnergy, int time) {
			this.material = material;
			this.materialId = -1;
			this.produceEnergy = prodeceEnergy;
			this.time = time;
		}

		public EnergyProduceRecipe(int materialId, int prodeceEnergy, int time) {
			material = null;
			this.materialId = materialId;
			this.produceEnergy = prodeceEnergy;
			this.time = time;
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
