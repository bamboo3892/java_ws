package com.okina.multiblock;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.okina.client.IHUDUser;
import com.okina.inventory.AbstractFilter;
import com.okina.inventory.FilterInventory;
import com.okina.inventory.IFilterUser;
import com.okina.main.GuiHandler.IGuiTile;
import com.okina.main.TestCore;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MultiBlockCasingTileEntity extends TileEntity implements ITickable, ISimpleTilePacketUser, ISidedInventory, IEnergyReceiver, IEnergyProvider, IFilterUser, IHUDUser, IGuiTile {

	/**0 : input 1 : output 2 : disabled*/
	public int[] flagIO = new int[6];
	public ConnectionEntry<MultiBlockCoreTileEntity> coreTile = null;
	private AbstractFilter[] filter = new AbstractFilter[6];

	private boolean needCheckCoreTile;

	public MultiBlockCasingTileEntity() {
		for (int i = 0; i < 6; i++){
			flagIO[i] = 2;
		}
	}

	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && (player.getHeldItem().getItem() == TestCore.filter || player.getHeldItem().getItem() == TestCore.craftingFilter)) return false;
		if(filter[side.getIndex()] != null){
			return filter[side.getIndex()].onRightClicked(worldObj, pos, side, player);
		}else if(isConnected()){
			return coreTile.getTile().onRightClicked(state, player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	public boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(isConnected()){
			return coreTile.getTile().onShiftRightClicked(state, player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(isConnected()){
			if(!player.isSneaking()){
				if(!(worldObj.getBlockState(pos.add(side.getDirectionVec())).getBlock() instanceof MultiBlockCasing) && getCoreTie().getInterfaceConnection(side) != null){
					flagIO[side.getIndex()] = flagIO[side.getIndex()] == 0 ? 1 : flagIO[side.getIndex()] == 1 ? 2 : 0;
					TestCore.proxy.markForTileUpdate(getPosition(), PacketType.FLAG_IO);
					return true;
				}
			}
			return coreTile.getTile().onRightClickedByWrench(state, player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	public void onLeftClicked(EntityPlayer player, MovingObjectPosition mop) {
		if(player.isSneaking() && filter[mop.sideHit.getIndex()] != null){
			ItemStack filter = removeFilter(mop.sideHit);
			if(filter != null && !worldObj.isRemote){
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + mop.sideHit.getFrontOffsetX() + 0.5, pos.getY() + mop.sideHit.getFrontOffsetY() + 0.5, pos.getZ() + mop.sideHit.getFrontOffsetZ() + 0.5, filter));
			}
		}
	}

	@Override
	public void update() {
		if(needCheckCoreTile){
			if(coreTile != null){
				if(worldObj.getTileEntity(coreTile.getPosition()) instanceof MultiBlockCoreTileEntity){
					MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) worldObj.getTileEntity(coreTile.getPosition());
					coreTile = new ConnectionEntry<MultiBlockCoreTileEntity>(tile);
				}
			}
			needCheckCoreTile = false;
		}
	}

	public void transferItemForSide(EnumFacing side) {
		if(worldObj.getTileEntity(pos.add(side.getDirectionVec())) instanceof IInventory){
			IInventory inv = (IInventory) worldObj.getTileEntity(pos.add(side.getDirectionVec()));
			if(inv != null && (flagIO[side.getIndex()] == 1 || flagIO[side.getIndex()] == 0)){
				if(inv instanceof MultiBlockCasingTileEntity && ((MultiBlockCasingTileEntity) inv).getCoreTie() == getCoreTie()) return;
				if(inv == getCoreTie()) return;
				if(filter[side.getIndex()] == null){
					if(flagIO[side.getIndex()] == 1){
						InventoryHelper.tryPushItemEX(this, inv, side, side.getOpposite(), coreTile.getTile().getMaxTransfer(side));
					}else if(flagIO[side.getIndex()] == 0){
						InventoryHelper.tryPushItemEX(inv, this, side.getOpposite(), side, coreTile.getTile().getMaxTransfer(side));
					}
				}else{
					if(flagIO[side.getIndex()] == 1){
						filter[side.getIndex()].tranferItem(this, inv, side, side.getOpposite(), coreTile.getTile().getMaxTransfer(side));
					}else if(flagIO[side.getIndex()] == 0){
						filter[side.getIndex()].tranferItem(inv, this, side.getOpposite(), side, coreTile.getTile().getMaxTransfer(side));
					}
				}
			}
		}
	}

	public boolean isConnected() {
		return !(coreTile == null || coreTile.getTile() == null);
	}
	public MultiBlockCoreTileEntity getCoreTie() {
		if(isConnected()){
			return coreTile.getTile();
		}
		return null;
	}
	public boolean connect(MultiBlockCoreTileEntity tile) {
		if(!isConnected()){
			coreTile = new ConnectionEntry(tile);
			TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONNECTION);
			return true;
		}
		return false;
	}
	public void disconnect() {
		coreTile = null;
		for (int i = 0; i < 6; i++){
			flagIO[i] = 2;
		}
		Random rand = worldObj.rand;
		for (int i = 0; i < 6; i++){
			if(filter[i] != null){
				ItemStack itemStack = filter[i].getFilterItem();
				double x = pos.getX() + rand.nextFloat() * 0.8F + 0.1F;
				double y = pos.getY() + rand.nextFloat() * 0.8F + 0.1F;
				double z = pos.getZ() + rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem = new EntityItem(worldObj, x, y, z, itemStack);
				entityitem.motionX = (float) rand.nextGaussian() * 0.05F;
				entityitem.motionY = (float) rand.nextGaussian() * 0.05F + 0.2F;
				entityitem.motionZ = (float) rand.nextGaussian() * 0.05F;
				if(worldObj.spawnEntityInWorld(entityitem)){
					removeFilter(EnumFacing.getFront(i));
				}
			}
		}
		TestCore.proxy.markForTileUpdate(pos, PacketType.FLAG_IO);
		TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONNECTION);
	}

	@Override
	public void updateFilter() {
		TestCore.proxy.markForTileUpdate(pos, PacketType.FILTER2);
	}
	@Override
	public boolean setFilter(EnumFacing side, AbstractFilter filter) {
		if(this.filter[side.getIndex()] == null){
			if(isConnected()){
				if(worldObj.isAirBlock(pos.add(side.getDirectionVec()))){
					this.filter[side.getIndex()] = filter;
					updateFilter();
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public AbstractFilter getFilter(EnumFacing side) {
		return filter[side.getIndex()];
	}

	@Override
	public ItemStack removeFilter(EnumFacing side) {
		if(filter[side.getIndex()] == null){
			return null;
		}else{
			ItemStack itemStack = filter[side.getIndex()].getFilterItem();
			filter[side.getIndex()] = null;
			updateFilter();
			return itemStack;
		}
	}
	@Override
	public World getWorldObject() {
		return worldObj;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**contents items update*/
	@Override
	public void markDirty() {
		super.markDirty();
		if(coreTile != null && coreTile.getTile() != null){
			TestCore.proxy.markForTileUpdate(coreTile.getTile().getPos(), PacketType.NBT_CONTENT);
		}
	}

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.FLAG_IO){
			String str = "";
			for (int i = 0; i < 6; i++){
				str += flagIO[i];
			}
			return new SimpleTilePacket(this, PacketType.FLAG_IO, str);
		}else if(type == PacketType.NBT_CONNECTION){
			NBTTagCompound tag = new NBTTagCompound();
			if(coreTile != null){
				coreTile.writeToNBT(tag);
			}
			return new SimpleTilePacket(this, PacketType.NBT_CONNECTION, tag);
		}else if(type == PacketType.FILTER2){
			NBTTagCompound tag = new NBTTagCompound();
			for (int side = 0; side < 6; side++){
				if(filter[side] == null) continue;
				NBTTagCompound filterTag = new NBTTagCompound();
				filter[side].writeToNBT(filterTag);
				tag.setTag("filter" + side, filterTag);
			}
			return new SimpleTilePacket(this, PacketType.FILTER2, tag);
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
		}else if(type == PacketType.NBT_CONNECTION && value instanceof NBTTagCompound){//should client
			NBTTagCompound tag = (NBTTagCompound) value;
			coreTile = ConnectionEntry.createFromNBT(tag);
			needCheckCoreTile = true;
		}else if(type == PacketType.FILTER && value instanceof String){
			String str = (String) value;
			int side = Character.getNumericValue(str.charAt(0));
			if(filter[side] instanceof FilterInventory){
				FilterInventory f = (FilterInventory) filter[side];
				int damage = Character.getNumericValue(str.charAt(1));
				int nbt = Character.getNumericValue(str.charAt(2));
				int oreDict = Character.getNumericValue(str.charAt(3));
				int ban = Character.getNumericValue(str.charAt(4));
				int priority = Character.getNumericValue(str.charAt(5));
				if(side >= 0 && side <= 5 && damage != -1 && nbt != -1 && oreDict != -1 && ban != -1 && priority != -1){
					f.useDamage = damage == 1;
					f.useNBT = nbt == 1;
					f.useOreDictionary = oreDict == 1;
					f.filterBan = ban == 1;
					f.priority = priority;
				}
			}
		}else if(type == PacketType.FILTER2 && value instanceof NBTTagCompound){
			for (int side = 0; side < 6; side++){
				NBTTagCompound sideTag = ((NBTTagCompound) value).getCompoundTag("filter" + side);
				filter[side] = AbstractFilter.createFromNBT(this, EnumFacing.getFront(side), sideTag);
			}
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}
	}
	@Override
	public final BlockPos getPosition() {
		return pos;
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
		if(isConnected()){
			return coreTile.getTile().getSizeInventory();
		}
		return 0;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		if(isConnected()){
			return coreTile.getTile().getStackInSlot(slot);
		}
		return null;
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if(isConnected()){
			return coreTile.getTile().decrStackSize(slot, amount);
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if(isConnected()){
			coreTile.getTile().setInventorySlotContents(slot, itemStack);
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
		if(isConnected()){
			return coreTile.getTile().getName();
		}
		return "no inventory";
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
		if(isConnected()){
			return coreTile.getTile().getInventoryStackLimit();
		}
		return 0;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(isConnected()){
			return coreTile.getTile().isUseableByPlayer(player);
		}
		return false;
	}
	@Override
	public void openInventory(EntityPlayer player) {
		if(isConnected()){
			coreTile.getTile().openInventory(player);
		}
	}
	@Override
	public void closeInventory(EntityPlayer player) {
		if(isConnected()){
			coreTile.getTile().closeInventory(player);
		}
	}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		if(isConnected()){
			return coreTile.getTile().isItemValidForSlot(slot, itemStack);
		}
		return false;
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(isConnected()){
			return coreTile.getTile().getSlotsForFace(side);
		}
		return new int[0];
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack itemStack, EnumFacing side) {
		if(isConnected() && flagIO[side.getIndex()] == 0){
			if(filter[side.getIndex()] != null){
				return filter[side.getIndex()].canTransferItem(itemStack) && coreTile.getTile().canInsertItem(slot, itemStack, side);
			}
			return coreTile.getTile().canInsertItem(slot, itemStack, side);
		}
		return false;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, EnumFacing side) {
		if(isConnected() && flagIO[side.getIndex()] == 1){
			if(filter[side.getIndex()] != null){
				return filter[side.getIndex()].canTransferItem(itemStack) && coreTile.getTile().canInsertItem(slot, itemStack, side);
			}
			return coreTile.getTile().canExtractItem(slot, itemStack, side);
		}
		return false;
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

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		if(isConnected()){
			return coreTile.getTile().canConnectEnergy(from);
		}
		return false;
	}
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if(isConnected()){
			return coreTile.getTile().receiveEnergy(from, maxReceive, simulate);
		}
		return 0;
	}
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(isConnected()){
			return coreTile.getTile().extractEnergy(from, maxExtract, simulate);
		}
		return 0;
	}
	@Override
	public int getEnergyStored(EnumFacing from) {
		if(isConnected()){
			return coreTile.getTile().getEnergyStored(from);
		}
		return 0;
	}
	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		if(isConnected()){
			return coreTile.getTile().getMaxEnergyStored(from);
		}
		return 0;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return filter[side] == null ? null : filter[side].getGuiElement(player, side, serverSide);
	}

	public static final ResourceLocation HUD_TEXTURE = new ResourceLocation(TestCore.MODID + ":textures/gui/container/aaa.png");

	@Override
	public void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop) {

		//		GL11.glBegin(GL11.GL_QUADS);
		//		GL11.glTexCoord2f(0, 0);
		//		GL11.glVertex2i(0, 0);
		//		GL11.glTexCoord2f(0, 1);
		//		GL11.glVertex2i(0, sizeY);
		//		GL11.glTexCoord2f(1, 1);
		//		GL11.glVertex2i(sizeX, sizeY);
		//		GL11.glTexCoord2f(1, 0);
		//		GL11.glVertex2i(sizeX, 0);
		//		GL11.glEnd();

		if(isConnected() && renderTicks < 60){

			ScaledResolution sr = new ScaledResolution(mc);
			Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);

			String message = coreTile.getTile().getInterfaceString(mop.sideHit);
			if(message != null && message.length() != 0){
				String[] str = message.split(",");
				int length = mc.fontRendererObj.getStringWidth(str[0]);
				float alpha = (float) (1.0 - renderTicks / 60);
				int color;
				if(str.length == 1){
					color = ((int) (alpha * 255) << 24) | 0x04FFFFFF;
				}else{
					color = ((int) (alpha * 255) << 24) | Integer.parseInt(str[1], 16);
				}
				GL11.glPushMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glTranslatef(center.getX(), center.getY(), 0);
				GL11.glTranslatef(-length / 2, 20, 0);
				mc.fontRendererObj.drawString(str[0], 0, 0, color, true);
				GL11.glPopMatrix();
			}
		}
	}
	@Override
	public boolean comparePastRenderObj(Object object, MovingObjectPosition past, MovingObjectPosition current) {
		return object != null && object.getClass() == this.getClass() && past != null && current != null && past.sideHit == current.sideHit;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		for (int i = 0; i < 6; i++){
			NBTTagCompound sideTag = tag.getCompoundTag("side" + i);
			flagIO[i] = sideTag.getByte("io");
		}
		for (int side = 0; side < 6; side++){
			NBTTagCompound sideTag = tag.getCompoundTag("filter" + side);
			filter[side] = AbstractFilter.createFromNBT(this, EnumFacing.getFront(side), sideTag);
		}
		coreTile = ConnectionEntry.createFromNBT(tag.getCompoundTag("core"));
		needCheckCoreTile = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound[] sideTag = new NBTTagCompound[6];
		for (int i = 0; i < 6; i++){
			sideTag[i] = new NBTTagCompound();
			sideTag[i].setByte("io", (byte) flagIO[i]);
			tag.setTag("side" + i, sideTag[i]);
		}
		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound filterTag = new NBTTagCompound();
			filter[side].writeToNBT(filterTag);
			tag.setTag("filter" + side, filterTag);
		}
		if(isConnected()){
			NBTTagCompound coreTag = new NBTTagCompound();
			coreTile.writeToNBT(coreTag);
			tag.setTag("core", coreTag);
		}
	}

}
