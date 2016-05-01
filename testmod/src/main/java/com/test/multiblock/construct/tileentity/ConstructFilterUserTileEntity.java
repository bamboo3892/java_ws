package com.test.multiblock.construct.tileentity;

import java.util.Random;

import com.test.inventory.AbstractFilter;
import com.test.inventory.FilterInventory;
import com.test.inventory.IFilterUser;
import com.test.main.GuiHandler.IGuiTile;
import com.test.main.TestCore;
import com.test.multiblock.BlockPipeTileEntity;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.utils.ConnectionEntry;
import com.test.utils.InventoryHelper;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;
import com.test.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class ConstructFilterUserTileEntity extends ConstructInventoryBaseTileEntity implements IFilterUser, IGuiTile {

	public static final int[] filterCapability = { 0, 2, 4, 5, 6 };

	private AbstractFilter[] filter = new AbstractFilter[6];

	public ConstructFilterUserTileEntity(int grade) {
		super(grade);
	}

	@Override
	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && (player.getHeldItem().getItem() == TestCore.filter || player.getHeldItem().getItem() == TestCore.craftingFilter)) return false;
		if(filter[side] != null){
			return filter[side].onRightClicked(worldObj, xCoord, yCoord, zCoord, side, player);
			//player.openGui(TestCore.instance, TestCore.FILTER_GUI_ID_0 + side, worldObj, xCoord, yCoord, zCoord);
		}else{
			return onRightClickedNotFilterSide(player, side, hitX, hitY, hitZ);
		}
	}

	public abstract boolean onRightClickedNotFilterSide(EntityPlayer player, int side, float hitX, float hitY, float hitZ);

	@Override
	public boolean onRightClickedByWrench(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(!player.isSneaking()){
			int n = changeIO(side);
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockPipeTileEntity){
				((BlockPipeTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)).checkConnection();
			}
			TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.FLAG_IO);
			if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(n == 0 ? "input" : n == 1 ? "output" : "disabled"));
		}else{
			if(worldObj.isRemote){
				//do nothing
			}else{
				if(flagIO[side] == 1){
					if(connectNextBlock(side)){
						ConnectionEntry entry = connection[side];
						if(!(entry == null)){
							player.addChatMessage(new ChatComponentText(connection[side].getText()));
						}else{
							player.addChatMessage(new ChatComponentText("No Connection Found"));
						}
					}else{
						player.addChatMessage(new ChatComponentText("No Connection Found"));
					}
					this.spawnParticle = true;
					this.pSide = side;
					TestCore.proxy.markForTileUpdate(new Position(xCoord, yCoord, zCoord), PacketType.NBT_CONNECTION);
				}
			}
		}
		return false;
	}

	@Override
	public void onLeftClicked(EntityPlayer player, int side, double hitX, double hitY, double hitZ) {
		if(player.isSneaking() && filter[side] != null){
			ItemStack filter = this.removeFilter(side);
			if(filter != null && !worldObj.isRemote){
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + dir.offsetX + 0.5, yCoord + dir.offsetY + 0.5, zCoord + dir.offsetZ + 0.5, filter));
			}
		}
	}

	@Override
	public void onTileRemoved() {
		Random rand = worldObj.rand;
		for (int i = 0; i < 6; i++){
			if(filter[i] != null){
				ItemStack itemStack = filter[i].getFilterItem();
				double x = (double) ((float) xCoord + rand.nextFloat() * 0.8F + 0.1F);
				double y = (double) ((float) yCoord + rand.nextFloat() * 0.8F + 0.1F);
				double z = (double) ((float) zCoord + rand.nextFloat() * 0.8F + 0.1F);
				EntityItem entityitem = new EntityItem(worldObj, x, y, z, itemStack);
				entityitem.motionX = (double) ((float) rand.nextGaussian() * 0.05F);
				entityitem.motionY = (double) ((float) rand.nextGaussian() * 0.05F + 0.2F);
				entityitem.motionZ = (double) ((float) rand.nextGaussian() * 0.05F);
				worldObj.spawnEntityInWorld(entityitem);
			}
		}
		for (int i1 = 0; i1 < this.getSizeInventory(); ++i1){
			ItemStack itemstack = this.getStackInSlot(i1);

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

	public void updateFilter() {
		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.FILTER2);
	}
	public boolean setFilter(int side, AbstractFilter filter) {
		if(this.filter[side] != null) return false;
		int n = 0;
		for (int i = 0; i < 6; i++){
			if(this.filter[i] != null) n++;
		}
		if(n >= filterCapability[grade]) return false;
		this.filter[side] = filter;
		this.updateFilter();
		return true;
	}
	public AbstractFilter getFilter(int side) {
		if(side >= 0 && side < 6){
			return this.filter[side];
		}
		return null;
	}
	public ItemStack removeFilter(int side) {
		if(filter[side] == null){
			return null;
		}else{
			ItemStack itemStack = filter[side].getFilterItem();
			filter[side] = null;
			this.updateFilter();
			return itemStack;
		}
	}
	public World getWorldObject() {
		return this.worldObj;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected boolean itemTransfer(int maxTransfer) {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		int[] randomOrder = UtilMethods.getRandomArray(new int[] { 0, 1, 2, 3, 4, 5 });
		int[] priority = new int[6];
		for (int side = 0; side < 6; side++){
			if(connection[side] != null && flagIO[side] == 1){
				if(filter[side] != null){
					priority[side] = filter[side].getPriority();
				}else{
					priority[side] = 0;
				}
			}else{
				priority[side] = 0;
			}
		}
		int[] order = new int[6];
		for (int i = 0; i < 6; i++){
			order[i] = -1;
		}
		for (int i = AbstractFilter.MAX_PRIORITY; i >= 0; i--){
			for (int side : randomOrder){
				if(priority[side] == i){
					int index = 0;
					while (order[index] != -1)
						index++;
					order[index] = side;
				}
			}
		}
		for (int side : order){
			if(connection[side] != null && connection[side].getTile() != null && flagIO[side] == 1){
				if(filter[side] == null){
					if(InventoryHelper.tryPushItemEX(this, connection[side].getTile(), ForgeDirection.getOrientation(side), ForgeDirection.getOrientation(connection[side].side), maxTransfer)){
						return true;
					}
				}else{
					if(filter[side].tranferItem(this, connection[side].getTile(), side, connection[side].side, maxTransfer)){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemStack, int side) {
		if(flagIO[side] != 0){
			return false;
		}else{
			if(filter[side] != null){
				if(filter[side].canTransferItem(itemStack)){
					return true;
				}
				return false;
			}else{
				return true;
			}
		}
	}
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		if(flagIO[side] != 1){
			return false;
		}else{
			if(filter[side] != null){
				if(filter[side].canTransferItem(itemStack)){
					return true;
				}
				return false;
			}else{
				return true;
			}
		}
	}

	/**callled on only server*/
	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.FILTER2){
			NBTTagCompound tag = new NBTTagCompound();
			for (int side = 0; side < 6; side++){
				if(filter[side] == null) continue;
				NBTTagCompound filterTag = new NBTTagCompound();
				filter[side].writeToNBT(filterTag);
				tag.setTag("filter" + side, filterTag);
			}
			return new SimpleTilePacket(this, PacketType.FILTER2, tag);
		}
		return super.getPacket(type);
	}

	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.FILTER && value instanceof String){
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
				filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
			}
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
		}
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		return filter[side] != null ? filter[side].getGuiElement(player, side, serverSide) : null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound sideTag = new NBTTagCompound();
			filter[side].writeToNBT(sideTag);
			tag.setTag("filter" + side, sideTag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound sideTag;
		for (int side = 0; side < 6; side++){
			sideTag = tag.getCompoundTag("filter" + side);
			filter[side] = AbstractFilter.createFromNBT(this, side, sideTag);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		for (int side = 0; side < 6; side++){
			if(filter[side] == null) continue;
			NBTTagCompound sideTag = new NBTTagCompound();
			filter[side].writeToNBT(sideTag);
			tag.setTag("filter" + side, sideTag);
		}
	}

}