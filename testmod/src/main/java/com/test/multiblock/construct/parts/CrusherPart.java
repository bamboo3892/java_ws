package com.test.multiblock.construct.parts;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructCrusherTileEntity;
import com.test.multiblock.construct.tileentity.ISignalReceiver;
import com.test.utils.RectangularSolid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CrusherPart extends ConstructPartBase implements ISidedInventory, ISignalReceiver {

	public static int[] maxRemain = { 2, 10, 20, 40, 100 };
	public ItemStack cactus;
	public int direction;
	public int remain = maxRemain[grade];
	public ContainerPart container = null;

	public CrusherPart() {

	}

	public boolean readyToCrush() {
		return cactus != null && remain > 0;
	}
	public void doCrash() {
		if(remain <= 1){
			remain = maxRemain[grade];
			cactus = null;
			this.markDirty();
		}else{
			remain -= 1;
		}
		dispatchEventOnNextTick();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(container != null && readyToCrush()){
			container.startCrush();
		}
	}

	@Override
	public void markDirty() {

	}
	@Override
	public int getSizeInventory() {
		return 1;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? cactus : null;
	}
	@Override
	public ItemStack decrStackSize(int slotIndex, int splitStackSize) {
		if(this.cactus != null){
			if(this.cactus.stackSize <= splitStackSize){
				ItemStack tmpItemStack = cactus;
				this.cactus = null;
				this.markDirty();
				return tmpItemStack;
			}
			ItemStack splittedItemStack = this.cactus.splitStack(splitStackSize);
			if(this.cactus.stackSize == 0){
				this.cactus = null;
			}
			this.markDirty();
			return splittedItemStack;
		}
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		cactus = itemStack;
		this.markDirty();
	}
	@Override
	public String getInventoryName() {
		return "container.crusher";
	}
	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return itemStack == null ? false : Block.getBlockFromItem(itemStack.getItem()) == Blocks.cactus;
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return flagIO[side] == 0;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return flagIO[side] == 1;
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0 };
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return ConstructCrusherTileEntity.nameForNBT;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.readFromNBT(tag, solid);
		this.cactus = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("content"));
		this.direction = tag.getInteger("dir");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.writeToNBT(tag, solid);
		if(this.cactus != null){
			NBTTagCompound itemnbt = new NBTTagCompound();
			this.cactus.writeToNBT(itemnbt);
			tag.setTag("content", itemnbt);
		}
		tag.setInteger("dir", direction);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructCrusher[grade];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	protected int getRenderMeta() {
		return this.direction;
	}

}
