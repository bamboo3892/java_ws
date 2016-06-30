package com.okina.multiblock;

import java.util.ArrayList;

import com.okina.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.okina.multiblock.construct.tileentity.IPipeConnectionUser;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.ConnectionEntry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class BlockPipeTileEntity extends TileEntity implements ITickable, ISimpleTilePacketUser {

	public boolean[] connection = new boolean[6];
	private boolean needUpdate = true;

	public BlockPipeTileEntity() {

	}

	@Override
	public void update() {
		if(needUpdate){
			checkConnection();
			needUpdate = false;
		}
	}

	/**effective only on client*/
	public void checkConnection() {
		//		for (EnumFacing dir : EnumFacing.VALUES){
		//			boolean b = false;
		//			TileEntity tile = worldObj.getTileEntity(pos.add(dir.getDirectionVec()));
		//			if(tile instanceof ConstructBaseTileEntity){
		//				b = ((ConstructBaseTileEntity) tile).flagIO[dir.getOpposite().getIndex()] != 2;
		//			}else if(tile instanceof BlockPipeTileEntity && worldObj.getBlockState(pos.add(dir.getDirectionVec())).getBlock() == TestCore.pipe && worldObj.getBlockState(pos).getValue(BlockPipe.COLOR) == worldObj.getBlockState(pos.add(dir.getDirectionVec())).getValue(BlockPipe.COLOR)){
		//				b = true;
		//			}
		//			connection[dir.ordinal()] = b;
		//		}
		//		worldObj.markBlockRangeForRenderUpdate(pos, pos);
		//		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.RENDER);
	}

	/**objective tile must extend ConstructBaseTileEntity*/
	public void findConnection(ArrayList<BlockPipeTileEntity> checkedPipes, ArrayList<ConnectionEntry> tiles, Class tileClass, boolean sideMode) {
		checkedPipes.add(this);
		for (EnumFacing dir : EnumFacing.VALUES){
			if(dir.ordinal() >= 6) break;
			TileEntity t = worldObj.getTileEntity(pos.add(dir.getDirectionVec()));
			if(t == null) continue;
			if(t instanceof BlockPipeTileEntity && getBlockMetadata() == t.getBlockMetadata()){
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
		invalidate();
		if(worldObj.isRemote){
			//do nothing
		}else{
			ArrayList<BlockPipeTileEntity> checkedPipes = new ArrayList<BlockPipeTileEntity>();
			ArrayList<ConnectionEntry> tiles = new ArrayList<ConnectionEntry>();
			Class tileClass = IPipeConnectionUser.class;
			checkedPipes.add(this);
			for (EnumFacing dir : EnumFacing.VALUES){
				if(dir.ordinal() >= 6) break;
				TileEntity t = worldObj.getTileEntity(pos.add(dir.getDirectionVec()));
				if(t == null || t.isInvalid()){
					continue;
				}else if(t instanceof BlockPipeTileEntity && getBlockMetadata() == t.getBlockMetadata()){
					BlockPipeTileEntity pipe = (BlockPipeTileEntity) t;
					if(!checkedPipes.contains(pipe)){
						pipe.findAllConnection(checkedPipes, tiles, tileClass);
					}
				}else if(tileClass.isAssignableFrom(t.getClass())){
					TileEntity tile = (t);
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
		for (EnumFacing dir : EnumFacing.VALUES){
			if(dir.ordinal() >= 6) break;
			TileEntity t = worldObj.getTileEntity(pos.add(dir.getDirectionVec()));
			if(t == null) continue;
			if(t instanceof BlockPipeTileEntity && getBlockMetadata() == t.getBlockMetadata()){
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
			checkConnection();
		}
	}
	@Override
	public BlockPos getPosition() {
		return pos;
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
