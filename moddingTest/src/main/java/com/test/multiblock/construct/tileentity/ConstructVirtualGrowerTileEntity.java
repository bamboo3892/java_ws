package com.test.multiblock.construct.tileentity;

import com.test.main.TestCore;
import com.test.multiblock.BlockPipeTileEntity;
import com.test.multiblock.construct.block.ConstructVirtualGlower;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.register.VirtualGrowerRecipeRegister;
import com.test.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;
import com.test.utils.ConnectionEntry;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

public class ConstructVirtualGrowerTileEntity extends ConstructBaseTileEntity implements ILinkConnectionUser, ISignalReceiver {

	public static final String nameForNBT = "virtualGlower";
	public static final int[] maxCapasity = {400, 1000, 4000, 10000, 40000};

	public ConstructContainerTileEntity container = null;
	public ConnectionEntry<ConstructEnergyProviderTileEntity> provider = null;
	private int providerX;
	private int providerY;
	private int providerZ;
	private boolean needCheckProvider;
	private EnergyStorage energyStorage;

	public ConstructVirtualGrowerTileEntity(){
		this(0);
	}

	public ConstructVirtualGrowerTileEntity(int grade) {
		super(grade);
		energyStorage = new EnergyStorage(maxCapasity[grade]);
	}

	@Override
	public void onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ){}
	@Override
	public void onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ){}
	@Override
	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(worldObj.getBlock(xCoord, yCoord, zCoord) instanceof ConstructVirtualGlower){
			if(player.isSneaking()){
				//do nothing
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
			if(worldObj.getTileEntity(providerX, providerY, providerZ) instanceof ConstructEnergyProviderTileEntity){
				ConstructEnergyProviderTileEntity tile = (ConstructEnergyProviderTileEntity) worldObj.getTileEntity(providerX, providerY, providerZ);
				provider = new ConnectionEntry<ConstructEnergyProviderTileEntity>(tile);
			}
			needCheckProvider = false;
		}
		if(provider != null){
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
	public boolean tryConnect(ConstructBaseTileEntity tile) {
		if(tile instanceof ConstructEnergyProviderTileEntity){
			this.provider = new ConnectionEntry<ConstructEnergyProviderTileEntity>((ConstructEnergyProviderTileEntity)tile);
			return true;
		}
		return false;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyStorage = new EnergyStorage(maxCapasity[grade]);
		energyStorage.readFromNBT(tag);
		int[] coord = tag.getIntArray("provider");
		if(coord != null && coord.length == 3){
			providerX = coord[0];
			providerY = coord[1];
			providerZ = coord[2];
		}
		needCheckProvider = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		energyStorage.writeToNBT(tag);
		if(provider != null){
			tag.setIntArray("provider", new int[] { provider.x, provider.y, provider.z });
		}
	}

}
