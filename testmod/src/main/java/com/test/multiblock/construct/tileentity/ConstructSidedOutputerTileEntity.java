package com.test.multiblock.construct.tileentity;

import java.util.ArrayList;

import com.test.main.TestCore;
import com.test.multiblock.BlockPipeTileEntity;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.utils.ConnectionEntry;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class ConstructSidedOutputerTileEntity<Target> extends ConstructBaseTileEntity implements IPipeConnectionUser, ILinkConnectionUser {

	public ConnectionEntry<Target>[] connection = new ConnectionEntry[6];

	/**server only*/
	protected ArrayList<ConnectionEntry>[] connections = new ArrayList[6];
	/**server only*/
	protected int[] index = new int[6];

	public ConstructSidedOutputerTileEntity(int grade) {
		super(grade);
		for (int i = 0; i < 6; i++){
			connection[i] = null;
			connections[i] = new ArrayList<ConnectionEntry>();
			index[i] = 0;
		}
	}

	protected boolean needUpdateEntry = true;

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(worldObj.isRemote){
			if(needUpdateEntry){
				updateEntry();
				needUpdateEntry = false;
			}
		}else{
			if(needUpdateEntry){
				updateEntry();
				checkConnectionToEntry();
				needUpdateEntry = false;
			}
		}
	}

	@Override
	public boolean onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			spawnCennectionParticle(side, "cloud");
		}else{
			//do nothing
		}
		return true;
	}

	@Override
	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		//checkConnection();
		if(!player.isSneaking()){
			int n = changeIO(side);
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
				((BlockPipeTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
			}
			TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
			if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(n == 0 ? "input" : n == 1 ? "output" : "disabled"));
		}else{
			if(worldObj.isRemote){
				//do nothing
			}else{
				if(flagIO[side] == 1){
					if(connectNextBlock(side)){
						ConnectionEntry entry = connection[side];
						if(!(entry == null)){
							player.addChatMessage(new ChatComponentText(connection[side].getText()));
						}else{
							player.addChatMessage(new ChatComponentText("No Connection Found"));
						}
					}else{
						player.addChatMessage(new ChatComponentText("No Connection Found"));
					}
					this.spawnParticle = true;
					this.pSide = side;
					TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
				}
			}
		}
		return false;
	}

	public int changeIO(int side) {
		if(side < 0 || side >= 6) return 3;
		flagIO[side] = flagIO[side] == 2 ? 0 : flagIO[side] + 1;
		return flagIO[side];
	}

	/**
	 * @param side
	 * @return have any connection?
	 */
	public boolean connectNextBlock(int side) {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		checkConnectionToEntryForSide(side);
		if(connections[side].size() <= 0){
			index[side] = 0;
			connection[side] = null;
			return false;
		}else{
			index[side] = (index[side] >= connections[side].size() - 1 ? 0 : index[side] + 1);
			connection[side] = connections[side].get(index[side]);
			return true;
		}
	}

	public void spawnCennectionParticle(int side, String name) {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(side < 0 || side > 5) return;
		if(connection[side] != null){
			ConnectionEntry entry = connection[side];

			if(worldObj.getTileEntity(entry.x, entry.y, entry.z) instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) worldObj.getTileEntity(entry.x, entry.y, entry.z);
				baseTile.restRenderTicks = 100;
				baseTile.renderSide = entry.side;
			}

			if(shouldDistinguishSide()){
				ForgeDirection dir = ForgeDirection.getOrientation(entry.side);
				for (int i = 0; i < 5; i++)
					worldObj.spawnParticle(name, entry.x + dir.offsetX * 0.6 + 0.5, entry.y + dir.offsetY * 0.6 + 0.5, entry.z + dir.offsetZ * 0.6 + 0.5, 0.0D, 0.0D, 0.0D);
			}else{
				for (int i = 0; i < 8; i++){
					double offsetX = (i & 4) == 4 ? 0.4 : -0.4;
					double offsetY = (i & 2) == 2 ? 0.4 : -0.4;
					double offsetZ = (i & 1) == 1 ? 0.4 : -0.4;
					worldObj.spawnParticle(name, entry.x + offsetX + 0.5, entry.y + offsetY + 0.5, entry.z + offsetZ + 0.5, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

	@Override
	public void needUpdateEntry() {
		needUpdateEntry = true;
	}

	private void checkConnectionToEntry() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int side = 0; side < 6; side++){
			checkConnectionToEntryForSide(side);
		}
		TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
	}

	/**server only*/
	public void checkConnectionToEntryForSide(int side) {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(side < 0 || side > 5) return;
		connections[side].clear();
		if(shouldDistinguishSide()){
			connections[side].add(new ConnectionEntry(this, ForgeDirection.getOrientation(side).ordinal()));
		}else{
			connections[side].add(new ConnectionEntry(this));
		}
		ArrayList ppppppppppp = new ArrayList<BlockPipeTileEntity>();
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		int newX = xCoord + dir.offsetX;
		int newY = yCoord + dir.offsetY;
		int newZ = zCoord + dir.offsetZ;
		TileEntity tile = worldObj.getTileEntity(newX, newY, newZ);
		if(tile instanceof BlockPipeTileEntity && !tile.isInvalid()){
			BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
			pipe.findConnection(ppppppppppp, connections[side], getTargetClass(), shouldDistinguishSide());
		}else if(tile != null && getTargetClass().isAssignableFrom(tile.getClass())){
			if(tile instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) tile;
				if(baseTile.flagIO[dir.getOpposite().ordinal()] == 0){
					if(shouldDistinguishSide()){
						connections[side].add(new ConnectionEntry(getTargetClass().cast(baseTile), ForgeDirection.getOrientation(side).getOpposite().ordinal()));
					}else{
						connections[side].add(new ConnectionEntry(getTargetClass().cast(baseTile)));
					}
				}
			}
		}
		connections[side].remove(0);

		//find past connection
		for (int i = 0; i < connections[side].size(); i++){
			ConnectionEntry entry = connections[side].get(i);
			if(entry == null){
				continue;
			}else if(connection[side] != null && connection[side].equals(entry)){
				index[side] = i;
				return;
			}
		}
		connection[side] = null;
	}

	protected boolean updateEntry() {
		for (ConnectionEntry entry : connection){
			if(entry != null){
				TileEntity tile = worldObj.getTileEntity(entry.x, entry.y, entry.z);
				if(tile != null && getTargetClass().isAssignableFrom(tile.getClass()) && !tile.isInvalid()){
					entry.setTile(getTargetClass().cast(tile));
				}else{
					entry = null;
				}
			}
		}
		TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean canStartAt(int side) {
		return this.flagIO[side] == 1;
	}

	@Override
	public boolean tryConnect(ConstructBaseTileEntity tile, int clickedSide, int linkUserSide) {
		if(!worldObj.isRemote){
			if(tile != null && getTargetClass().isAssignableFrom(tile.getClass())){
				if(this.flagIO[linkUserSide] == 1 && tile.flagIO[clickedSide] == 0){
					ConnectionEntry pastEntry = connection[linkUserSide];
					if(shouldDistinguishSide()){
						connection[linkUserSide] = new ConnectionEntry(getTargetClass().cast(tile), clickedSide);
					}else{
						connection[linkUserSide] = new ConnectionEntry(getTargetClass().cast(tile));
					}
					checkConnectionToEntryForSide(linkUserSide);
					if(connection[linkUserSide] != null){
						TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
						return true;
					}else{
						connection[linkUserSide] = pastEntry;
						return false;
					}
				}
			}
		}
		return false;
	}

	protected abstract Class<Target> getTargetClass();
	protected abstract boolean shouldDistinguishSide();

	/**use to send connection packet with spawn particle*/
	protected boolean spawnParticle = false;
	protected int pSide = -1;

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.NBT_CONNECTION){
			NBTTagCompound tag = new NBTTagCompound();
			for (int i = 0; i < 6; i++){
				if(connection[i] != null){
					NBTTagCompound sideTag = new NBTTagCompound();
					connection[i].writeToNBT(sideTag);
					tag.setTag("side" + i, sideTag);
				}
			}
			if(spawnParticle){
				tag.setBoolean("connectP", true);
				tag.setInteger("pSide", pSide);
				spawnParticle = false;
				pSide = -1;
			}
			return new SimpleTilePacket(this, PacketType.NBT_CONNECTION, tag);
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.NBT_CONNECTION && value instanceof NBTTagCompound){//should client
			NBTTagCompound tag = (NBTTagCompound) value;
			for (int i = 0; i < 6; i++){
				NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
				connection[i] = ConnectionEntry.createFromNBT(sideTag);
			}
			needUpdateEntry = true;
			if(tag.getBoolean("connectP")) this.spawnCennectionParticle(tag.getInteger("pSide"), "cloud");
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		checkConnectionToEntry();
		for (int i = 0; i < 6; i++){
			NBTTagCompound side = new NBTTagCompound();
			side.setByte("io", (byte) flagIO[i]);
			if(connection[i] != null && solid.isInclude(connection[i].getPosition())){
				side.setInteger("x", connection[i].x - solid.minX);
				side.setInteger("y", connection[i].y - solid.minY);
				side.setInteger("z", connection[i].z - solid.minZ);
				side.setInteger("side", connection[i].side);
			}
			tag.setTag("side" + i, side);
		}
	}

	/**read 6 compound from parameter tag*/
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		for (int i = 0; i < 6; i++){
			NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
			flagIO[i] = sideTag.getByte("io");
			index[i] = sideTag.getInteger("index");
			connection[i] = ConnectionEntry.createFromNBT(sideTag);
		}
		needUpdateEntry = true;
	}

	/**add 6 compound ("side0", ... , "side5") to parameter tag*/
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		checkConnectionToEntry();
		for (int i = 0; i < 6; i++){
			NBTTagCompound side = new NBTTagCompound();
			side.setByte("io", (byte) flagIO[i]);
			side.setInteger("index", index[i]);
			if(connection[i] != null){
				connection[i].writeToNBT(side);
			}
			tag.setTag("side" + i, side);
		}
	}

}
