package com.okina.register;

import java.util.HashMap;

import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.multiblock.construct.block.ConstructFunctionalBase;
import com.okina.multiblock.construct.parts.ConstructPartBase;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;

public class ConstructPartRegistry {

	public static HashMap<String, Class<? extends ConstructPartBase>> partsMap = new HashMap<String, Class<? extends ConstructPartBase>>();
	public static HashMap<String, ConstructFunctionalBase[]> blockMap = new HashMap<String, ConstructFunctionalBase[]>();

	public static boolean registerConstructPart(Block[] block, ConstructPartBase part) {
		String name = part.getNameForNBT();
		if(partsMap.containsKey(name) || block == null || block.length != 5){
			FMLLog.severe("That name part is already registered : " + name, new Object[0]);
			return false;
		}else{
			partsMap.put(name, part.getClass());
			blockMap.put(name, (ConstructFunctionalBase[]) block);
			return true;
		}
	}

	/**
	 * @param tag
	 * @param solid (use to encode id to coordinate)
	 * @return
	 */
	public static ConstructPartBase getNewPartFromNBT(MultiBlockCoreTileEntity tile, NBTTagCompound tag, RectangularSolid solid) {
		String name = tag.getString("name");
		if(partsMap.containsKey(name)){
			try{
				ConstructPartBase part = partsMap.get(name).newInstance();
				part.setCoreTile(tile);
				part.readFromNBT(tag, solid);
				return part;
			}catch (InstantiationException e){
				e.printStackTrace();
			}catch (IllegalAccessException e){
				e.printStackTrace();
			}
		}else{
			FMLLog.severe("That name part is not registered : " + name, new Object[0]);
		}
		return null;
	}

	public static ConstructFunctionalBase getBlockFromNBTName(String name, int grade) {
		if(blockMap.containsKey(name)){
			try{
				return blockMap.get(name)[grade];
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}

}
