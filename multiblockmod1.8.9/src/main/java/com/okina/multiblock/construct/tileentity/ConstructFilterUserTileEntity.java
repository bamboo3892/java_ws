package com.okina.multiblock.construct.tileentity;

import java.util.Random;

import com.okina.inventory.AbstractFilter;
import com.okina.inventory.FilterInventory;
import com.okina.inventory.IFilterUser;
import com.okina.main.GuiHandler.IGuiTile;
import com.okina.main.TestCore;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.InventoryHelper;
import com.okina.utils.RectangularSolid;
import com.okina.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class ConstructFilterUserTileEntity extends ConstructInventoryBaseTileEntity implements IFilterUser, IGuiTile {

	public static final int[] filterCapability = { 0, 2, 4, 5, 6 };

	private AbstractFilter[] filter = new AbstractFilter[6];

	public ConstructFilterUserTileEntity(int grade) {
		super(grade);
	}

	@Override
	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && (player.getHeldItem().getItem() == TestCore.filter || player.getHeldItem().getItem() == TestCore.craftingFilter)) return false;
		if(filter[side.getIndex()] != null){
			return filter[side.getIndex()].onRightClicked(worldObj, pos, side, player);
			//player.openGui(TestCore.instance, TestCore.FILTER_GUI_ID_0 + side, worldObj, xCoord, yCoord, zCoord);
		}else{
			return onRightClickedNotFilterSide(player, side, hitX, hitY, hitZ);
		}
	}
	@Override
	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof IToolWrench)) return false;
		if(!player.isSneaking()){
			int n = changeIO(side);
			if(worldObj.getTileEntity(pos.add(side.getDirectionVec())) instanceof BlockPipeTileEntity){
				((BlockPipeTileEntity) worldObj.getTileEntity(pos.add(side.getDirectionVec()))).checkConnection();
			}
			TestCore.proxy.markForTileUpdate(pos, PacketType.FLAG_IO);
			if(worldObj.isRemote) player.addChatMessage(new ChatComponentText(n == 0 ? "input" : n == 1 ? "output" : "disabled"));
		}else{
			if(worldObj.isRemote){
				//do nothing
			}else{
				if(flagIO[side.getIndex()] == 1){
					if(connectNextBlock(side)){
						ConnectionEntry entry = connection[side.getIndex()];
						if(!(entry == null)){
							player.addChatMessage(new ChatComponentText(connection[side.getIndex()].getText()));
						}else{
							player.addChatMessage(new ChatComponentText("No Connection Found"));
						}
					}else{
						player.addChatMessage(new ChatComponentText("No Connection Found"));
					}
					spawnParticle = true;
					pSide = side;
					TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONNECTION);
				}
			}
		}
		return false;
	}

	public abstract boolean onRightClickedNotFilterSide(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ);

	@Override
	public void onLeftClicked(EntityPlayer player, MovingObjectPosition mop) {
		if(player.isSneaking() && filter[mop.sideHit.getIndex()] != null){
			ItemStack filter = removeFilter(mop.sideHit);
			if(filter != null && !worldObj.isRemote){
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + mop.sideHit.getFrontOffsetX() + 0.5, pos.getY() + mop.sideHit.getFrontOffsetY() + 0.5, pos.getZ() + mop.sideHit.getFrontOffsetZ() + 0.5, filter));
			}
		}
	}

	@Override
	public void onTileRemoved() {
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
				worldObj.spawnEntityInWorld(entityitem);
			}
		}
		for (int i1 = 0; i1 < getSizeInventory(); ++i1){
			ItemStack itemstack = getStackInSlot(i1);

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

	@Override
	public void updateFilter() {
		TestCore.proxy.markForTileUpdate(getPosition(), PacketType.FILTER2);
	}
	@Override
	public boolean setFilter(EnumFacing side, AbstractFilter filter) {
		if(this.filter[side.getIndex()] != null) return false;
		int n = 0;
		for (int i = 0; i < 6; i++){
			if(this.filter[i] != null) n++;
		}
		if(n >= filterCapability[grade]) return false;
		this.filter[side.getIndex()] = filter;
		updateFilter();
		return true;
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
					if(InventoryHelper.tryPushItemEX(this, connection[side].getTile(), EnumFacing.getFront(side), connection[side].getFront(), maxTransfer)){
						return true;
					}
				}else{
					if(filter[side].tranferItem(this, connection[side].getTile(), EnumFacing.getFront(side), connection[side].getFront(), maxTransfer)){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if(flagIO[side.getIndex()] != 0){
			return false;
		}else{
			if(filter[side.getIndex()] != null){
				if(filter[side.getIndex()].canTransferItem(itemStack)){
					return true;
				}
				return false;
			}else{
				return true;
			}
		}
	}
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, EnumFacing side) {
		if(flagIO[side.getIndex()] != 1){
			return false;
		}else{
			if(filter[side.getIndex()] != null){
				if(filter[side.getIndex()].canTransferItem(itemStack)){
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

	@Override
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
				filter[side] = AbstractFilter.createFromNBT(this, EnumFacing.getFront(side), sideTag);
			}
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
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
			filter[side] = AbstractFilter.createFromNBT(this, EnumFacing.getFront(side), sideTag);
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
