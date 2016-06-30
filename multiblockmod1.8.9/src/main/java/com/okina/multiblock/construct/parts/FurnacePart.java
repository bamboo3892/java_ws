package com.okina.multiblock.construct.parts;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ConstructFurnaceTileEntity;
import com.okina.multiblock.construct.tileentity.ISignalReceiver;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.RectangularSolid;

import cofh.api.energy.EnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FurnacePart extends ConstructPartBase implements ISignalReceiver {

	public static final int smeltEnergy = 2000;

	public ContainerPart container = null;
	public ConnectionEntry<EnergyProviderPart> provider = null;
	private boolean needCheckProvider;
	private EnergyStorage energyStorage = new EnergyStorage(smeltEnergy * 2);

	public FurnacePart() {

	}

	@Override
	public void updatePart() {
		super.updatePart();
		if(needCheckProvider){
			if(provider != null && coreTile.getPart(provider.x, provider.y, provider.z) instanceof EnergyProviderPart){
				EnergyProviderPart tile = (EnergyProviderPart) coreTile.getPart(provider.x, provider.y, provider.z);
				provider = new ConnectionEntry<EnergyProviderPart>(tile, provider.x, provider.y, provider.z);
			}
			needCheckProvider = false;
		}
		int empty = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
		if(empty > 0){
			int receive = coreTile.sendEnergy(xCoord, yCoord, zCoord, empty);
			energyStorage.receiveEnergy(receive, false);
		}
	}

	public boolean readyToFurnace() {
		if(container != null){
			return energyStorage.getEnergyStored() >= smeltEnergy;
		}
		return false;
	}

	public void doSmelt() {
		if(container != null){
			energyStorage.extractEnergy(smeltEnergy, false);
		}
		dispatchEventOnNextTick();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(container != null && readyToFurnace()){
			container.startFurnace();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.readFromNBT(tag, solid);
		energyStorage = new EnergyStorage(smeltEnergy * 2);
		energyStorage.readFromNBT(tag);
		NBTTagCompound providerTag = tag.getCompoundTag("provider");
		provider = ConnectionEntry.createFromNBT(providerTag);
		needCheckProvider = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.writeToNBT(tag, solid);
		energyStorage.writeToNBT(tag);
		if(provider != null){
			NBTTagCompound providerTag = new NBTTagCompound();
			provider.writeToNBT(providerTag);
			tag.setTag("provider", providerTag);
		}
	}

	@Override
	public String getNameForNBT() {
		return ConstructFurnaceTileEntity.nameForNBT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructFurnace[grade];
	}

}
