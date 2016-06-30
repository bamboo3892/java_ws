package com.okina.multiblock;

import java.util.Random;

import com.okina.item.itemBlock.ItemMultiBlock;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.block.ConstructFunctionalBase;
import com.okina.register.ConstructPartRegistry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLLog;

public class DisassemblyTableTileEntity extends TileEntity implements IInventory {

	private ItemStack multiBlock;

	public DisassemblyTableTileEntity() {

	}

	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote) return multiBlock != null && (!isItemValidForSlot(0, player.getHeldItem()));
		if(multiBlock != null){
			ItemStack content = getStackInSlot(0).copy();
			content.stackSize = 1;
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + 0.5 + side.getFrontOffsetX() * 0.5, pos.getY() + 0.5 + side.getFrontOffsetY() * 0.5, pos.getZ() + 0.5 + side.getFrontOffsetZ() * 0.5, content));
			decrStackSize(0, 1);
			markDirty();
			return true;
		}else{
			if(isItemValidForSlot(0, player.getHeldItem())){
				ItemStack set = player.getHeldItem().copy();
				set.stackSize = 1;
				setInventorySlotContents(0, set);
				player.getHeldItem().stackSize -= 1;
				return true;
			}else{
				return false;
			}
		}
	}

	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(multiBlock != null){
			NBTTagCompound tag = multiBlock.getTagCompound();
			if(tag != null){
				//content
				NBTTagList blockTagList = tag.getTagList("blockList", Constants.NBT.TAG_COMPOUND);
				for (int tagCounter = 0; tagCounter < blockTagList.tagCount(); ++tagCounter){
					NBTTagCompound blockTagCompound = blockTagList.getCompoundTagAt(tagCounter);
					int grade = blockTagCompound.getInteger("grade");
					if(grade >= 0 && grade < 5){
						ConstructFunctionalBase block = ConstructPartRegistry.getBlockFromNBTName(blockTagCompound.getString("name"), grade);
						if(block != null){
							if(worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + side.getFrontOffsetX() + 0.5, pos.getY() + side.getFrontOffsetY() + 0.5, pos.getZ() + side.getFrontOffsetZ() + 0.5, new ItemStack(block, 1)))) continue;
						}
					}
					FMLLog.severe("Could not make block instance from nbt", new Object[] {});
				}

				NBTTagList interfaceTagList = tag.getTagList("interface", Constants.NBT.TAG_COMPOUND);
				for (int tagCounter = 0; tagCounter < interfaceTagList.tagCount(); ++tagCounter){
					NBTTagCompound interfaceTag = interfaceTagList.getCompoundTagAt(tagCounter);
					int grade = interfaceTag.getInteger("grade");
					if(grade >= 0 && grade < 5){
						if(worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + side.getFrontOffsetX() + 0.5, pos.getY() + side.getFrontOffsetY() + 0.5, pos.getZ() + side.getFrontOffsetZ() + 0.5, new ItemStack(TestCore.constructInterface[grade], 1)))) continue;
					}else{
						FMLLog.severe("Could not make block instance from nbt", new Object[] {});
					}
				}
			}
			multiBlock = null;
			markDirty();
			return true;
		}
		return false;
	}

	public void onTileRemoved() {
		if(this instanceof IInventory){
			IInventory tile = this;
			Random rand = worldObj.rand;
			for (int i1 = 0; i1 < tile.getSizeInventory(); ++i1){
				ItemStack itemstack = tile.getStackInSlot(i1);

				if(itemstack != null){
					float f = rand.nextFloat() * 0.8F + 0.1F;
					float f1 = rand.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem;

					for (float f2 = rand.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; worldObj.spawnEntityInWorld(entityitem)){
						int j1 = rand.nextInt(21) + 10;

						if(j1 > itemstack.stackSize){
							j1 = itemstack.stackSize;
						}

						itemstack.stackSize -= j1;
						entityitem = new EntityItem(worldObj, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (float) rand.nextGaussian() * f3;
						entityitem.motionY = (float) rand.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float) rand.nextGaussian() * f3;

						if(itemstack.hasTagCompound()){
							entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
						}
					}
				}
			}
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void markDirty() {
		super.markDirty();
		worldObj.markBlockForUpdate(pos);
	}

	@Override
	public final Packet getDescriptionPacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		writeToNBT(nbtTagCompound);
		return new S35PacketUpdateTileEntity(pos, 1, nbtTagCompound);
	}

	@Override
	public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbtTagCompound = pkt.getNbtCompound();
		readFromNBT(nbtTagCompound);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}
	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		return slotIndex == 0 ? multiBlock : null;
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if(slotIndex == 0 && multiBlock != null){
			if(multiBlock.stackSize <= amount){
				ItemStack tmpItemStack = multiBlock.copy();
				multiBlock = null;
				markDirty();
				return tmpItemStack;
			}
			ItemStack splittedItemStack = multiBlock.splitStack(amount);
			if(multiBlock.stackSize == 0){
				multiBlock = null;
			}
			markDirty();
			return splittedItemStack;
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		if(slotIndex == 0){
			multiBlock = itemStack;
			if(itemStack != null && itemStack.stackSize > getInventoryStackLimit()){
				itemStack.stackSize = getInventoryStackLimit();
			}
		}
	}
	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(getStackInSlot(index) != null){
			ItemStack itemstack = getStackInSlot(index);
			setInventorySlotContents(index, null);
			return itemstack;
		}else{
			return null;
		}
	}
	@Override
	public void clear() {
		for (int index = 0; index < getSizeInventory(); index++){
			setInventorySlotContents(index, null);
		}
	}
	@Override
	public String getName() {
		return "disassemblytable";
	}
	@Override
	public boolean hasCustomName() {
		return false;
	}
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
	}
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(worldObj.getTileEntity(pos) != this){
			return false;
		}
		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 50000.0D;
	}
	@Override
	public void openInventory(EntityPlayer player) {}
	@Override
	public void closeInventory(EntityPlayer player) {}
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return slotIndex == 0 && itemStack != null && itemStack.getItem() instanceof ItemMultiBlock;
	}
	@Override
	public int getField(int id) {
		return 0;
	}
	@Override
	public void setField(int id, int value) {}
	@Override
	public int getFieldCount() {
		return 0;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound itemTag = tag.getCompoundTag("item");
		if(itemTag != null){
			multiBlock = ItemStack.loadItemStackFromNBT(itemTag);
		}else{
			multiBlock = null;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(multiBlock != null){
			NBTTagCompound itemTag = new NBTTagCompound();
			multiBlock.writeToNBT(itemTag);
			tag.setTag("item", itemTag);
		}
	}

}
