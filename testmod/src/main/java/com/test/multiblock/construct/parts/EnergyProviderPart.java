package com.test.multiblock.construct.parts;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructEnergyProviderTileEntity;
import com.test.utils.RectangularSolid;

import cofh.api.energy.EnergyStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

public class EnergyProviderPart extends ConstructPartBase {

	public static final int[] storage = { 400000, 2000000, 20000000, 80000000, 100000000 };
	public static final int[] transfer = { 400000, 400000, 400000, 400000, 400000 };

	private EnergyStorage energyStorage;

	public EnergyProviderPart() {
		energyStorage = new EnergyStorage(storage[grade], transfer[grade]);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.readFromNBT(tag, solid);
		energyStorage = new EnergyStorage(storage[grade], transfer[grade]);
		energyStorage.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.writeToNBT(tag, solid);
		energyStorage.writeToNBT(tag);
	}

	@Override
	public String getNameForNBT() {
		return ConstructEnergyProviderTileEntity.nameForNBT;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructEnergyProvider[grade];
	}

}
