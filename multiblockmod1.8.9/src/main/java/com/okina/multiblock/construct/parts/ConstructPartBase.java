package com.okina.multiblock.construct.parts;

import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ConstructPartBase {

	public static final String[] ColorCode = { "ffffff", "ffffff", "ffff00", "00ffff", "00ff00" };

	public int xCoord;
	public int yCoord;
	public int zCoord;

	public int grade;
	/**
	 * 0 : input
	 * 1 : output
	 * 2 : disabled
	 */
	public int flagIO[] = new int[6];
	private boolean eventScheduled = false;
	protected MultiBlockCoreTileEntity coreTile;
	public boolean isStopped = false;

	public ConstructPartBase() {
		for (int i = 0; i < 6; i++){
			flagIO[i] = 2;
		}
	}

	public boolean onRightClicked(World world, EntityPlayer player, EnumFacing side) {
		return false;
	}

	/**server only*/
	public void updatePart() {
		if(eventScheduled){
			for (EnumFacing side : EnumFacing.VALUES){
				if(coreTile.getPart(xCoord + side.getFrontOffsetX(), yCoord + side.getFrontOffsetY(), zCoord + side.getFrontOffsetZ()) instanceof EventCatcherPart){
					((EventCatcherPart) coreTile.getPart(xCoord + side.getFrontOffsetX(), yCoord + side.getFrontOffsetY(), zCoord + side.getFrontOffsetZ())).onEventReceived(side.getOpposite());
				}
			}
			eventScheduled = false;
		}
	}

	/**client only*/
	public void onRandomDisplayTick() {

	}

	protected void dispatchEventOnNextTick() {
		eventScheduled = true;
	}

	public boolean isOpenGuiOnClicked() {
		return false;
	}

	public Object getGuiElement(EntityPlayer player, boolean serverSide) {
		return null;
	}

	public NBTTagCompound getContentUpdateTag() {
		return null;
	}

	public void processCommand(PacketType type, NBTTagCompound tag) {

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void setCoreTile(MultiBlockCoreTileEntity tile) {
		coreTile = tile;
	}

	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		for (int i = 0; i < 6; i++){
			NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
			flagIO[i] = sideTag.getByte("io");
		}
		grade = tag.getInteger("grade");
		eventScheduled = tag.getBoolean("event");
	}

	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		NBTTagCompound[] side = new NBTTagCompound[6];
		for (int i = 0; i < 6; i++){
			side[i] = new NBTTagCompound();
			side[i].setByte("io", (byte) flagIO[i]);
			tag.setTag("side" + i, side[i]);
		}
		tag.setInteger("grade", grade);
		tag.setBoolean("event", eventScheduled);
	}

	public abstract String getNameForNBT();

	/**sintax : color + partname*/
	public String getNameForHUD() {
		return getNameForNBT().toUpperCase() + "," + ColorCode[grade];
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected final float Amplitude = 0.1f;
	private double rand1 = Math.random() * Math.PI;
	private float rand2 = (float) (Math.random() * 30f) - 15f;
	private float rand3 = (float) (Math.random() * 30f) - 15f;

	@SideOnly(Side.CLIENT)
	public void renderPart(double totalTicks) {
		//		if(RenderBlocks.getInstance().blockAccess == null) RenderBlocks.getInstance().blockAccess = coreTile.getWorldObj();
		//		float yOffset = (float) Math.sin(this.rand1 + (totalTicks / 40d) % (2d * Math.PI)) * Amplitude - 0.25f;
		//		GL11.glTranslatef(0, yOffset, 0);
		//		GL11.glRotatef(rand2, 0, 1, 0);
		//		GL11.glRotatef(rand3, 1, 0, 0);
		//		GL11.glScalef(2, 2, 2);
		//		EntityItem entityitem = null;
		//		entityitem = new EntityItem(coreTile.getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(getRenderBlock(), 1, getRenderMeta()));
		//		entityitem.hoverStart = 0.0F;
		//		RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
		//		////RenderingHelper.renderInvCuboid(RenderBlocks.getInstance(), getRenderBlock(), 2f / 16f, 2f / 16f, 2f / 16f, 14f / 16f, 14f / 16f, 14f / 16f, getRenderMeta());
		//		//RenderBlocks.getInstance().renderBlockByRenderType(getRenderBlock(), 0, 0, 0);
		//		GL11.glScalef(0.5f, 0.5f, 0.5f);
		//		GL11.glRotatef(-rand3, 1, 0, 0);
		//		GL11.glRotatef(-rand2, 0, 1, 0);
		//		GL11.glTranslatef(0, -yOffset, 0);
	}

	@SideOnly(Side.CLIENT)
	protected abstract Block getRenderBlock();

	@SideOnly(Side.CLIENT)
	protected int getRenderMeta() {
		return 0;
	}

}
