package com.test.multiblock;

import java.util.Random;

import com.test.item.itemBlock.ItemMultiBlock;
import com.test.main.TestCore;
import com.test.multiblock.construct.block.ConstructFunctionalBase;
import com.test.register.ConstructPartRegistry;

import cpw.mods.fml.common.FMLLog;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class DisassemblyTableTileEntity extends TileEntity implements IInventory {

	private ItemStack multiBlock;

	public DisassemblyTableTileEntity() {

	}

	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote) return multiBlock != null && (!this.isItemValidForSlot(0, player.getHeldItem()));
		if(multiBlock != null){
			ItemStack content = this.getStackInSlot(0).copy();
			content.stackSize = 1;
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5 + dir.offsetX * 0.5, yCoord + 0.5 + dir.offsetY * 0.5, zCoord + 0.5 + dir.offsetZ * 0.5, content));
			this.decrStackSize(0, 1);
			this.markDirty();
			return true;
		}else{
			if(this.isItemValidForSlot(0, player.getHeldItem())){
				ItemStack set = player.getHeldItem().copy();
				set.stackSize = 1;
				this.setInventorySlotContents(0, set);
				player.getHeldItem().stackSize -= 1;
				return true;
			}else{
				return false;
			}
		}
	}

	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(multiBlock != null){
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			NBTTagCompound tag = multiBlock.getTagCompound();
			if(tag != null){
				//content
				NBTTagList blockTagList = tag.getTagList("blockList", Constants.NBT.TAG_COMPOUND);
				for (int tagCounter = 0; tagCounter < blockTagList.tagCount(); ++tagCounter){
					NBTTagCompound blockTagCompound = (NBTTagCompound) blockTagList.getCompoundTagAt(tagCounter);
					int grade = blockTagCompound.getInteger("grade");
					if(grade >= 0 && grade < 5){
						ConstructFunctionalBase block = ConstructPartRegistry.getBlockFromNBTName(blockTagCompound.getString("name"), grade);
						if(block != null){
							if(worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, new ItemStack(block, 1)))) continue;
						}
					}
					FMLLog.severe("Could not make block instance from nbt", new Object[] {});
				}

				NBTTagList interfaceTagList = tag.getTagList("interface", Constants.NBT.TAG_COMPOUND);
				for (int tagCounter = 0; tagCounter < interfaceTagList.tagCount(); ++tagCounter){
					NBTTagCompound interfaceTag = (NBTTagCompound) interfaceTagList.getCompoundTagAt(tagCounter);
					int grade = interfaceTag.getInteger("grade");
					if(grade >= 0 && grade < 5){
						if(worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, new ItemStack(TestCore.constructInterface[grade], 1)))) continue;
					}else{
						FMLLog.severe("Could not make block instance from nbt", new Object[] {});
					}
				}
			}
			this.multiBlock = null;
			this.markDirty();
			return true;
		}
		return false;
	}

	public void onTileRemoved() {
		if(this instanceof IInventory){
			IInventory tile = (IInventory) this;
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
						entityitem = new EntityItem(worldObj, (double) ((float) xCoord + f), (double) ((float) yCoord + f1), (double) ((float) zCoord + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (double) ((float) rand.nextGaussian() * f3);
						entityitem.motionY = (double) ((float) rand.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double) ((float) rand.nextGaussian() * f3);

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
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public final Packet getDescriptionPacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.writeToNBT(nbtTagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}

	@Override
	public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound nbtTagCompound = pkt.func_148857_g();
		this.readFromNBT(nbtTagCompound);
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
				this.markDirty();
				return tmpItemStack;
			}
			ItemStack splittedItemStack = multiBlock.splitStack(amount);
			if(multiBlock.stackSize == 0){
				multiBlock = null;
			}
			this.markDirty();
			return splittedItemStack;
		}
		return null;
	}
	@Override
	public final ItemStack getStackInSlotOnClosing(int slotIndex) {
		return slotIndex == 0 ? multiBlock : null;
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
	public String getInventoryName() {
		return null;
	}
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if(worldObj.getTileEntity(xCoord, yCoord, zCoord) != this){
			return false;
		}
		return entityplayer.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 50000.0D;
	}
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return slotIndex == 0 && itemStack != null && itemStack.getItem() instanceof ItemMultiBlock;
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
