package com.okina.multiblock.construct.tileentity;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.okina.client.IHUDUser;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.block.ConstructFunctionalBase;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MovingObjectPosition;

public abstract class ConstructBaseTileEntity extends TileEntity implements ITickable, ISimpleTilePacketUser, IHUDUser {

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
	public void update() {
		if(!worldObj.isRemote){
			if(eventScheduled){
				for (int side = 0; side < 6; side++){
					EnumFacing dir = EnumFacing.getFront(side);
					if(worldObj.getTileEntity(pos.add(dir.getDirectionVec())) instanceof ConstructEventCatcherTileEntity){
						((ConstructEventCatcherTileEntity) worldObj.getTileEntity(pos.add(dir.getDirectionVec()))).onEventReceived(dir.getOpposite());
					}
				}
				eventScheduled = false;
			}
		}else{
			restRenderTicks = restRenderTicks != 0 ? restRenderTicks - 1 : 0;
		}
	}

	protected void dispatchEventOnNextTick() {
		if(!worldObj.isRemote) eventScheduled = true;
	}

	/**effective only on client*/
	public void updateIsNeighberBaseBlock() {
		boolean b = false;
		for (EnumFacing dir : EnumFacing.VALUES){
			b = worldObj.getBlockState(pos.add(dir.getDirectionVec())).getBlock() instanceof ConstructFunctionalBase;
			isNeighberBaseBlock[dir.ordinal()] = b;
		}
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
		//		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.RENDER);
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

	public abstract boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ);

	public abstract boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ);

	public abstract boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ);

	public void onLeftClicked(EntityPlayer player, MovingObjectPosition mop) {
		restRenderTicks = 50;
		renderSide = -1;
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
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}else if(type == PacketType.RENDER){
			updateIsNeighberBaseBlock();
		}
	}
	@Override
	public final BlockPos getPosition() {
		return getPos();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**contents items update*/
	@Override
	public void markDirty() {
		super.markDirty();
		TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONTENT);
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
		grade = tag.getInteger("grade");
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

	@Override
	public void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop) {
		String message = null;
		int color = 0;
		if(flagIO[mop.sideHit.getIndex()] == 0){
			message = "Input";
			color = 0x00BFFF;
		}else if(flagIO[mop.sideHit.getIndex()] == 1){
			message = "Output";
			color = 0xFF8C00;
		}
		if(message != null){
			ScaledResolution sr = new ScaledResolution(mc);
			Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);
			int length = mc.fontRendererObj.getStringWidth(message);
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(center.getX(), center.getY(), 0);
			GL11.glTranslatef(-length / 2, 20, 0);
			mc.fontRendererObj.drawString(message, 0, 0, color, true);
			GL11.glPopMatrix();
		}
	}
	@Override
	public boolean comparePastRenderObj(Object object, MovingObjectPosition past, MovingObjectPosition current) {
		return object == this;
	}

}
