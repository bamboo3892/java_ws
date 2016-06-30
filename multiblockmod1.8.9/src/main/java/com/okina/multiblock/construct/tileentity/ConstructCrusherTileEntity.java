package com.okina.multiblock.construct.tileentity;

import java.util.Random;

import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.construct.block.ConstructCrusher;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

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
	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			return !(cactus == null && !isItemValidForSlot(0, player.getHeldItem()));
		}else{
			if(cactus != null){
				ItemStack content = getStackInSlot(0).copy();
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, content));
				decrStackSize(0, 1);
				markDirty();
				return true;
			}else{
				if(isItemValidForSlot(0, player.getHeldItem())){
					ItemStack set = player.getHeldItem().copy();
					set.stackSize = 1;
					setInventorySlotContents(0, set);
					player.getHeldItem().stackSize -= 1;
					markDirty();
					return true;
				}else{
					return false;
				}
			}
		}
	}

	@Override
	public boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(worldObj.getBlockState(pos).getBlock() instanceof ConstructCrusher){
			if(player.isSneaking()){
				worldObj.setBlockState(pos, state.withProperty(ConstructCrusher.DIRECTION, Integer.valueOf(side.getIndex())));
			}else{
				flagIO[side.getIndex()] = flagIO[side.getIndex()] == 0 ? 2 : 0;
				TestCore.proxy.markForTileUpdate(pos, PacketType.FLAG_IO);
				if(worldObj.getTileEntity(pos.add(side.getDirectionVec())) instanceof BlockPipeTileEntity){
					((BlockPipeTileEntity) worldObj.getTileEntity(pos.add(side.getDirectionVec()))).checkConnection();
				}
				if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(flagIO[side.getIndex()] == 0 ? "input" : flagIO[side.getIndex()] == 1 ? "output" : "disabled"));
			}
		}
		return true;
	}

	//	@Override
	//	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
	//		if(worldObj.isRemote){
	//			return !(this.cactus == null && !this.isItemValidForSlot(0, player.getHeldItem()));
	//		}else{
	//			if(this.cactus != null){
	//				ItemStack content = this.getStackInSlot(0).copy();
	//				worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, content));
	//				this.decrStackSize(0, 1);
	//				this.markDirty();
	//				return true;
	//			}else{
	//				if(this.isItemValidForSlot(0, player.getHeldItem())){
	//					ItemStack set = player.getHeldItem().copy();
	//					set.stackSize = 1;
	//					this.setInventorySlotContents(0, set);
	//					player.getHeldItem().stackSize -= 1;
	//					this.markDirty();
	//					return true;
	//				}else{
	//					return false;
	//				}
	//			}
	//		}
	//	}
	//	@Override
	//	public boolean onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
	//		return false;
	//	}
	//	@Override
	//	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
	//		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
	//		if(worldObj.getBlock(xCoord, yCoord, zCoord) instanceof ConstructCrusher){
	//			if(player.isSneaking()){
	//				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, side, 3);
	//			}else{
	//				flagIO[side] = flagIO[side] == 0 ? 2 : 0;
	//				TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
	//				ForgeDirection dir = ForgeDirection.getOrientation(side);
	//				if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
	//					((BlockPipeTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
	//				}
	//				if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(flagIO[side] == 0 ? "input" : flagIO[side] == 1 ? "output" : "disabled"));
	//			}
	//		}
	//		return true;
	//	}

	public boolean readyToCrush() {
		return cactus != null && remain > 0;
	}
	public void doCrash() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(remain <= 1){
			remain = maxRemain[grade];
			cactus = null;
			markDirty();
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
			double d5 = MathHelper.randomFloatClamp(random, 0.75F, 1.0F);
			double d6 = 0.20000000298023224D + d3 / 100.0D;
			double d7 = MathHelper.cos(f3) * 0.2F * d5 * d5 * (d3 + 0.2D);
			double d8 = MathHelper.sin(f3) * 0.2F * d5 * d5 * (d3 + 0.2D);
			String str = "blockcrack_" + Block.getIdFromBlock(Blocks.cactus) + "_0";
			worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, (double) (pos.getX() + 0.5F), (double) (pos.getY() + 0.2F), (double) (pos.getZ() + 0.5F), d7, d6, d8, Block.getStateId(Blocks.cactus.getDefaultState()));
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
		if(type == PacketType.NBT_CONTENT){
			NBTTagCompound tag = new NBTTagCompound();
			if(cactus != null){
				cactus.writeToNBT(tag);
			}
			return new SimpleTilePacket(this, PacketType.NBT_CONTENT, tag);
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			cactus = ItemStack.loadItemStackFromNBT((NBTTagCompound) value);
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
			markDirty();
			return removed;
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		cactus = itemStack;
		markDirty();
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
		return "crusher";
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
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return false;
	}
	@Override
	public void openInventory(EntityPlayer player) {}
	@Override
	public void closeInventory(EntityPlayer player) {}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return itemStack == null ? false : Block.getBlockFromItem(itemStack.getItem()) == Blocks.cactus;
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		return flagIO[side.getIndex()] == 0;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return flagIO[side.getIndex()] == 1;
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0 };
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		if(cactus != null){
			NBTTagCompound itemnbt = new NBTTagCompound();
			cactus.writeToNBT(itemnbt);
			tag.setTag("content", itemnbt);
		}
		tag.setInteger("dir", getBlockMetadata());
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		cactus = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("content"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(cactus != null){
			NBTTagCompound itemnbt = new NBTTagCompound();
			cactus.writeToNBT(itemnbt);
			tag.setTag("content", itemnbt);
		}
	}



}
