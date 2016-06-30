package com.okina.multiblock.construct.tileentity;

import com.okina.main.TestCore;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;

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
	public void update() {
		super.update();
		if(worldObj.isRemote){
			if(eventP){
				spawnSignalParticle();
				worldObj.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, TestCore.MODID + ":clockpulser", 0.05F, 1F, false);
				eventP = false;
			}
		}
	}

	@Override
	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			//do nothing
		}else{
			onEventReceived(side);
		}
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

	/**server only*/
	public void onEventReceived(EnumFacing side) {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 6; i++){
			if(connection[i] != null && flagIO[i] == 1 && connection[i].getTile() != null) connection[i].getTile().onSignalReceived();
		}
		TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, 0));
	}

	/**client only*/
	public void spawnSignalParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 5; i++)
			worldObj.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + Math.random() * 0.6 + 0.5, pos.getY() + Math.random() * 0.6 + 0.5, pos.getZ() + Math.random() * 0.6 + 0.5, 0.0D, 0.0D, 0.0D);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int changeIO(EnumFacing side) {
		flagIO[side.getIndex()] = flagIO[side.getIndex()] == 2 ? 1 : 2;
		return flagIO[side.getIndex()];
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.EFFECT){//should client
			eventP = true;
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
		super.writeDetailToNBTForItemStack(tag, solid);
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
