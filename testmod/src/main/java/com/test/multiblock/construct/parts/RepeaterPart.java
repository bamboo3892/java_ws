package com.test.multiblock.construct.parts;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructRepeaterTileEntity;
import com.test.multiblock.construct.tileentity.ISignalReceiver;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.utils.RectangularSolid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class RepeaterPart extends SidedOutPuterPart<ISignalReceiver> implements ISignalReceiver {

	public boolean activate;
	public int delay = 20;
	public int partialTick = 0;

	public RepeaterPart() {

	}

	@Override
	public void updatePart() {
		super.updatePart();
		if(activate) partialTick++;
		if(partialTick >= delay){
			coreTile.sendPacket(PacketType.EFFECT, xCoord, yCoord, zCoord, new NBTTagCompound());
			emitSignal();
			activate = false;
			partialTick = 0;
		}
	}

	public void emitSignal() {
		for (int i = 0; i < 6; i++){
			if(connection[i] != null && flagIO[i] == 1 && connection[i].getTile() != null) {
				this.sendConnectionParticlePacket(i, 0xb22222);
				connection[i].getTile().onSignalReceived();
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		activate = true;
		partialTick = 0;
	}
	
	@Override
	public void processCommand(PacketType type, NBTTagCompound tag) {
		if(type == PacketType.EFFECT){
			ForgeDirection dir = ForgeDirection.getOrientation((int) (Math.random() * 6));
			for (int i = 0; i < 3; i++){
				double x = coreTile.toReadWorldX(xCoord + Math.random() * 0.6 + 0.5, yCoord + Math.random() * 0.6 + 0.5, zCoord + Math.random() * 0.6 + 0.5);
				double y = coreTile.toReadWorldY(xCoord + Math.random() * 0.6 + 0.5, yCoord + Math.random() * 0.6 + 0.5, zCoord + Math.random() * 0.6 + 0.5);
				double z = coreTile.toReadWorldZ(xCoord + Math.random() * 0.6 + 0.5, yCoord + Math.random() * 0.6 + 0.5, zCoord + Math.random() * 0.6 + 0.5);
				coreTile.getWorldObj().spawnParticle("reddust", x, y, z, 0.0D, 0.0D, 0.0D);
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		delay = tag.getInteger("delay");
		partialTick = tag.getInteger("partialTick");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		tag.setInteger("delay", delay);
		tag.setInteger("partialTick", partialTick);
	}

	@Override
	public String getNameForNBT() {
		return ConstructRepeaterTileEntity.nameForNBT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructRepeater[grade];
	}

}
