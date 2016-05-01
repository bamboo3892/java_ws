package com.test.multiblock.construct.tileentity;

import com.test.main.TestCore;
import com.test.multiblock.BlockPipeTileEntity;
import com.test.multiblock.construct.block.ConstructFurnace;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.utils.ConnectionEntry;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

public class ConstructFurnaceTileEntity extends ConstructBaseTileEntity implements ILinkConnectionUser, ISignalReceiver {

	public static String nameForNBT = "furnace";
	public static final int smeltEnergy = 2000;

	public ConstructContainerTileEntity container = null;
	public ConnectionEntry<ConstructEnergyProviderTileEntity> provider = null;
	private boolean needCheckProvider;
	private EnergyStorage energyStorage;

	public ConstructFurnaceTileEntity() {
		this(0);
	}

	public ConstructFurnaceTileEntity(int grade) {
		super(grade);
		energyStorage = new EnergyStorage(smeltEnergy * 2);
	}

	@Override
	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}
	@Override
	public boolean onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}
	@Override
	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(worldObj.getBlock(xCoord, yCoord, zCoord) instanceof ConstructFurnace){
			if(player.isSneaking()){
				// do nothing
			}else{
				flagIO[side] = flagIO[side] == 0 ? 2 : 0;
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
					((BlockPipeTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
				}
				TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
				if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(flagIO[side] == 0 ? "input" : flagIO[side] == 1 ? "output" : "disabled"));
			}
		}
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(needCheckProvider){
			if(provider != null && worldObj.getTileEntity(provider.x, provider.y, provider.z) instanceof ConstructEnergyProviderTileEntity){
				ConstructEnergyProviderTileEntity tile = (ConstructEnergyProviderTileEntity) worldObj.getTileEntity(provider.x, provider.y, provider.z);
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

	public boolean readyToFurnace() {
		if(worldObj.isRemote) return true;
		if(container != null){
			return energyStorage.getEnergyStored() >= smeltEnergy;
		}
		return false;
	}

	public void doSmelt() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(container != null){
			energyStorage.extractEnergy(smeltEnergy, false);
		}
		dispatchEventOnNextTick();
	}

	public void spawnFurnacingParticle(int side) {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		worldObj.spawnParticle("flame", xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, dir.offsetX * 0.4, dir.offsetY * 0.4, dir.offsetZ * 0.4);
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(container != null && readyToFurnace()){
			container.startFurnace();
		}
	}

	@Override
	public boolean canStartAt(int side) {
		return true;
	}
	@Override
	public boolean tryConnect(ConstructBaseTileEntity tile, int clickedSide, int linkUserSide) {
		if(tile instanceof ConstructEnergyProviderTileEntity){
			this.provider = new ConnectionEntry<ConstructEnergyProviderTileEntity>((ConstructEnergyProviderTileEntity) tile);
			return true;
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
		energyStorage = new EnergyStorage(smeltEnergy * 2);
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