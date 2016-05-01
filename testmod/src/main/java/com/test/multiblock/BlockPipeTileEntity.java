package com.test.multiblock;

import java.util.ArrayList;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.test.multiblock.construct.tileentity.IPipeConnectionUser;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.tileentity.ISimpleTilePacketUser;
import com.test.utils.ConnectionEntry;
import com.test.utils.Position;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPipeTileEntity extends TileEntity implements ISimpleTilePacketUser {

	private boolean needUpdate = true;
	public boolean[] connection = new boolean[6];

	public BlockPipeTileEntity() {

	}

	@Override
	public void updateEntity() {
		if(needUpdate){
			checkConnection();
			needUpdate = false;
		}
	}

	/**effective only on client*/
	public void checkConnection() {
		boolean b = false;
		boolean flag = false;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(tile instanceof ConstructBaseTileEntity){
				b = ((ConstructBaseTileEntity) tile).flagIO[dir.getOpposite().ordinal()] != 2;
			}else if(tile instanceof BlockPipeTileEntity && this.getBlockMetadata() == tile.getBlockMetadata()){
				b = true;
			}else{
				b = false;
			}
			if(connection[dir.ordinal()] != b) flag = true;
			connection[dir.ordinal()] = b;
		}
		if(flag) TestCore.proxy.markForTileUpdate(getPosition(), PacketType.RENDER);
	}

	/**objective tile must extend ConstructBaseTileEntity*/
	public void findConnection(ArrayList<BlockPipeTileEntity> checkedPipes, ArrayList<ConnectionEntry> tiles, Class tileClass, boolean sideMode) {
		checkedPipes.add(this);
		for (ForgeDirection dir : ForgeDirection.values()){
			if(dir.ordinal() >= 6) break;
			int newX = xCoord + dir.offsetX;
			int newY = yCoord + dir.offsetY;
			int newZ = zCoord + dir.offsetZ;
			TileEntity t = worldObj.getTileEntity(newX, newY, newZ);
			if(t == null) continue;
			if(t instanceof BlockPipeTileEntity && this.getBlockMetadata() == t.getBlockMetadata()){
				BlockPipeTileEntity pipe = (BlockPipeTileEntity) t;
				if(!checkedPipes.contains(pipe)){
					pipe.findConnection(checkedPipes, tiles, tileClass, sideMode);
				}
			}else if(tileClass.isAssignableFrom(t.getClass())){
				if(t instanceof ConstructBaseTileEntity){
					ConstructBaseTileEntity tile = (ConstructBaseTileEntity) t;
					if(tile.flagIO[dir.getOpposite().ordinal()] == 0){
						if(sideMode){
							ConnectionEntry entry = new ConnectionEntry(tile, dir.getOpposite().ordinal());
							if(!tiles.contains(entry)){
								tiles.add(entry);
							}
						}else{
							ConnectionEntry entry = new ConnectionEntry(tile);
							if(!tiles.contains(entry)){
								tiles.add(entry);
							}
						}
					}
				}
			}
		}
	}

	public void onTileRemoved() {
		this.invalidate();
		if(worldObj.isRemote){
			//do nothing
		}else{
			ArrayList<BlockPipeTileEntity> checkedPipes = new ArrayList<BlockPipeTileEntity>();
			ArrayList<ConnectionEntry> tiles = new ArrayList<ConnectionEntry>();
			Class tileClass = IPipeConnectionUser.class;
			checkedPipes.add(this);
			for (ForgeDirection dir : ForgeDirection.values()){
				if(dir.ordinal() >= 6) break;
				int newX = xCoord + dir.offsetX;
				int newY = yCoord + dir.offsetY;
				int newZ = zCoord + dir.offsetZ;
				TileEntity t = worldObj.getTileEntity(newX, newY, newZ);
				if(t == null || t.isInvalid()){
					continue;
				}else if(t instanceof BlockPipeTileEntity && this.getBlockMetadata() == t.getBlockMetadata()){
					BlockPipeTileEntity pipe = (BlockPipeTileEntity) t;
					if(!checkedPipes.contains(pipe)){
						pipe.findAllConnection(checkedPipes, tiles, tileClass);
					}
				}else if(tileClass.isAssignableFrom(t.getClass())){
					TileEntity tile = (TileEntity) (t);
					ConnectionEntry entry = new ConnectionEntry(tile);
					if(!tiles.contains(entry)){
						tiles.add(entry);
					}
				}
			}
			for (ConnectionEntry tile : tiles){
				((IPipeConnectionUser) tile.getTile()).needUpdateEntry();
			}
		}
	}

	private void findAllConnection(ArrayList<BlockPipeTileEntity> checkedPipes, ArrayList<ConnectionEntry> tiles, Class tileClass) {
		checkedPipes.add(this);
		for (ForgeDirection dir : ForgeDirection.values()){
			if(dir.ordinal() >= 6) break;
			int newX = xCoord + dir.offsetX;
			int newY = yCoord + dir.offsetY;
			int newZ = zCoord + dir.offsetZ;
			TileEntity t = worldObj.getTileEntity(newX, newY, newZ);
			if(t == null) continue;
			if(t instanceof BlockPipeTileEntity && this.getBlockMetadata() == t.getBlockMetadata()){
				BlockPipeTileEntity pipe = (BlockPipeTileEntity) t;
				if(!checkedPipes.contains(pipe)){
					pipe.findConnection(checkedPipes, tiles, tileClass, false);
				}
			}else if(tileClass.isAssignableFrom(t.getClass())){
				if(t instanceof ConstructBaseTileEntity){
					ConstructBaseTileEntity tile = (ConstructBaseTileEntity) t;
					ConnectionEntry entry = new ConnectionEntry(tile);
					if(!tiles.contains(entry)){
						tiles.add(entry);
					}
				}
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.RENDER){
			return new SimpleTilePacket(this, PacketType.RENDER, 0);
		}
		return null;
	}
	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.RENDER){
			this.checkConnection();
		}
	}
	@Override
	public Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

}
