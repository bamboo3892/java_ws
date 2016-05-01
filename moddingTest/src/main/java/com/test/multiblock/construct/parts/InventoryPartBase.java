package com.test.multiblock.construct.parts;

import com.test.utils.RectangularSolid;

import net.minecraft.nbt.NBTTagCompound;

public abstract class InventoryPartBase extends ConstructPartBase {

	public InventoryPartBase() {
	}

	@Override
	public void readFromNBTTagCompound(NBTTagCompound tag, RectangularSolid solid) {
		this.position = solid.toCoord(tag.getInteger("index"));
		NBTTagCompound[] connection = new NBTTagCompound[6];
		for(int i=0;i<6;i++){
			connection[i] = tag.getCompoundTag("side"+i);
			this.flagIO[i] = connection[i].getByte("io");
			int index = connection[i].getInteger("index");
			int side = connection[i].getByte("side");
			this.connection[i] = solid.toCoord(index);
			this.side[i] = side;
		}
	}

	@Override
	public void writeToNBTTagCompound(NBTTagCompound tag, RectangularSolid solid){
		tag.setString("name", getNameForNBT());
		NBTTagCompound[] side = new NBTTagCompound[6];
		for (int i = 0; i < 6; i++) {
			side[i] = new NBTTagCompound();
			if(flagIO[i] != 2){
				side[i].setByte("io", (byte)flagIO[i]);
				side[i].setInteger("index", solid.toIndex(connection[i], false));
				side[i].setByte("side", (byte)this.side[i]);
			}else{
				side[i].setByte("io", (byte)flagIO[i]);
			}
			tag.setTag("side"+i, side[i]);
		}
	}

	@Override
	public abstract String getNameForNBT();

}
