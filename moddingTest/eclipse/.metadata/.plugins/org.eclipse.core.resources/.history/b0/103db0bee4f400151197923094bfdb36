package com.test.main;

import static com.test.main.TestCore.*;

import java.util.List;

import com.test.register.CrusherRecipeRegister;
import com.test.register.VirtualGrowerRecipeRegister;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class IntegrationHandler {

	public static final String THERMAL_EXPANSION = "ThermalFoundation";

	public static void checkIntegration() {
		integrationTE = isModContained(THERMAL_EXPANSION);
	}

	private static boolean isModContained(String modid) {
		List<ModContainer> mods = Loader.instance().getModList();
		for (ModContainer mod : mods){
			System.out.println(mod.getModId());
			if(mod.getModId().equals(modid)) return true;
		}
		return false;
	}

	public static void registerOre() {
		String[] names = OreDictionary.getOreNames();
		for (String name : names){
			if(name.length() >= 5){
				String name1 = name.substring(0, 3);
				if(name1.equals("ore")){
					String name2 = name.substring(3);
					if(OreDictionary.doesOreNameExist("ingot"+name2)){
						oreIds.add(OreDictionary.getOreID("ore" + name2));
						ingotIds.add(OreDictionary.getOreID("ingot" + name2));
						doubleDustIds.add(OreDictionary.getOreID("dustDouble" + name2));
						Item doubleDust = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName("dustDouble" + name2).setTextureName(MODID + ":double_" + name2 + "_dust");
						dustDouble.add(doubleDust);
						GameRegistry.registerItem(doubleDust, "dustDouble" + name2);

						if(OreDictionary.doesOreNameExist("dust" + name2)){
							dustIds.add(OreDictionary.getOreID("dust" + name2));
						}else{
							dustIds.add(-1);
						}
					}
				}
			}
		}

		for (int i = 0; i < oreNames.length; i++){
			blockIds[i] = -1;
			if(OreDictionary.doesOreNameExist("block" + oreNames[i])){
				blockIds[i] = OreDictionary.getOreID("block" + oreNames[i]);
			}
		}

	}

	private static int findOre(String name) {
		if(OreDictionary.doesOreNameExist(name)){
			return OreDictionary.getOreID(name);
		}
		return -1;
	}

	public static void registerRecipe() {
		//crafting
		for (int i = 0; i < oreIds.size(); i++){
			if(dustDouble.get(i) != null && dustIds.get(i) != -1){
				String name = OreDictionary.getOreName(dustIds.get(i));
				List<ItemStack> dusts = OreDictionary.getOres(name);
				if(dusts != null && dusts.size() > 0){
					ItemStack dust = dusts.get(0).copy();
					dust.stackSize = 2;
					if(dust != null){
						GameRegistry.addShapelessRecipe(dust, new ItemStack(dustDouble.get(i), 1));
					}
				}
			}
		}

		//smelting
		for (int i = 0; i < oreIds.size(); i++){
			if(dustDouble.get(i) != null && ingotIds.get(i) != -1){
				String name = OreDictionary.getOreName(ingotIds.get(i));
				List<ItemStack> ingots = OreDictionary.getOres(name);
				if(ingots != null && ingots.size() > 0){
					ItemStack ingot = ingots.get(0).copy();
					ingot.stackSize = 2;
					if(ingot != null){
						GameRegistry.addSmelting(dustDouble.get(i), ingot, 0.3F);
					}
				}
			}
		}

		//crusher
		for (int i = 0; i <  oreIds.size(); i++){
			if(oreIds.get(i) != -1 && dustDouble.get(i) != null){
				CrusherRecipeRegister.instance.registerRecipe(oreIds.get(i), new ItemStack(dustDouble.get(i), 1));
			}
		}

		//virtual grower
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.nether_star, 1), 40000, 10000);
	}

}
