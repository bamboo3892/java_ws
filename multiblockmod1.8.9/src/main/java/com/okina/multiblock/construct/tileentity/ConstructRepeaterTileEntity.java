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
			if(activate) partialTick++;
			if(partialTick >= delay){
				emitSignal();
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, 0));
				activate = false;
				partialTick = 0;
			}
		}else{
			if(repeaterP){
				spawnSignalParticle();
				worldObj.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, TestCore.MODID + ":clockpulser", 0.05F, 1F, false);
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
			worldObj.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + Math.random() * 0.6 + 0.5, pos.getY() + Math.random() * 0.6 + 0.5, pos.getZ() + Math.random() * 0.6 + 0.5, 0.0D, 0.0D, 0.0D);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		activate = true;
		partialTick = 0;
	}

	@Override
	protected Class<ISignalReceiver> getTargetClass() {
		return ISignalReceiver.class;
	}
	@Override
	protected boolean shouldDistinguishSide() {
		return false;
	}
	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.SLIDER_INPUT && value instanceof Integer){//both side
			delay = (Integer) value;
			partialTick = 0;
		}else if(type == PacketType.EFFECT){//should client
			repeaterP = true;
		}
	}
	@Override
	public int getValue() {
		return delay;
	}
	@Override
	public String getContainerName() {
		return "repeater";
	}
	@Override
	public int getMinValue() {
		return 1;
	}
	@Override
	public int getMaxValue() {
		return 100;
	}
	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return serverSide ? new SliderInputContainer(this) : new GuiSliderInput(this);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		tag.setInteger("delay", delay);
		tag.setInteger("partialTick", partialTick);
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
