package com.okina.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class UtilMethods {

	/**pTrue ??? */
	public static MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean useLiquids) {
		float f = player.rotationPitch;
		float f1 = player.rotationYaw;
		double d0 = player.posX;
		double d1 = player.posY + (double) player.getEyeHeight();
		double d2 = player.posZ;
		Vec3 vec3 = new Vec3(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = 5.0D;
		if(player instanceof EntityPlayerMP){
			d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
		return world.rayTraceBlocks(vec3, vec31, useLiquids, !useLiquids, false);
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

	public static BlockPos multVeci(Vec3i vec, int value) {
		return new BlockPos(vec.getX() * value, vec.getY() * value, vec.getZ() * value);
	}

	public static boolean[] convertConnectionInfo(int connection) {
		boolean[] b = new boolean[6];
		for (int i = 0; i < 6; i++){
			b[i] = ((connection >> i) & 1) == 1;
		}
		return b;
	}

	public static int convertConnectionInfo(boolean[] connection) {
		int n = 0;
		for (int i = 0; i < 6; i++){
			if(connection[i]) n |= 1 << i;
		}
		return n;
	}

}







