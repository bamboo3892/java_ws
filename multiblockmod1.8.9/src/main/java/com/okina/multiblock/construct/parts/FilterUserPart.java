package com.okina.multiblock.construct.parts;

import com.okina.inventory.AbstractFilter;
import com.okina.inventory.IFilterUser;
import com.okina.multiblock.construct.tileentity.ConstructFilterUserTileEntity;
import com.okina.utils.InventoryHelper;
import com.okina.utils.RectangularSolid;
import com.okina.utils.UtilMethods;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class FilterUserPart extends InventoryPartBase implements IFilterUser {

	private AbstractFilter[] filter = new AbstractFilter[6];

	public FilterUserPart() {

	}

	@Override
	protected boolean itemTransfer() {
		int[] randomOrder = UtilMethods.getRandomArray(new int[] { 0, 1, 2, 3, 4, 5 });
		int[] priority = new int[6];
		for (int side = 0; side < 6; side++){
			if(connection[side] != null && flagIO[side] == 1){
				if(filter[side] != null){
					priority[side] = filter[side].getPriority();
				}else{
					priority[side] = 0;
				}
			}else{
				priority[side] = 0;
			}
		}
		int[] order = new int[6];
		for (int i = 0; i < 6; i++){
			order[i] = -1;
		}
		for (int i = AbstractFilter.MAX_PRIORITY; i >= 0; i--){
			for (int side : randomOrder){
				if(priority[side] == i){
					int index = 0;
					while (order[index] != -1)
						index++;
					order[index] = side;
				}
			}
		}
		for (int index = 0; index < 6; index++){
			int side = order[index];
			if(connection[side] != null && connection[side].getTile() != null && flagIO[side] == 1){
				if(filter[side] == null){
					if(InventoryHelper.tryPushItemEX(this, connection[side].getTile(), EnumFacing.getFront(side), connection[side].getFront(), maxTransfer[grade])){
						return true;
					}
				}else{
					if(filter[side].tranferItem(this, connection[side].getTile(), EnumFacing.getFront(side), connection[side].getFront(), maxTransfer[grade])){
						return true;
					}
				}
			}
		}
		return false;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void updateFilter() {

	}
	@Override
	public boolean setFilter(EnumFacing side, AbstractFilter filter) {
		if(this.filter[side.getIndex()] != null) return false;
		int n = 0;
		for (int i = 0; i < 6; i++){
			if(this.filter[i] != null) n++;
		}
		if(n >= ConstructFilterUserTileEntity.filterCapability[grade]) return false;
		this.filter[side.getIndex()] = filter;
		return true;
	}
	@Override
	public AbstractFilter getFilter(EnumFacing side) {
		return filter[side.getIndex()];
	}
	@Override
	public ItemStack removeFilter(EnumFacing side) {
		if(filter[side.getIndex()] == null){
			return null;
		}else{
			ItemStack itemStack = filter[side.getIndex()].getFilterItem();
			filter[side.getIndex()] = null;
			return itemStack;
		}
	}
	@Override
	public World getWorldObject() {
		return coreTile.getWorld();
	}

	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if(super.canInsertItem(slotIndex, itemStack, side)){
			if(filter[side.getIndex()] != null){
				if(filter[side.getIndex()].canTransferItem(itemStack)){
					return true;
				}
				return false;
			}else{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if(super.canExtractItem(slotIndex, itemStack, side)){
			if(filter[side.getIndex()] != null){
				if(filter[side.getIndex()].canTransferItem(itemStack)){
					return true;
				}
				return false;
			}else{
				return true;
			}
		}
		return false;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.readFromNBT(nbtTagCompound, solid);
		NBTTagCompound sideTag;
		for (int side = 0; side < 6; side++){
			sideTag = nbtTagCompound.getCompoundTag("filter" + side);
			filter[side] = AbstractFilter.createFromNBT(this, EnumFacing.getFront(side), sideTag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound, RectangularSolid solid) {
		super.writeToNBT(nbtTagCompound, solid);
		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound sideTag = new NBTTagCompound();
			filter[side].writeToNBT(sideTag);
			nbtTagCompound.setTag("filter" + side, sideTag);
		}
	}

}
