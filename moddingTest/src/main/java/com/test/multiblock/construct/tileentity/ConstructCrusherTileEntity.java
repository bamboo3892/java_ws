package com.test.multiblock.construct.tileentity;

import java.util.Random;

import com.test.main.TestCore;
import com.test.multiblock.BlockPipeTileEntity;
import com.test.multiblock.construct.block.ConstructCrusher;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class ConstructCrusherTileEntity extends ConstructBaseTileEntity implements IConstructInventory, ISignalReceiver {

	public static String nameForNBT = "crusher";
	public static int[] maxRemain = { 2, 10, 20, 40, 100 };

	public ItemStack cactus;
	public int remain = maxRemain[grade];
	public ConstructContainerTileEntity container = null;

	public ConstructCrusherTileEntity() {
		this(0);
	}

	public ConstructCrusherTileEntity(int grade) {
		super(grade);
	}

	@Override
	public void onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote) return;
		if(this.cactus != null){
			ItemStack content = this.getStackInSlot(0).copy();
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, content));
			this.decrStackSize(0, 1);
			this.markDirty();
		}else{
			if(this.isItemValidForSlot(0, player.getHeldItem())){
				ItemStack set = player.getHeldItem().copy();
				set.stackSize = 1;
				this.setInventorySlotContents(0, set);
				player.getHeldItem().stackSize -= 1;
				this.markDirty();
			}
		}
	}
	@Override
	public void onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {}
	@Override
	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(worldObj.getBlock(xCoord, yCoord, zCoord) instanceof ConstructCrusher){
			if(player.isSneaking()){
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, side, 3);
			}else{
				flagIO[side] = flagIO[side] == 0 ? 2 : 0;
				TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
					((BlockPipeTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
				}
				if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(flagIO[side] == 0 ? "input" : flagIO[side] == 1 ? "output" : "disabled"));
			}
		}
		return true;
	}

	public boolean readyToCrush() {
		return cactus != null && remain > 0;
	}
	public void doCrash() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(remain <= 1){
			remain = maxRemain[grade];
			cactus = null;
			this.markDirty();
		}else{
			remain -= 1;
		}
		dispatchEventOnNextTick();
	}

	public void spawnCrushingParticle() {
		Random random = worldObj.rand;
		double d3 = 0.1D;
		int l1 = (int) (150.0D * d3);
		for (int i2 = 0; i2 < l1; ++i2){
			float f3 = MathHelper.randomFloatClamp(random, 0.0F, ((float) Math.PI * 2F));
			double d5 = (double) MathHelper.randomFloatClamp(random, 0.75F, 1.0F);
			double d6 = 0.20000000298023224D + d3 / 100.0D;
			double d7 = (double) (MathHelper.cos(f3) * 0.2F) * d5 * d5 * (d3 + 0.2D);
			double d8 = (double) (MathHelper.sin(f3) * 0.2F) * d5 * d5 * (d3 + 0.2D);
			String str = "blockcrack_" + Block.getIdFromBlock(Blocks.cactus) + "_0";
			worldObj.spawnParticle(str, (double) ((float) xCoord + 0.5F), (double) ((float) yCoord + 0.2F), (double) ((float) zCoord + 0.5F), d7, d6, d8);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(container != null && readyToCrush()){
			container.startCrush();
		}
	}

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.NBT_CONETENT){
			NBTTagCompound tag = new NBTTagCompound();
			if(this.cactus != null){
				this.cactus.writeToNBT(tag);
			}
			return new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.NBT_CONETENT, tag);
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value){
		super.processCommand(type, value);
		if(type == PacketType.NBT_CONETENT && value instanceof NBTTagCompound){
			this.cactus = ItemStack.loadItemStackFromNBT((NBTTagCompound)value);
		}
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
	public ItemStack decrStackSize(int slot, int smount) {
		if(slot == 0){
			ItemStack removed = cactus;
			cactus = null;
			this.markDirty();
			return removed;
		}
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
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
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {

	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.cactus = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("content"));
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(this.cactus != null){
			NBTTagCompound itemnbt = new NBTTagCompound();
			this.cactus.writeToNBT(itemnbt);
			tag.setTag("content", itemnbt);
		}
	}

}
