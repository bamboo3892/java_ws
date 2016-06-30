package com.okina.multiblock.construct.parts;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ConstructEventCatcherTileEntity;
import com.okina.multiblock.construct.tileentity.ISignalReceiver;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventCatcherPart extends SidedOutPuterPart<ISignalReceiver> {

	public EventCatcherPart() {

	}

	/**server only*/
	public void onEventReceived(EnumFacing side) {
		for (int i = 0; i < 6; i++){
			if(connection[i] != null && flagIO[i] == 1 && connection[i].getTile() != null){
				sendConnectionParticlePacket(i, 0xb22222);
				connection[i].getTile().onSignalReceived();
			}
		}
		coreTile.sendPacket(PacketType.EFFECT, xCoord, yCoord, zCoord, new NBTTagCompound());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void processCommand(PacketType type, NBTTagCompound tag) {
		if(type == PacketType.EFFECT){
			EnumFacing dir = EnumFacing.getFront((int) (Math.random() * 6));
			for (int i = 0; i < 3; i++){
				double x = coreTile.toReadWorldX(xCoord + Math.random() * 0.6 + 0.5, yCoord + Math.random() * 0.6 + 0.5, zCoord + Math.random() * 0.6 + 0.5);
				double y = coreTile.toReadWorldY(xCoord + Math.random() * 0.6 + 0.5, yCoord + Math.random() * 0.6 + 0.5, zCoord + Math.random() * 0.6 + 0.5);
				double z = coreTile.toReadWorldZ(xCoord + Math.random() * 0.6 + 0.5, yCoord + Math.random() * 0.6 + 0.5, zCoord + Math.random() * 0.6 + 0.5);
				coreTile.getWorld().spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
			}
		}
		super.processCommand(type, tag);
	}

	@Override
	protected Class<ISignalReceiver> getTargetClass() {
		return ISignalReceiver.class;
	}
	@Override
	protected boolean shouldDistinguishSide() {
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.readFromNBT(tag, solid);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.writeToNBT(tag, solid);
	}

	@Override
	public String getNameForNBT() {
		return ConstructEventCatcherTileEntity.nameForNBT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructEventCatcher[grade];
	}

}
