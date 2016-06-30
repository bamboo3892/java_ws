package com.okina.multiblock.construct.tileentity;

import com.okina.client.gui.GuiSliderInput;
import com.okina.main.TestCore;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.server.gui.SliderInputContainer;
import com.okina.tileentity.IGuiSliderUser;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;

public class ConstructClockPulserTileEntity extends ConstructSidedOutputerTileEntity<ISignalReceiver> implements IGuiSliderUser {

	public static final String nameForNBT = "clockPulser";
	public static final int[] minInterval = { 40, 20, 10, 5, 1 };
	public static final int[] maxInterval = { 60, 80, 100, 500, 1000 };

	//only use on server
	public int interval;
	public int partialTick = 0;

	/**client only*/
	private boolean clockP = false;

	public ConstructClockPulserTileEntity() {
		this(0);
	}

	public ConstructClockPulserTileEntity(int grade) {
		super(grade);
		interval = maxInterval[grade];
	}

	@Override
	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0, worldObj, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	@Override
	public boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			spawnCennectionParticle(side, EnumParticleTypes.REDSTONE);
		}else{
			//do nothing
		}
		return true;
	}

	@Override
	public void update() {
		super.update();
		if(!worldObj.isRemote){
			partialTick++;
			if(partialTick == interval){
				emitSignal();
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, 0));
				partialTick = 0;
			}
		}else{
			if(clockP){
				spawnSignalParticle();
				worldObj.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, TestCore.MODID + ":clockpulser", 0.05F, 1F, false);
				clockP = false;
			}
		}
	}

	/**server only*/
	public void emitSignal() {
		if(worldObj.isRemote) throw new RuntimeException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 6; i++){
			if(connection[i] != null && flagIO[i] == 1 && connection[i].getTile() != null) connection[i].getTile().onSignalReceived();
		}
	}

	/**client only*/
	public void spawnSignalParticle() {
		if(!worldObj.isRemote) throw new RuntimeException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 5; i++)
			worldObj.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + Math.random() * 0.6 + 0.5, pos.getY() + Math.random() * 0.6 + 0.5, pos.getZ() + Math.random() * 0.6 + 0.5, 0.0D, 0.0D, 0.0D);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int changeIO(EnumFacing side) {
		flagIO[side.getIndex()] = flagIO[side.getIndex()] == 1 ? 2 : 1;
		return flagIO[side.getIndex()];
	}

	@Override
	protected Class<ISignalReceiver> getTargetClass() {
		return ISignalReceiver.class;
	}
	@Override
	protected boolean shouldDistinguishSide() {
		return false;
	}

	/**process FLAG_IO, SLIDER_INPUT, EFFECT*/
	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.SLIDER_INPUT && value instanceof Integer){//both side
			interval = (Integer) value;
			partialTick = 0;
		}else if(type == PacketType.EFFECT){//should client
			clockP = true;
		}
	}
	@Override
	public int getValue() {
		return interval;
	}
	@Override
	public String getContainerName() {
		return "clockpulser";
	}
	@Override
	public int getMinValue() {
		return minInterval[grade];
	}
	@Override
	public int getMaxValue() {
		return maxInterval[grade];
	}
	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new SliderInputContainer(this) : new GuiSliderInput(this);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		tag.setString("name", getNameForNBT());
		tag.setInteger("interval", interval);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		interval = tag.getInteger("interval");
		partialTick = tag.getInteger("partialTick");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("interval", interval);
		tag.setInteger("partialTick", partialTick);
	}

}
