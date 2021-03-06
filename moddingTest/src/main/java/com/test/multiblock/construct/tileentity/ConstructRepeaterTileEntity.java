package com.test.multiblock.construct.tileentity;

import com.test.main.TestCore;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.tileentity.IGuiSliderUser;
import com.test.utils.RectangularSolid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ConstructRepeaterTileEntity extends ConstructSidedOutputerTileEntity<ISignalReceiver> implements ISignalReceiver, IGuiSliderUser {

	public static final String nameForNBT = "repeater";

	/**use only on server*/
	public boolean activate;
	public int delay = 20;
	public int partialTick = 0;

	private boolean repeaterP;

	public ConstructRepeaterTileEntity() {
		this(0);
	}

	public ConstructRepeaterTileEntity(int grade) {
		super(grade);
	}

	public void onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		player.openGui(TestCore.instance, TestCore.SLIDERINPUT_GUI_ID, worldObj, xCoord, yCoord, zCoord);
	}
	public void onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			spawnCennectionParticle(side, "reddust");
		}else{
			//do nothing
		}
	}

	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote){
			if(activate) partialTick++;
			if(partialTick >= delay){
				emitSignal();
				TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.EFFECT, 0));
				activate = false;
				partialTick = 0;
			}
		}else{
			if(repeaterP){
				spawnSignalParticle();
				worldObj.playSound(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, TestCore.MODID + ":clockpulser", 0.3F, 1F, false);
				repeaterP = false;
			}
		}
	}

	public void emitSignal() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 6; i++){
			if(connection[i] != null && flagIO[i] == 1 && connection[i].getTile() != null) connection[i].getTile().onSignalReceived();
		}
	}

	public void spawnSignalParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 5; i++)
			worldObj.spawnParticle("reddust", xCoord + Math.random() * 0.6 + 0.5, yCoord + Math.random() * 0.6 + 0.5, zCoord + Math.random() * 0.6 + 0.5, 0.0D, 0.0D, 0.0D);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		activate = true;
		partialTick = 0;
	}

	protected Class<ISignalReceiver> getTargetClass() {
		return ISignalReceiver.class;
	}
	protected boolean shouldDistinguishSide() {
		return false;
	}
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.SLIDER_INPUT && value instanceof Integer){//both side
			this.delay = (Integer)value;
			this.partialTick = 0;
		}else if(type == PacketType.EFFECT){//should client
			this.repeaterP = true;
		}
	}
	public int getValue() {
		return this.delay;
	}
	public String getContainerName() {
		return "repeater";
	}
	public int getMinValue() {
		return 1;
	}
	public int getMaxValue() {
		return 100;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		tag.setString("name", getNameForNBT());
		tag.setInteger("delay", delay);
		NBTTagCompound[] side = new NBTTagCompound[6];
		for (int i = 0; i < 6; i++){
			if(connection[i].getTile() != null){
				TileEntity tile = (TileEntity) connection[i].getTile();
				side[i] = new NBTTagCompound();
				side[i].setByte("io", (byte) flagIO[i]);
				side[i].setInteger("index", solid.toIndex(tile.xCoord, tile.yCoord, tile.zCoord, false));
				tag.setTag("side" + i, side[i]);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		delay = tag.getInteger("delay");
		partialTick = tag.getInteger("partialTick");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("delay", delay);
		tag.setInteger("partialTick", partialTick);
	}

}
