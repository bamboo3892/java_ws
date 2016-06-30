package com.okina.multiblock.construct.parts;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ConstructClockPulserTileEntity;
import com.okina.multiblock.construct.tileentity.ISignalReceiver;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClockPulserPart extends SidedOutPuterPart<ISignalReceiver> {

	public int interval;
	public int partialTick = 0;

	public ClockPulserPart() {

	}

	@Override
	public void updatePart() {
		super.updatePart();
		partialTick++;
		if(partialTick == interval){
			coreTile.sendPacket(PacketType.EFFECT, xCoord, yCoord, zCoord, new NBTTagCompound());
			emitSignal();
			partialTick = 0;
		}
	}

	public void emitSignal() {
		for (int i = 0; i < 6; i++){
			if(connection[i] != null && flagIO[i] == 1 && connection[i].getTile() != null){
				sendConnectionParticlePacket(i, 0xb22222);
				connection[i].getTile().onSignalReceived();
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void processCommand(PacketType type, NBTTagCompound tag) {
		if(type == PacketType.EFFECT){
			if(coreTile.renderDetail){
				EnumFacing dir = EnumFacing.getFront((int) (Math.random() * 6));
				double x = coreTile.toReadWorldX(xCoord + Math.random() * 0.4 + 0.3, yCoord + Math.random() * 0.4 + 0.3, zCoord + Math.random() * 0.4 + 0.3);
				double y = coreTile.toReadWorldY(xCoord + Math.random() * 0.4 + 0.3, yCoord + Math.random() * 0.4 + 0.3, zCoord + Math.random() * 0.4 + 0.3);
				double z = coreTile.toReadWorldZ(xCoord + Math.random() * 0.4 + 0.3, yCoord + Math.random() * 0.4 + 0.3, zCoord + Math.random() * 0.4 + 0.3);
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

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.readFromNBT(tag, solid);
		interval = tag.getInteger("interval");
		partialTick = tag.getInteger("partialTick");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.writeToNBT(tag, solid);
		tag.setInteger("interval", interval);
		tag.setInteger("partialTick", partialTick);
	}

	@Override
	public String getNameForNBT() {
		return ConstructClockPulserTileEntity.nameForNBT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructClockPulser[grade];
	}

}
