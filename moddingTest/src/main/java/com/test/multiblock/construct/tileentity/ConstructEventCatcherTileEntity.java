package com.test.multiblock.construct.tileentity;

import com.test.main.TestCore;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.tileentity.ISimpleTilePacketUser;
import com.test.utils.RectangularSolid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ConstructEventCatcherTileEntity extends ConstructSidedOutputerTileEntity<ISignalReceiver> implements ISimpleTilePacketUser {

	public static final String nameForNBT = "eventCatcher";

	private boolean eventP = false;

	public ConstructEventCatcherTileEntity() {
		this(0);
	}

	public ConstructEventCatcherTileEntity(int grade) {
		super(grade);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(worldObj.isRemote){
			if(eventP){
				spawnSignalParticle();
				worldObj.playSound(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, TestCore.MODID + ":clockpulser", 0.3F, 1F, false);
				eventP = false;
			}
		}
	}

	@Override
	public void onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			//do nothing
		}else{
			onEventReceived(side);
		}
	}
	@Override
	public void onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			spawnCennectionParticle(side, "reddust");
		}else{
			//do nothing
		}
	}

	/**server only*/
	public void onEventReceived(int side) {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 6; i++){
			if(connection[i] != null && flagIO[i] == 1 && connection[i].getTile() != null) connection[i].getTile().onSignalReceived();
		}
		TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.EFFECT, 0));
	}

	/**client only*/
	public void spawnSignalParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 5; i++)
			worldObj.spawnParticle("reddust", xCoord + Math.random() * 0.6 + 0.5, yCoord + Math.random() * 0.6 + 0.5, zCoord + Math.random() * 0.6 + 0.5, 0.0D, 0.0D, 0.0D);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int changeIO(int side) {
		if(side < 0 || side >= 6) return 3;
		flagIO[side] = flagIO[side] == 2 ? 1 : 2;
		return flagIO[side];
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.EFFECT){//should client
			this.eventP = true;
		}
	}

	@Override
	protected Class<ISignalReceiver> getTargetClass() {
		return ISignalReceiver.class;
	}
	@Override
	protected boolean shouldDistinguishSide() {
		return false;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

}
