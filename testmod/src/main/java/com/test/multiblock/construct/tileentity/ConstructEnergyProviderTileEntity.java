package com.test.multiblock.construct.tileentity;

import java.util.Random;

import com.test.main.TestCore;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.utils.RectangularSolid;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ConstructEnergyProviderTileEntity extends ConstructBaseTileEntity implements IEnergyHandler {

	public static final String nameForNBT = "energyprovider";
	public static final int[] storage = { 400000, 2000000, 20000000, 80000000, 100000000 };
	public static final int[] transfer = { 400000, 400000, 400000, 400000, 400000 };

	private EnergyStorage energyStorage;
	private int pastEnergyLevel;

	//	private boolean undateRender = true;

	public ConstructEnergyProviderTileEntity() {
		this(0);
	}

	public ConstructEnergyProviderTileEntity(int grade) {
		super(grade);
		energyStorage = new EnergyStorage(storage[grade], transfer[grade]);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote){
			int energyLevel = getEvergyLevel();
			if(pastEnergyLevel != energyLevel){
				pastEnergyLevel = energyLevel;
				//				undateRender = true;
				TestCore.proxy.markForTileUpdate(this.getPosition(), PacketType.ENERGY);
				//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		//		else{
		//			if(undateRender){
		//				worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		//				undateRender = false;
		//			}
		//		}
	}

	@Override
	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}
	@Override
	public boolean onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}
	@Override
	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	public int sendEnergy(ILinkConnectionUser receiver, int maxSend) {
		int send = energyStorage.extractEnergy(maxSend, false);
		TileEntity tile = (TileEntity) receiver;
		Random rand = worldObj.rand;
		for (int i = 0; i < (int) ((float) send / 50F); i++){
			double startX = xCoord + 0.4 + rand.nextDouble() * 0.2;
			double startY = yCoord + 0.4 + rand.nextDouble() * 0.2;
			double startZ = zCoord + 0.4 + rand.nextDouble() * 0.2;
			double endX = tile.xCoord - startX + 0.5;
			double endY = tile.yCoord - startY + 0.5;
			double endZ = tile.zCoord - startZ + 0.5;
			TestCore.spawnParticle(worldObj, TestCore.PARTICLE_ENERGY, startX, startY, startZ, endX, endY, endZ);
		}
		return send;
	}

	public int getEvergyLevel() {
		return (int) ((float) energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored() * 15F);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.ENERGY){
			return new SimpleTilePacket(this, PacketType.ENERGY, energyStorage.getEnergyStored());
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.ENERGY && value instanceof Integer){
			this.energyStorage.setEnergyStored((Integer) value);
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return energyStorage.receiveEnergy(maxReceive, simulate);
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return energyStorage.extractEnergy(maxExtract, simulate);
	}
	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energyStorage.getEnergyStored();
	}
	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energyStorage.getMaxEnergyStored();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		energyStorage.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyStorage = new EnergyStorage(storage[grade], transfer[grade]);
		energyStorage.readFromNBT(tag);
		//		energyStorage.setEnergyStored(energyStorage.getMaxEnergyStored());
		//		this.undateRender = tag.getBoolean("undateRender");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		energyStorage.writeToNBT(tag);
		//		tag.setBoolean("undateRender", undateRender);
		//		undateRender = false;
	}

}
