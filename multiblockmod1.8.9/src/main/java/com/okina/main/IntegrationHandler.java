package com.okina.main;

import net.minecraftforge.oredict.OreDictionary;

public class IntegrationHandler {

	public static void registerOre() {
		//		String[] names = OreDictionary.getOreNames();
		//		for (String name : names){
		//			if(name.length() >= 5){
		//				String name1 = name.substring(0, 3);
		//				if(name1.equals("ore")){
		//					String name2 = name.substring(3);
		//					if(OreDictionary.doesOreNameExist("ingot" + name2)){
		//						oreIds.add(OreDictionary.getOreID("ore" + name2));
		//						ingotIds.add(OreDictionary.getOreID("ingot" + name2));
		//						doubleDustIds.add(OreDictionary.getOreID("dustDouble" + name2));
		//						Item doubleDust = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName("dustDouble" + name2).setTextureName(MODID + ":double_" + name2 + "_dust");
		//						dustDouble.add(doubleDust);
		//						GameRegistry.registerItem(doubleDust, "dustDouble" + name2);
		//
		//						if(OreDictionary.doesOreNameExist("dust" + name2)){
		//							dustIds.add(OreDictionary.getOreID("dust" + name2));
		//						}else{
		//							dustIds.add(-1);
		//						}
		//					}
		//				}
		//			}
		//		}
		//
		//		for (int i = 0; i < oreNames.length; i++){
		//			blockIds[i] = -1;
		//			if(OreDictionary.doesOreNameExist("block" + oreNames[i])){
		//				blockIds[i] = OreDictionary.getOreID("block" + oreNames[i]);
		//			}
		//		}

	}

	private static int findOre(String name) {
		if(OreDictionary.doesOreNameExist(name)){
			return OreDictionary.getOreID(name);
		}
		return -1;
	}

	public static void registerRecipe() {
		//crusher
		//		if(Loader.isModLoaded("ThermalExpansion")){
		//			RecipePulverizer[] pulRecipes = PulverizerManager.getRecipeList();
		//			if(pulRecipes != null){
		//				for (RecipePulverizer pulRecipe : pulRecipes){
		//					CrusherRecipeRegister.instance.registerRecipe(pulRecipe.getInput(), pulRecipe.getPrimaryOutput());
		//				}
		//			}
		//		}
	}

}
