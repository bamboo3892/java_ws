package com.okina.multiblock.construct.tileentity;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;

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
	public void update() {
		super.update();
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
	public boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			spawnCennectionParticle(side, EnumParticleTypes.CLOUD);
		}else{
			//do nothing
		}
		return true;
	}
	@Override
	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		//checkConnection();
		if(!player.isSneaking()){
			int n = changeIO(side);
			if(worldObj.getTileEntity(pos.add(side.getDirectionVec())) instanceof BlockPipeTileEntity){
				((BlockPipeTileEntity) worldObj.getTileEntity(pos.add(side.getDirectionVec()))).checkConnection();
			}
			TestCore.proxy.markForTileUpdate(pos, PacketType.FLAG_IO);
			if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(n == 0 ? "input" : n == 1 ? "output" : "disabled"));
		}else{
			if(worldObj.isRemote){
				//do nothing
			}else{
				if(flagIO[side.getIndex()] == 1){
					if(connectNextBlock(side)){
						ConnectionEntry entry = connection[side.getIndex()];
						if(!(entry == null)){
							player.addChatMessage(new ChatComponentText(connection[side.getIndex()].getText()));
						}else{
							player.addChatMessage(new ChatComponentText("No Connection Found"));
						}
					}else{
						player.addChatMessage(new ChatComponentText("No Connection Found"));
					}
					this.spawnParticle = true;
					this.pSide = side;
					TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONNECTION);
				}
			}
		}
		return false;
	}

	public int changeIO(EnumFacing side) {
		flagIO[side.getIndex()] = flagIO[side.getIndex()] == 2 ? 0 : flagIO[side.getIndex()] + 1;
		return flagIO[side.getIndex()];
	}

	/**
	 * @param side
	 * @return have any connection?
	 */
	public boolean connectNextBlock(EnumFacing side) {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		checkConnectionToEntryForSide(side);
		if(connections[side.getIndex()].size() <= 0){
			index[side.getIndex()] = 0;
			connection[side.getIndex()] = null;
			return false;
		}else{
			index[side.getIndex()] = (index[side.getIndex()] >= connections[side.getIndex()].size() - 1 ? 0 : index[side.getIndex()] + 1);
			connection[side.getIndex()] = connections[side.getIndex()].get(index[side.getIndex()]);
			return true;
		}
	}

	public void spawnCennectionParticle(EnumFacing side, EnumParticleTypes type) {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(connection[side.getIndex()] != null){
			ConnectionEntry entry = connection[side.getIndex()];

			if(worldObj.getTileEntity(entry.getPosition()) instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) worldObj.getTileEntity(entry.getPosition());
				baseTile.restRenderTicks = 100;
				baseTile.renderSide = entry.side;
			}

			if(shouldDistinguishSide()){
				EnumFacing dir = entry.getFront();
				for (int i = 0; i < 5; i++)
					worldObj.spawnParticle(type, entry.x + dir.getFrontOffsetX() * 0.6 + 0.5, entry.y + dir.getFrontOffsetY() * 0.6 + 0.5, entry.z + dir.getFrontOffsetZ() * 0.6 + 0.5, 0.0D, 0.0D, 0.0D);
			}else{
				for (int i = 0; i < 8; i++){
					double offsetX = (i & 4) == 4 ? 0.4 : -0.4;
					double offsetY = (i & 2) == 2 ? 0.4 : -0.4;
					double offsetZ = (i & 1) == 1 ? 0.4 : -0.4;
					worldObj.spawnParticle(type, entry.x + offsetX + 0.5, entry.y + offsetY + 0.5, entry.z + offsetZ + 0.5, 0.0D, 0.0D, 0.0D);
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
		for (EnumFacing side : EnumFacing.VALUES){
			checkConnectionToEntryForSide(side);
		}
		TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONNECTION);
	}

	/**server only*/
	public void checkConnectionToEntryForSide(EnumFacing side) {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		connections[side.getIndex()].clear();
		if(shouldDistinguishSide()){
			connections[side.getIndex()].add(new ConnectionEntry(this, side));
		}else{
			connections[side.getIndex()].add(new ConnectionEntry(this));
		}
		ArrayList ppppppppppp = new ArrayList<BlockPipeTileEntity>();
		BlockPos newPos = pos.add(side.getDirectionVec());
		TileEntity tile = worldObj.getTileEntity(newPos);
		if(tile instanceof BlockPipeTileEntity && !tile.isInvalid()){
			BlockPipeTileEntity pipe = (BlockPipeTileEntity) tile;
			pipe.findConnection(ppppppppppp, connections[side.getIndex()], getTargetClass(), shouldDistinguishSide());
		}else if(tile != null && getTargetClass().isAssignableFrom(tile.getClass())){
			if(tile instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) tile;
				if(baseTile.flagIO[side.getIndex()] == 0){
					if(shouldDistinguishSide()){
						connections[side.getIndex()].add(new ConnectionEntry(getTargetClass().cast(baseTile), side));
					}else{
						connections[side.getIndex()].add(new ConnectionEntry(getTargetClass().cast(baseTile)));
					}
				}
			}
		}
		connections[side.getIndex()].remove(0);

		//find past connection
		for (int i = 0; i < connections[side.getIndex()].size(); i++){
			ConnectionEntry entry = connections[side.getIndex()].get(i);
			if(entry == null){
				continue;
			}else if(connection[side.getIndex()] != null && connection[side.getIndex()].equals(entry)){
				index[side.getIndex()] = i;
				return;
			}
		}
		connection[side.getIndex()] = null;
	}

	protected boolean updateEntry() {
		for (ConnectionEntry entry : connection){
			if(entry != null){
				TileEntity tile = worldObj.getTileEntity(entry.getPosition());
				if(tile != null && getTargetClass().isAssignableFrom(tile.getClass()) && !tile.isInvalid()){
					entry.setTile(getTargetClass().cast(tile));
				}else{
					entry = null;
				}
			}
		}
		TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONNECTION);
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean canStartAt(EnumFacing side) {
		return flagIO[side.getIndex()] == 1;
	}

	@Override
	public boolean tryConnect(ConstructBaseTileEntity tile, EnumFacing clickedSide, EnumFacing linkUserSide) {
		if(!worldObj.isRemote){
			if(tile != null && getTargetClass().isAssignableFrom(tile.getClass())){
				if(flagIO[linkUserSide.getIndex()] == 1 && tile.flagIO[clickedSide.getIndex()] == 0){
					ConnectionEntry pastEntry = connection[linkUserSide.getIndex()];
					if(shouldDistinguishSide()){
						connection[linkUserSide.getIndex()] = new ConnectionEntry(getTargetClass().cast(tile), clickedSide);
					}else{
						connection[linkUserSide.getIndex()] = new ConnectionEntry(getTargetClass().cast(tile));
					}
					checkConnectionToEntryForSide(linkUserSide);
					if(connection[linkUserSide.getIndex()] != null){
						TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONNECTION);
						return true;
					}else{
						connection[linkUserSide.getIndex()] = pastEntry;
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
	protected EnumFacing pSide = null;

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
			if(spawnParticle && pSide != null){
				tag.setBoolean("connectP", true);
				tag.setInteger("pSide", pSide.getIndex());
				spawnParticle = false;
				pSide = null;
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
			if(tag.getBoolean("connectP")) this.spawnCennectionParticle(EnumFacing.getFront(tag.getInteger("pSide")), EnumParticleTypes.CLOUD);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
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

	@Override
	public void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop) {
		String message = null;
		int color = 0;
		if(flagIO[mop.sideHit.getIndex()] == 0){
			message = "Input";
			color = 0x00BFFF;
		}else if(flagIO[mop.sideHit.getIndex()] == 1 && connection[mop.sideHit.getIndex()] != null && !mc.theWorld.isAirBlock(connection[mop.sideHit.getIndex()].getPosition())){
			message = "Output >> " + mc.theWorld.getBlockState(connection[mop.sideHit.getIndex()].getPosition()).getBlock().getLocalizedName();
			color = 0xFF8C00;
		}else if(flagIO[mop.sideHit.getIndex()] == 1){
			message = "Output";
			color = 0xFF8C00;
		}
		if(message != null){
			ScaledResolution sr = new ScaledResolution(mc);
			Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);
			int length = mc.fontRendererObj.getStringWidth(message);

			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(center.getX(), center.getY(), 0);
			GL11.glTranslatef(-length / 2, 20, 0);
			mc.fontRendererObj.drawString(message, 0, 0, color, true);
			GL11.glPopMatrix();
		}
	}

}
