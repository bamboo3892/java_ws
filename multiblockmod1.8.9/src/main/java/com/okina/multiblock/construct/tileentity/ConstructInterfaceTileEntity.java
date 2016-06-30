package com.okina.multiblock.construct.tileentity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;
import com.okina.utils.RectangularSolid;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;

public class ConstructInterfaceTileEntity extends ConstructBaseTileEntity implements IConstructInventory, ILinkConnectionUser {

	public static String nameForNBT = "interface";

	public ConnectionEntry<IConstructInventory> connection = null;
	protected boolean needUpdateEntry = true;

	public ConstructInterfaceTileEntity() {
		this(0);
	}

	public ConstructInterfaceTileEntity(int grade) {
		super(grade);
	}

	@Override
	public void update() {
		super.update();
		updateEntry();
		//		if(needUpdateEntry){
		//			updateEntry();
		//			needUpdateEntry = false;
		//		}
		if(!worldObj.isRemote) itemTransfer();
	}

	private boolean itemTransfer() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(connection != null && connection.getTile() != null){
			for (EnumFacing dir : EnumFacing.VALUES){
				TileEntity tile = worldObj.getTileEntity(pos.add(dir.getDirectionVec()));
				if(tile instanceof IInventory && connection.getTile() != tile){
					if(flagIO[dir.getIndex()] == 1){
						if(InventoryHelper.tryPushItemEX(this, (IInventory) tile, dir, dir.getOpposite(), ConstructInventoryBaseTileEntity.maxTransfer[grade])){
							return true;
						}
					}else if(flagIO[dir.getIndex()] == 0){
						if(InventoryHelper.tryPushItemEX((IInventory) tile, this, dir.getOpposite(), dir, ConstructInventoryBaseTileEntity.maxTransfer[grade])){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(connection != null){
			if(!worldObj.isAirBlock(connection.getPosition())){
				worldObj.getBlockState(connection.getPosition()).getBlock().onBlockActivated(worldObj, connection.getPosition(), worldObj.getBlockState(connection.getPosition()), player, side, hitX, hitY, hitZ);
			}
			return true;
		}else{
			return false;
		}
	}
	@Override
	public boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			spawnCennectionParticle(EnumParticleTypes.CLOUD);
		}else{
			//do nothing
		}
		return true;
	}
	@Override
	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(player.isSneaking()){
			//do nothing
		}else{
			flagIO[side.getIndex()] = flagIO[side.getIndex()] == 0 ? 1 : (flagIO[side.getIndex()] == 1 ? 2 : 0);
			TestCore.proxy.markForTileUpdate(pos, PacketType.FLAG_IO);
			if(worldObj.getTileEntity(pos.add(side.getDirectionVec())) instanceof BlockPipeTileEntity){
				((BlockPipeTileEntity) worldObj.getTileEntity(pos.add(side.getDirectionVec()))).checkConnection();
			}
			if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(flagIO[side.getIndex()] == 0 ? "input" : flagIO[side.getIndex()] == 1 ? "output" : "disabled"));
		}
		return true;
	}

	//	@Override
	//	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
	//		if(this.connection != null){
	//			if(worldObj.getBlock(this.connection.x, this.connection.y, this.connection.z) != null){
	//				worldObj.getBlock(this.connection.x, this.connection.y, this.connection.z).onBlockActivated(worldObj, this.connection.x, this.connection.y, this.connection.z, player, side, hitX, hitY, hitZ);
	//			}
	//			return true;
	//		}else{
	//			return false;
	//		}
	//	}
	//	@Override
	//	public boolean onShiftRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
	//		if(worldObj.isRemote){
	//			spawnCennectionParticle("cloud");
	//		}else{
	//			//do nothing
	//		}
	//		return true;
	//	}
	//	@Override
	//	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
	//		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
	//		if(player.isSneaking()){
	//			//do nothing
	//		}else{
	//			flagIO[side] = flagIO[side] == 0 ? 1 : (flagIO[side] == 1 ? 2 : 0);
	//			TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
	//			ForgeDirection dir = ForgeDirection.getOrientation(side);
	//			if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
	//				((BlockPipeTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
	//			}
	//			if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(flagIO[side] == 0 ? "input" : flagIO[side] == 1 ? "output" : "disabled"));
	//		}
	//		return true;
	//	}

	@Override
	public void onTileRemoved() {

	}

	protected boolean updateEntry() {
		if(connection != null){
			TileEntity tile = worldObj.getTileEntity(connection.getPosition());
			if(tile instanceof IConstructInventory && !tile.isInvalid()){
				connection.setTile((IConstructInventory) tile);
			}else{
				connection = null;
				TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONNECTION);
			}
		}
		return true;
	}

	private void spawnCennectionParticle(EnumParticleTypes name) {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(connection != null){
			for (int n = 0; n < 7; n++){
				double xOffset = (n & 1) == 1 ? -0.5 : 0.5;
				double yOffset = (n & 2) == 2 ? -0.5 : 0.5;
				double zOffset = (n & 4) == 4 ? -0.5 : 0.5;
				worldObj.spawnParticle(name, connection.x + xOffset + 0.5, connection.y + yOffset + 0.5, connection.z + zOffset + 0.5, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean canStartAt(EnumFacing side) {
		return true;
	}
	@Override
	public boolean tryConnect(ConstructBaseTileEntity tile, EnumFacing clickedSide, EnumFacing linkUserSide) {
		if(tile instanceof IConstructInventory && tile != this && tile.grade <= grade){
			connection = new ConnectionEntry(tile);
			return true;
		}
		return false;
	}

	/**use to send connection packet with spawn particle*/
	protected boolean spawnParticle = false;

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.NBT_CONNECTION){
			NBTTagCompound tag = new NBTTagCompound();
			if(connection != null){
				connection.writeToNBT(tag);
			}
			if(spawnParticle){
				tag.setBoolean("connectP", true);
				spawnParticle = false;
			}
			return new SimpleTilePacket(this, PacketType.NBT_CONNECTION, tag);
		}
		return super.getPacket(type);
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.NBT_CONNECTION && value instanceof NBTTagCompound){//should client
			NBTTagCompound tag = (NBTTagCompound) value;
			connection = ConnectionEntry.createFromNBT(tag);
			needUpdateEntry = true;
			if(tag.getBoolean("connectP")) spawnCennectionParticle(EnumParticleTypes.CLOUD);
		}
	}

	/**contents items update*/
	@Override
	public void markDirty() {
		super.markDirty();
		if(connection != null && connection.getTile() != null){
			TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONTENT);
		}
	}

	@Override
	public int getSizeInventory() {
		if(connection != null && connection.getTile() != null){
			return connection.getTile().getSizeInventory();
		}else{
			return 0;
		}
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		if(connection != null && connection.getTile() != null){
			return connection.getTile().getStackInSlot(slotIndex);
		}else{
			return null;
		}
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int amount) {
		if(connection != null && connection.getTile() != null){
			return connection.getTile().decrStackSize(slotIndex, amount);
		}else{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		if(connection != null && connection.getTile() != null){
			connection.getTile().setInventorySlotContents(slotIndex, itemStack);
			return;
		}else{
			return;
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
		if(connection != null && connection.getTile() != null){
			return connection.getTile().getName();
		}else{
			return null;
		}
	}
	@Override
	public boolean hasCustomName() {
		if(connection != null && connection.getTile() != null){
			return connection.getTile().hasCustomName();
		}else{
			return false;
		}
	}
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
	}
	@Override
	public int getInventoryStackLimit() {
		if(connection != null && connection.getTile() != null){
			return connection.getTile().getInventoryStackLimit();
		}else{
			return 0;
		}
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(connection != null && connection.getTile() != null){
			return connection.getTile().isUseableByPlayer(player);
		}else{
			return false;
		}
	}
	@Override
	public void openInventory(EntityPlayer player) {
		if(connection != null && connection.getTile() != null){
			connection.getTile().openInventory(player);
		}else{
			return;
		}
	}
	@Override
	public void closeInventory(EntityPlayer player) {
		if(connection != null && connection.getTile() != null){
			connection.getTile().closeInventory(player);
		}else{
			return;
		}
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		if(connection != null && connection.getTile() != null){
			return connection.getTile().isItemValidForSlot(slotIndex, itemStack);
		}else{
			return false;
		}
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(connection != null && connection.getTile() instanceof ISidedInventory){
			ISidedInventory inv = connection.getTile();
			return inv.getSlotsForFace(side);
		}else if(connection != null && connection.getTile() instanceof IInventory){
			int[] slot = new int[connection.getTile().getSizeInventory()];
			for (int n = 0; n < slot.length; n++){
				slot[n] = n;
			}
			return slot;
		}else{
			return new int[0];
		}
	}
	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if(connection != null && connection.getTile() instanceof ISidedInventory){
			int io = ((ConstructBaseTileEntity) connection.getTile()).flagIO[side.getIndex()];
			((ConstructBaseTileEntity) connection.getTile()).flagIO[side.getIndex()] = 0;
			boolean bbb = connection.getTile().canInsertItem(slotIndex, itemStack, side);
			((ConstructBaseTileEntity) connection.getTile()).flagIO[side.getIndex()] = io;
			return bbb;
		}else if(connection != null && connection.getTile() instanceof IInventory){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if(connection != null && connection.getTile() instanceof ISidedInventory){
			int io = ((ConstructBaseTileEntity) connection.getTile()).flagIO[side.getIndex()];
			((ConstructBaseTileEntity) connection.getTile()).flagIO[side.getIndex()] = 1;
			boolean bbb = connection.getTile().canExtractItem(slotIndex, itemStack, side);
			((ConstructBaseTileEntity) connection.getTile()).flagIO[side.getIndex()] = io;
			return bbb;
		}else if(connection != null && connection.getTile() instanceof IInventory){
			return true;
		}else{
			return false;
		}
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
	public void renderHUD(Minecraft mc, double renderTicks, MovingObjectPosition mop) {
		super.renderHUD(mc, renderTicks, mop);
		if(connection != null && connection.hasTile() && !mc.theWorld.isAirBlock(connection.getPosition())){
			String message = "Link to " + mc.theWorld.getBlockState(connection.getPosition()).getBlock().getLocalizedName();
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

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		updateEntry();
		tag.setString("name", getNameForNBT());
		tag.setInteger("index", solid.toIndex(connection.x, connection.y, connection.z, false));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound entrytag = tag.getCompoundTag("entry");
		connection = ConnectionEntry.createFromNBT(entrytag);
		needUpdateEntry = true;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(connection != null){
			NBTTagCompound entryTag = new NBTTagCompound();
			connection.writeToNBT(entryTag);
			tag.setTag("entry", entryTag);
		}
	}

}
