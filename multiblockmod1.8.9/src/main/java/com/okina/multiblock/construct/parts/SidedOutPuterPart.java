package com.okina.multiblock.construct.parts;

import com.okina.main.TestCore;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.Bezier;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public abstract class SidedOutPuterPart<Target> extends ConstructPartBase {

	/**clients 'connection' has only coordinate info not part reference*/
	public ConnectionEntry<Target>[] connection = new ConnectionEntry[6];
	protected boolean needUpdateEntry = true;

	public SidedOutPuterPart() {
		for (int i = 0; i < 6; i++){
			connection[i] = null;
		}
	}

	@Override
	public void updatePart() {
		super.updatePart();
		if(needUpdateEntry){
			updateEntry();
			needUpdateEntry = false;
		}
	}

	@Override
	public void onRandomDisplayTick() {
		for (int side = 0; side < 6; side++){
			if(connection[side] != null){
				double x1 = coreTile.toReadWorldX(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
				double y1 = coreTile.toReadWorldY(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
				double z1 = coreTile.toReadWorldZ(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
				double x2 = coreTile.toReadWorldX(connection[side].x + 0.5, connection[side].y + 0.5, connection[side].z + 0.5);
				double y2 = coreTile.toReadWorldY(connection[side].x + 0.5, connection[side].y + 0.5, connection[side].z + 0.5);
				double z2 = coreTile.toReadWorldZ(connection[side].x + 0.5, connection[side].y + 0.5, connection[side].z + 0.5);
				int side1 = coreTile.toRealWorldSide(EnumFacing.getFront(side)).getIndex();
				int side2 = coreTile.toRealWorldSide(connection[side].getFront()).getIndex();
				TestCore.spawnParticle(coreTile.getWorld(), TestCore.PARTICLE_BEZIER_DOT, x1, y1, z1, x2, y2, z2, side1, side2, 0x00FFFF);
			}
		}
	}

	protected void updateEntry() {
		for (ConnectionEntry entry : connection){
			if(entry != null){
				ConstructPartBase part = coreTile.getPart(entry.x, entry.y, entry.z);
				if(part != null && getTargetClass().isAssignableFrom(part.getClass())){
					entry.setTile(getTargetClass().cast(part));
				}else{
					entry = null;
				}
			}
		}
	}

	protected void sendConnectionParticlePacket(int side, int color) {
		if(coreTile.renderDetail && connection[side] != null){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setByte("side", (byte) side);
			tag.setInteger("color", color);
			coreTile.sendPacket(PacketType.NBT_CONNECTION, xCoord, yCoord, zCoord, tag);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void processCommand(PacketType type, NBTTagCompound tag) {
		if(type == PacketType.NBT_CONNECTION){//should client
			int side = tag.getByte("side");
			int color = tag.getInteger("color");
			if(connection[side] != null){
				double x1 = coreTile.toReadWorldX(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
				double y1 = coreTile.toReadWorldY(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
				double z1 = coreTile.toReadWorldZ(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
				double x2 = coreTile.toReadWorldX(connection[side].x + 0.5, connection[side].y + 0.5, connection[side].z + 0.5);
				double y2 = coreTile.toReadWorldY(connection[side].x + 0.5, connection[side].y + 0.5, connection[side].z + 0.5);
				double z2 = coreTile.toReadWorldZ(connection[side].x + 0.5, connection[side].y + 0.5, connection[side].z + 0.5);
				int side1 = coreTile.toRealWorldSide(EnumFacing.getFront(side)).getIndex();
				int side2 = coreTile.toRealWorldSide(connection[side].getFront()).getIndex();
				EnumFacing startDir = coreTile.toRealWorldSide(EnumFacing.getFront(side));
				EnumFacing endDir = coreTile.toRealWorldSide(connection[side].getFront());
				Bezier bezier = new Bezier(x1, y1, z1, x2, y2, z2, startDir.getFrontOffsetX() * 2, startDir.getFrontOffsetY() * 2, startDir.getFrontOffsetZ() * 2, -endDir.getFrontOffsetX() * 2, -endDir.getFrontOffsetY() * 2, -endDir.getFrontOffsetZ() * 2);
				//				ConstructPartBase part = coreTile.getPart(connection[side].x, connection[side].y, connection[side].z);
				//				if(part != null){
				//					for (int i = 0; i < 1; i++){
				//						int maxAge = (int) (bezier.getLength() * 20);
				//						double newX = part.getRenderPosX(coreTile.getWorldObj().getTotalWorldTime() + maxAge);
				//						double newY = part.getRenderPosY(coreTile.getWorldObj().getTotalWorldTime() + maxAge);
				//						double newZ = part.getRenderPosZ(coreTile.getWorldObj().getTotalWorldTime() + maxAge);
				//						bezier.end[0] = coreTile.toReadWorldX(newX, newY, newZ);
				//						bezier.end[1] = coreTile.toReadWorldY(newX, newY, newZ);
				//						bezier.end[2] = coreTile.toReadWorldZ(newX, newY, newZ);
				//					}
				//				}
				TestCore.spawnParticle(coreTile.getWorld(), TestCore.PARTICLE_BEZIER, bezier, color);
			}
		}
		super.processCommand(type, tag);
	}

	protected abstract Class<Target> getTargetClass();
	protected abstract boolean shouldDistinguishSide();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.readFromNBT(tag, solid);
		for (int i = 0; i < 6; i++){
			NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
			flagIO[i] = sideTag.getByte("io");
			connection[i] = ConnectionEntry.createFromNBT(sideTag);
		}
		needUpdateEntry = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.writeToNBT(tag, solid);
		for (int i = 0; i < 6; i++){
			NBTTagCompound side = new NBTTagCompound();
			side.setByte("io", (byte) flagIO[i]);
			if(connection[i] != null){
				connection[i].writeToNBT(side);
			}
			tag.setTag("side" + i, side);
		}
	}

}
