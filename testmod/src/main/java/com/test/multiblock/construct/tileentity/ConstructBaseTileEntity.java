package com.test.multiblock.construct.tileentity;

import java.util.Random;

import com.test.main.TestCore;
import com.test.multiblock.construct.block.ConstructFunctionalBase;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.tileentity.ISimpleTilePacketUser;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class ConstructBaseTileEntity extends TileEntity implements ISimpleTilePacketUser {

	/**0 : input 1 : output 2 : disabled*/
	public int[] flagIO = new int[6];
	public int grade;

	/**server only*/
	private boolean eventScheduled = false;

	/**use for only rendering*/
	public boolean[] isNeighberBaseBlock = new boolean[6];
	public int restRenderTicks = 0;
	public int renderSide = -1;

	public ConstructBaseTileEntity(int grade) {
		this.grade = grade;
		for (int i = 0; i < 6; i++){
			flagIO[i] = 2;
		}
	}

	@Override
	public void updateEntity() {
		if(!worldObj.isRemote){
			if(eventScheduled){
				for (int side = 0; side < 6; side++){
					ForgeDirection dir = ForgeDirection.getOrientation(side);
					if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof ConstructEventCatcherTileEntity){
						((ConstructEventCatcherTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).onEventReceived(dir.getOpposite().ordinal());
					}
				}
				eventScheduled = false;
			}
		}else{
			this.restRenderTicks = this.restRenderTicks != 0 ? this.restRenderTicks - 1 : 0;
		}
	}

	protected void dispatchEventOnNextTick() {
		if(!worldObj.isRemote) eventScheduled = true;
	}

	/**effective only on client*/
	public void updateIsNeighberBaseBlock() {
		boolean b = false;
		boolean flag = false;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			b = worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof ConstructFunctionalBase;
			if(isNeighberBaseBlock[dir.ordinal()] != b) flag = true;
			isNeighberBaseBlock[dir.ordinal()] = b;
		}
		if(flag) TestCore.proxy.markForTileUpdate(getPosition(), PacketType.RENDER);
	}

	/**drop containeditem or custom NBT item*/
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

	public abstract boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ);

	public abstract boolean onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ);

	public abstract boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ);

	public void onLeftClicked(EntityPlayer player, int side, double hitX, double hitY, double hitZ) {
		this.restRenderTicks = 50;
		this.renderSide = -1;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.FLAG_IO){
			String str = "";
			for (int i = 0; i < 6; i++){
				str += flagIO[i];
			}
			return new SimpleTilePacket(this, PacketType.FLAG_IO, str);
		}else if(type == PacketType.RENDER){
			return new SimpleTilePacket(this, PacketType.RENDER, 0);
		}
		return null;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.FLAG_IO && value instanceof String){//should client
			String str = (String) value;
			if(str.length() == 6){
				for (int i = 0; i < 6; i++){
					flagIO[i] = Character.getNumericValue(str.charAt(i));
				}
			}
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}else if(type == PacketType.RENDER){
			this.updateIsNeighberBaseBlock();
		}
	}
	@Override
	public final Position getPosition() {
		return new Position(xCoord, yCoord, zCoord);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**contents items update*/
	@Override
	public void markDirty() {
		super.markDirty();
		TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONTENT);
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

	public abstract String getNameForNBT();

	/**
	 * solid is not relative
	 * write relative coordinate
	 */
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		NBTTagCompound[] side = new NBTTagCompound[6];
		for (int i = 0; i < 6; i++){
			side[i] = new NBTTagCompound();
			side[i].setByte("io", (byte) flagIO[i]);
			tag.setTag("side" + i, side[i]);
		}
		tag.setInteger("grade", grade);
		tag.setBoolean("event", eventScheduled);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		for (int i = 0; i < 6; i++){
			NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
			flagIO[i] = sideTag.getByte("io");
		}
		this.grade = tag.getInteger("grade");
		eventScheduled = tag.getBoolean("event");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound[] side = new NBTTagCompound[6];
		for (int i = 0; i < 6; i++){
			side[i] = new NBTTagCompound();
			side[i].setByte("io", (byte) flagIO[i]);
			tag.setTag("side" + i, side[i]);
		}
		tag.setInteger("grade", grade);
		tag.setBoolean("event", eventScheduled);
	}

}
