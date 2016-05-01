package com.test.multiblock.construct.parts;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructVirtualGrowerTileEntity;
import com.test.multiblock.construct.tileentity.ISignalReceiver;
import com.test.register.VirtualGrowerRecipeRegister;
import com.test.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;
import com.test.utils.ConnectionEntry;
import com.test.utils.RectangularSolid;

import cofh.api.energy.EnergyStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

public class VirtualGlowerPart extends ConstructPartBase implements ISignalReceiver {

	public static final int[] maxCapasity = { 400, 1000, 4000, 10000, 40000 };

	public ContainerPart container = null;
	public ConnectionEntry<EnergyProviderPart> provider = null;
	private boolean needCheckProvider;
	private EnergyStorage energyStorage;

	public VirtualGlowerPart() {
		energyStorage = new EnergyStorage(maxCapasity[grade]);
	}

	@Override
	public void updatePart() {
		super.updatePart();
		if(needCheckProvider){
			if(provider != null && coreTile.getPart(provider.x, provider.y, provider.z) instanceof EnergyProviderPart){
				EnergyProviderPart part = (EnergyProviderPart) coreTile.getPart(provider.x, provider.y, provider.z);
				provider.setTile(part);
			}
			needCheckProvider = false;
		}
		int empty = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
		if(empty > 0){
			int receive = coreTile.sendEnergy(xCoord, yCoord, zCoord, empty);
			energyStorage.receiveEnergy(receive, false);
		}
	}

	public boolean readyToGlow() {
		if(container != null){
			VirtualGrowerRecipe recipe = VirtualGrowerRecipeRegister.instance.findRecipe(container.items[0]);
			if(recipe != null){
				return energyStorage.getEnergyStored() >= recipe.energy;
			}
		}
		return false;
	}

	public void doGrow() {
		if(container != null){
			VirtualGrowerRecipe recipe = VirtualGrowerRecipeRegister.instance.findRecipe(container.items[0]);
			if(recipe != null){
				energyStorage.extractEnergy(recipe.energy, false);
			}
		}
		dispatchEventOnNextTick();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(container != null && readyToGlow()){
			container.startGrow();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.readFromNBT(tag, solid);
		energyStorage = new EnergyStorage(maxCapasity[grade]);
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
		return ConstructVirtualGrowerTileEntity.nameForNBT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructVirtualGrower[grade];
	}

}
