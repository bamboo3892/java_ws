package com.okina.multiblock.construct.tileentity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.construct.block.ConstructVirtualGlower;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.register.VirtualGrowerRecipeRegister;
import com.okina.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import cofh.api.energy.EnergyStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;

public class ConstructVirtualGrowerTileEntity extends ConstructBaseTileEntity implements ILinkConnectionUser, ISignalReceiver {

	public static final String nameForNBT = "virtualGlower";
	public static final int[] maxCapasity = { 400, 1000, 4000, 10000, 40000 };

	public ConstructContainerTileEntity container = null;
	public ConnectionEntry<ConstructEnergyProviderTileEntity> provider = null;
	private boolean needCheckProvider;
	private EnergyStorage energyStorage;

	public ConstructVirtualGrowerTileEntity() {
		this(0);
	}

	public ConstructVirtualGrowerTileEntity(int grade) {
		super(grade);
		energyStorage = new EnergyStorage(maxCapasity[grade]);
	}

	@Override
	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}
	@Override
	public boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}
	@Override
	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(worldObj.getBlockState(pos).getBlock() instanceof ConstructVirtualGlower){
			if(player.isSneaking()){
				// do nothing
			}else{
				flagIO[side.getIndex()] = flagIO[side.getIndex()] == 0 ? 2 : 0;
				if(worldObj.getTileEntity(pos.add(side.getDirectionVec())) instanceof BlockPipeTileEntity){
					((BlockPipeTileEntity) worldObj.getTileEntity(pos.add(side.getDirectionVec()))).checkConnection();
				}
				TestCore.proxy.markForTileUpdate(pos, PacketType.FLAG_IO);
				if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(flagIO[side.getIndex()] == 0 ? "input" : flagIO[side.getIndex()] == 1 ? "output" : "disabled"));
			}
		}
		return true;
	}

	@Override
	public void update() {
		super.update();
		if(needCheckProvider){
			if(provider != null && worldObj.getTileEntity(provider.getPosition()) instanceof ConstructEnergyProviderTileEntity){
				ConstructEnergyProviderTileEntity tile = (ConstructEnergyProviderTileEntity) worldObj.getTileEntity(provider.getPosition());
				provider = new ConnectionEntry<ConstructEnergyProviderTileEntity>(tile);
			}
			needCheckProvider = false;
		}
		if(provider != null && provider.getTile() != null){
			int empty = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
			if(empty > 0){
				int receive = provider.getTile().sendEnergy(this, empty);
				energyStorage.receiveEnergy(receive, false);
			}
		}
	}

	public boolean readyToGlow() {
		if(worldObj.isRemote) return true;
		if(container != null){
			VirtualGrowerRecipe recipe = VirtualGrowerRecipeRegister.instance.findRecipe(container.items[0]);
			if(recipe != null){
				return energyStorage.getEnergyStored() >= recipe.energy;
			}
		}
		return false;
	}

	public void doGrow() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(container != null){
			VirtualGrowerRecipe recipe = VirtualGrowerRecipeRegister.instance.findRecipe(container.items[0]);
			if(recipe != null){
				energyStorage.extractEnergy(recipe.energy, false);
			}
		}
		dispatchEventOnNextTick();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(container != null && readyToGlow()){
			container.startGrow();
		}
	}

	@Override
	public boolean canStartAt(EnumFacing side) {
		return true;
	}

	@Override
	public boolean tryConnect(ConstructBaseTileEntity tile, EnumFacing clickedSide, EnumFacing linkUserSide) {
		if(tile instanceof ConstructEnergyProviderTileEntity){
			provider = new ConnectionEntry<ConstructEnergyProviderTileEntity>((ConstructEnergyProviderTileEntity) tile);
			return true;
		}
		return false;
	}

	@Override
	public void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop) {
		super.renderHUD(mc, renderTicks, mop);
		if(provider != null){
			String message = "Link to Energy Provider Construct";
			ScaledResolution sr = new ScaledResolution(mc);
			Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);
			int length = mc.fontRendererObj.getStringWidth(message);
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef(center.getX(), center.getY(), 0);
			GL11.glTranslatef(-length / 2, 30, 0);
			mc.fontRendererObj.drawString(message, 0, 0, 0x00ff00, true);
			GL11.glPopMatrix();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		energyStorage.writeToNBT(tag);
		if(provider != null && solid.isInclude(provider.getPosition())){
			NBTTagCompound providerTag = new NBTTagCompound();
			providerTag.setInteger("x", provider.x - solid.minX);
			providerTag.setInteger("y", provider.y - solid.minY);
			providerTag.setInteger("z", provider.z - solid.minZ);
			providerTag.setInteger("side", provider.side);
			tag.setTag("provider", providerTag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyStorage = new EnergyStorage(maxCapasity[grade]);
		energyStorage.readFromNBT(tag);
		NBTTagCompound providerTag = tag.getCompoundTag("provider");
		provider = ConnectionEntry.createFromNBT(providerTag);
		needCheckProvider = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		energyStorage.writeToNBT(tag);
		if(provider != null){
			NBTTagCompound providerTag = new NBTTagCompound();
			provider.writeToNBT(providerTag);
			tag.setTag("provider", providerTag);
		}
	}

}
