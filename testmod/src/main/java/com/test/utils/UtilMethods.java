package com.test.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class UtilMethods {

	/**pTrue ??? */
	public static MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean pTrue) {
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) f + (double) (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;
		Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		if(player instanceof EntityPlayerMP){
			d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
		return world.func_147447_a(vec3, vec31, pTrue, !pTrue, false);
	}

	public static int[] getRandomArray(int min, int max) {
		int[] re = new int[max - min + 1];
		for (int i = 0; i < re.length; i++){
			re[i] = i + min;
		}
		re = getRandomArray(re);
		return re;
	}

	public static int[] getRandomArray(int[] array) {
		if(array == null || array.length == 0) return null;
		int[] newArray = array.clone();
		boolean[] flags = new boolean[array.length];
		int index1;
		int index2;
		for (int i = 0; i < array.length; i++){
			index1 = (int) (Math.random() * (array.length - i));
			index2 = 0;
			for (int j = 0; j <= index1; j++){
				while (flags[index2])
					index2++;
				if(j < index1) index2++;
			}
			newArray[i] = array[index2];
			flags[index2] = true;
		}
		return newArray;
	}

}