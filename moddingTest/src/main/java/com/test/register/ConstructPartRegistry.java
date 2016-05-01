package com.test.register;

import java.util.HashMap;

import com.test.multiblock.construct.parts.ConstructPartBase;
import com.test.utils.RectangularSolid;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.nbt.NBTTagCompound;

public class ConstructPartRegistry {

	public static HashMap<String, Class<? extends ConstructPartBase>> partsMap = new HashMap<String, Class<? extends ConstructPartBase>>();

	public static boolean registerConstructPart(ConstructPartBase part){
		String name = part.getNameForNBT();
		if(partsMap.containsKey(name)){
			FMLLog.severe("That name part is already registered : " + name, new Object[0]);
			return false;
		}else{
			partsMap.put(name, part.getClass());
			return true;
		}
	}

	/**
	 *
	 * @param tag
	 * @param solid (use to transfer id to coordinate)
	 * @return
	 */
	public static ConstructPartBase getNewPartFromNBT(NBTTagCompound tag, RectangularSolid solid){
		String name = tag.getString("name");
		if(partsMap.containsKey(name)){
			try {
				ConstructPartBase part = partsMap.get(name).newInstance();
				part.readFromNBTTagCompound(tag, solid);
				return part;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}else{
			FMLLog.severe("That name part is not registered : " + name, new Object[0]);
		}
		return null;
	}

}
