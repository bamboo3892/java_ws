package com.okina.utils;

import java.util.ArrayList;

import net.minecraft.util.BlockPos;

public class RectangularSolid {

	public int minX = Integer.MAX_VALUE;
	public int minY = Integer.MAX_VALUE;
	public int minZ = Integer.MAX_VALUE;
	public int maxX = Integer.MIN_VALUE;
	public int maxY = Integer.MIN_VALUE;
	public int maxZ = Integer.MIN_VALUE;

	public RectangularSolid() {}

	public RectangularSolid(int xSize, int ySize, int zSize) {
		if(xSize < 1 || ySize < 1 || zSize < 1) return;
		minX = 0;
		minY = 0;
		minZ = 0;
		maxX = xSize - 1;
		maxY = ySize - 1;
		maxZ = zSize - 1;
	}

	public RectangularSolid(BlockPos start, int xSize, int ySize, int zSize) {
		minX = start.getX();
		minY = start.getY();
		minZ = start.getZ();
		maxX = minX + xSize;
		maxY = minY + ySize;
		maxZ = minZ + zSize;
	}

	public RectangularSolid(BlockPos minPoint, BlockPos maxPoint) {
		if(minPoint == null || maxPoint == null) throw new IllegalArgumentException();
		if(minPoint.getX() == maxPoint.getX() || minPoint.getY() == maxPoint.getY() || minPoint.getZ() == maxPoint.getZ()) throw new IllegalArgumentException();
		minX = minPoint.getX();
		minY = minPoint.getY();
		minZ = minPoint.getZ();
		maxX = maxPoint.getX();
		maxY = maxPoint.getY();
		maxZ = maxPoint.getZ();
	}

	public RectangularSolid(ArrayList<BlockPos> list) {
		if(list.size() != 8){
			System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
			return;
		}
		for (BlockPos pos : list){
			minX = minX > pos.getX() ? pos.getX() : minX;
			minY = minY > pos.getY() ? pos.getY() : minY;
			minZ = minZ > pos.getZ() ? pos.getZ() : minZ;
			maxX = maxX < pos.getX() ? pos.getX() : maxX;
			maxY = maxY < pos.getY() ? pos.getY() : maxY;
			maxZ = maxZ < pos.getZ() ? pos.getZ() : maxZ;
		}
	}

	public BlockPos getMinPoint() {
		return new BlockPos(minX, minY, minZ);
	}

	public BlockPos getMaxPoint() {
		return new BlockPos(maxX, maxY, maxZ);
	}

	public BlockPos getCenterPoint() {
		return new BlockPos((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
	}

	public int getXSize() {
		return maxX - minX + 1;
	}

	public int getYSize() {
		return maxY - minY + 1;
	}

	public int getZSize() {
		return maxZ - minZ + 1;
	}

	public boolean isInclude(BlockPos p) {
		if(p != null){
			return p.getX() >= minX && p.getY() >= minY && p.getZ() >= minZ && p.getX() <= maxX && p.getY() <= maxY && p.getZ() <= maxZ;
		}
		return false;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param relative
	 * @return index
	 */
	public int toIndex(int x, int y, int z, boolean relative) {
		if(!relative){
			x -= minX;
			y -= minY;
			z -= minZ;
		}
		int index = x * getYSize() * getZSize() + y * getZSize() + z;
		return index;
	}

	public int toIndex(BlockPos p, boolean relative) {
		return toIndex(p.getX(), p.getY(), p.getZ(), relative);
	}

	public int getIndexSize() {
		return getXSize() * getYSize() * getZSize();
	}

	public BlockPos toCoord(int index) {
		int z = index % getZSize();
		index -= z;
		index /= getZSize();
		int y = index % getYSize();
		index -= y;
		index /= getYSize();
		int x = index;
		return getMinPoint().add(new BlockPos(x, y, z));
	}

	@Override
	public String toString() {
		String x = " minX = " + minX + "\t maxX = " + maxX;
		String y = " minY = " + minY + "\tmaxY = " + maxY;
		String z = " minZ = " + minZ + "\tmaxZ = " + maxZ;
		return x + "\n" + y + "\n" + z;
	}

	public void set(ArrayList<BlockPos> list) {
		if(list.size() != 8){
			System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
			return;
		}
		for (BlockPos pos : list){
			minX = minX > pos.getX() ? pos.getX() : minX;
			minY = minY > pos.getY() ? pos.getY() : minY;
			minZ = minZ > pos.getZ() ? pos.getZ() : minZ;
			maxX = maxX < pos.getX() ? pos.getX() : maxX;
			maxY = maxY < pos.getY() ? pos.getY() : maxY;
			maxZ = maxZ < pos.getZ() ? pos.getZ() : maxZ;
		}
	}

}
