package com.okina.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ConnectionEntry<T> {

	private T tile;
	public int x;
	public int y;
	public int z;
	/** -1 : no side information */
	public int side = -1;

	public ConnectionEntry() {}

	public ConnectionEntry(T tile, int x, int y, int z) {
		this.tile = tile;
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = -1;
	}

	public ConnectionEntry(T tile, int x, int y, int z, int side) {
		this.tile = tile;
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = side;
	}

	/** tile must be TileEntity */
	public ConnectionEntry(T tile, int side) throws IllegalArgumentException {
		if(tile instanceof TileEntity){
			TileEntity tile2 = (TileEntity) tile;
			this.tile = tile;
			this.x = tile2.getPos().getX();
			this.y = tile2.getPos().getY();
			this.z = tile2.getPos().getZ();
			this.side = side;
		}else{
			throw new IllegalArgumentException("parameter should be TileEntity");
		}
	}

	/** tile must be TileEntity */
	public ConnectionEntry(T tile, EnumFacing side) throws IllegalArgumentException {
		if(tile instanceof TileEntity){
			TileEntity tile2 = (TileEntity) tile;
			this.tile = tile;
			this.x = tile2.getPos().getX();
			this.y = tile2.getPos().getY();
			this.z = tile2.getPos().getZ();
			this.side = side.getIndex();
		}else{
			throw new IllegalArgumentException("parameter should be TileEntity");
		}
	}

	/** tile must be TileEntity */
	public ConnectionEntry(T tile) throws IllegalArgumentException {
		if(tile instanceof TileEntity){
			TileEntity tile2 = (TileEntity) tile;
			this.tile = tile;
			this.x = tile2.getPos().getX();
			this.y = tile2.getPos().getY();
			this.z = tile2.getPos().getZ();
			this.side = -1;
		}else{
			throw new IllegalArgumentException("parameter should be TileEntity");
		}
	}

	public BlockPos getPosition() {
		return new BlockPos(x, y, z);
	}

	public EnumFacing getFront() {
		return side == -1 ? null : EnumFacing.getFront(side);
	}

	public boolean hasTile() {
		return this.getTile() != null;
	}

	public T getTile() {
		if(tile instanceof TileEntity){
			return tile == null || ((TileEntity) tile).isInvalid() ? null : tile;
		}else{
			return tile;
		}
	}

	public void setTile(T tile) {
		this.tile = tile;
	}

	@Override
	public boolean equals(Object o) {
		return equals(o, true);
	}

	public boolean equals(Object o, boolean sideMode) {
		if(o instanceof ConnectionEntry){
			ConnectionEntry entry = (ConnectionEntry) o;
			if(sideMode){
				return this.tile == entry.tile && this.side == entry.side;
			}else{
				return this.tile == entry.tile;
			}
		}
		return false;
	}

	public String getText() {
		if(tile != null){
			if(side < 0 || side > 6){
				return tile.getClass().getSimpleName();
			}else{
				return tile.getClass().getSimpleName() + " " + EnumFacing.getFront(side).toString();
			}
		}
		return "No Tile";
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		tag.setInteger("side", side);
	}

	public static ConnectionEntry createFromNBT(NBTTagCompound tag) {
		if(tag == null || !tag.hasKey("x") || !tag.hasKey("y") || !tag.hasKey("z") || !tag.hasKey("side")) return null;
		int x = tag.getInteger("x");
		int y = tag.getInteger("y");
		int z = tag.getInteger("z");
		int side = tag.getInteger("side");
		return new ConnectionEntry(null, x, y, z, side);
	}

}
