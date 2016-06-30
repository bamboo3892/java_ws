package com.okina.multiblock;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.okina.inventory.AbstractFilter;
import com.okina.main.GuiHandler.IGuiTile;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.parts.ConstructPartBase;
import com.okina.multiblock.construct.parts.EnergyProviderPart;
import com.okina.multiblock.construct.tileentity.ConstructInventoryBaseTileEntity;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.register.ConstructPartRegistry;
import com.okina.tileentity.ISimpleTilePacketUser;
import com.okina.utils.ConnectionEntry;
import com.okina.utils.RectangularSolid;
import com.okina.utils.UtilMethods;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;

public class MultiBlockCoreTileEntity extends TileEntity implements ITickable, ISimpleTilePacketUser, IEnergyReceiver, IEnergyProvider, ISidedInventory, IGuiTile {

	public int xSize;//the number of blocks in x direction
	public int ySize;
	public int zSize;
	public ConstructPartBase[][][] parts;
	private ConnectionEntry<ConstructPartBase>[] interfaceConnection = new ConnectionEntry[6];
	private int[] interfaceGrade = new int[6];
	private boolean[] sidePowered = new boolean[6];
	private EnergyStorage energyStorage;

	public boolean connected;
	private boolean isPausing = true;
	private boolean[][][] contentUpdateMarked;

	public boolean renderDetail = true;

	public MultiBlockCoreTileEntity() {

	}

	@Override
	public void update() {
		if(connected){
			if(!worldObj.isRemote && !isPausing){
				//casing transfer
				for (int kkk : UtilMethods.getRandomArray(0, 5)){
					EnumFacing dir = EnumFacing.getFront(kkk);
					List<Integer> priority = new LinkedList<Integer>();
					List<MultiBlockCasingTileEntity> inv = new LinkedList<MultiBlockCasingTileEntity>();
					int xxx = dir.getFrontOffsetX() != 0 ? 0 : 1;
					int yyy = dir.getFrontOffsetY() != 0 ? 0 : 1;
					int zzz = dir.getFrontOffsetZ() != 0 ? 0 : 1;
					for (int xxx2 = -1; xxx2 < (dir.getFrontOffsetX() == 0 ? 2 : 0); xxx2++){
						for (int yyy2 = -1; yyy2 < (dir.getFrontOffsetY() == 0 ? 2 : 0); yyy2++){
							for (int zzz2 = -1; zzz2 < (dir.getFrontOffsetZ() == 0 ? 2 : 0); zzz2++){
								MultiBlockCasingTileEntity ttt = (MultiBlockCasingTileEntity) worldObj.getTileEntity(pos.add(dir.getDirectionVec()).add(xxx * xxx2, yyy * yyy2, zzz * zzz2));
								inv.add(ttt);
								if(ttt.flagIO[dir.ordinal()] == 1 || ttt.flagIO[dir.ordinal()] == 0){
									if(ttt.getFilter(dir) != null){
										priority.add(ttt.getFilter(dir).getPriority());
									}else{
										priority.add(0);
									}
								}else{
									priority.add(0);
								}
							}
						}
					}
					if(priority.size() != 9 || inv.size() != 9) kkk = 0 / 0;
					int[] order = new int[9];
					for (int i = 0; i < 9; i++){
						order[i] = -1;
					}
					int[] randomOrder = UtilMethods.getRandomArray(0, 8);
					for (int i = AbstractFilter.MAX_PRIORITY; i >= 0; i--){
						for (int j : randomOrder){
							if(priority.get(j) == i){
								int index = 0;
								while (order[index] != -1)
									index++;
								order[index] = j;
							}
						}
					}
					for (int index : order){
						inv.get(index).transferItemForSide(dir);
					}
				}

				//	//update all parts
				for (int i = 0; i < xSize; i++){
					for (int j = 0; j < ySize; j++){
						for (int k = 0; k < zSize; k++){
							if(parts[i][j][k] != null){
								parts[i][j][k].updatePart();
							}
						}
					}
				}
			}

			//particle
			if(worldObj.isRemote){
				if((int) (Math.random() * xSize * ySize * zSize) % 5 == 0){
					int x = (int) (Math.random() * xSize);
					int y = (int) (Math.random() * ySize);
					int z = (int) (Math.random() * zSize);
					if(parts[x][y][z] != null){
						parts[x][y][z].onRandomDisplayTick();
					}
				}
				if((int) (Math.random() * 10) % 10 == 0){
					int side = (int) (Math.random() * 6);
					int side2 = toInsideWorldSide(EnumFacing.getFront(side)).getIndex();
					if(interfaceConnection[side2] != null && interfaceConnection[side2].getTile() != null){
						EnumFacing dir = EnumFacing.getFront(side);
						double x2 = toReadWorldX(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
						double y2 = toReadWorldY(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
						double z2 = toReadWorldZ(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
						int side3 = EnumFacing.getFront(side).getOpposite().getIndex();
						TestCore.spawnParticle(getWorld(), TestCore.PARTICLE_BEZIER_DOT, pos.getX() + 0.5 + dir.getFrontOffsetX() * 1.5, pos.getY() + 0.5 + dir.getFrontOffsetY() * 1.5, pos.getZ() + 0.5 + dir.getFrontOffsetZ() * 1.5, x2, y2, z2, side3, side, 0x00FF00);
					}
				}
			}
		}
	}

	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(connected){
			side = toInsideWorldSide(side);
			if(interfaceConnection[side.getIndex()] != null && interfaceConnection[side.getIndex()].getTile() != null){
				if(interfaceConnection[side.getIndex()].getTile().isOpenGuiOnClicked()){
					player.openGui(TestCore.instance, TestCore.BLOCK_GUI_ID_0 + toRealWorldSide(side).getIndex(), worldObj, pos.getX(), pos.getY(), pos.getZ());
					return true;
				}
				return interfaceConnection[side.getIndex()].getTile().onRightClicked(worldObj, player, side);
			}
		}
		return false;
	}

	public boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(connected){
			int side2 = toInsideWorldSide(side).getIndex();
			if(interfaceConnection[side2] != null && interfaceConnection[side2].getTile() != null){
				double x2 = toReadWorldX(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
				double y2 = toReadWorldY(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
				double z2 = toReadWorldZ(interfaceConnection[side2].x + 0.5, interfaceConnection[side2].y + 0.5, interfaceConnection[side2].z + 0.5);
				int side3 = side.getOpposite().getIndex();
				TestCore.spawnParticle(getWorld(), TestCore.PARTICLE_BEZIER_DOT, pos.getX() + 0.5 + side.getFrontOffsetX() * 0.5, pos.getY() + 0.5 + side.getFrontOffsetY() * 0.5, pos.getZ() + 0.5 + side.getFrontOffsetZ() * 0.5, x2, y2, z2, side3, side, 0x00FF00);
				return true;
			}
		}
		return false;
	}

	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.isSneaking()){
			if(connected){
				isPausing = !isPausing;
			}
		}else{
			renderDetail = !renderDetail;
			if(connected && !renderDetail){
				for (int i = 0; i < xSize; i++){
					for (int j = 0; j < ySize; j++){
						for (int k = 0; k < zSize; k++){
							ConstructPartBase part = getPart(i, j, k);
							if(part != null){
								double x2 = toReadWorldX(i + 0.5, j + 0.5, k + 0.5);
								double y2 = toReadWorldY(i + 0.5, j + 0.5, k + 0.5);
								double z2 = toReadWorldZ(i + 0.5, j + 0.5, k + 0.5);
								int grade = part.grade;
								int color = grade == 0 ? 0x8B4513 : grade == 1 ? 0xE0FFFF : grade == 2 ? 0xFFFF00 : grade == 3 ? 0x00FFFF : 0x00FF00;
								float size = 1.5f / Math.max(xSize, Math.max(ySize, zSize));
								TestCore.spawnParticle(getWorld(), TestCore.PARTICLE_DOT, x2, y2, z2, color, size);
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean connect() {
		if(!connected){
			for (int i = -1; i < 2; i++){
				for (int j = -1; j < 2; j++){
					for (int k = -1; k < 2; k++){
						if(i == 0 && j == 0 && k == 0) continue;
						if(worldObj.getTileEntity(pos.add(i, j, k)) instanceof MultiBlockCasingTileEntity){
							MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) worldObj.getTileEntity(pos.add(i, j, k));
							if(tile.isConnected()) return false;
						}else{
							return false;
						}
					}
				}
			}
			for (int i = -1; i < 2; i++){
				for (int j = -1; j < 2; j++){
					for (int k = -1; k < 2; k++){
						if(i == 0 && j == 0 && k == 0) continue;
						MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) worldObj.getTileEntity(pos.add(i, j, k));
						tile.connect(this);
					}
				}
			}
			connected = true;
			TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "1"));
			return true;
		}
		return false;
	}

	public void disconnect() {
		if(connected){
			for (int i = -1; i < 2; i++){
				for (int j = -1; j < 2; j++){
					for (int k = -1; k < 2; k++){
						if(i == 0 && j == 0 && k == 0) continue;
						if(worldObj.getTileEntity(pos.add(i, j, k)) instanceof MultiBlockCasingTileEntity){
							MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) worldObj.getTileEntity(pos.add(i, j, k));
							tile.disconnect();
						}
					}
				}
			}
		}
		connected = false;
		TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "0"));
	}

	public int getMaxTransfer(EnumFacing side) {
		side = toInsideWorldSide(side);
		return ConstructInventoryBaseTileEntity.maxTransfer[interfaceGrade[side.getIndex()]];
	}
	public ConstructPartBase getInterfaceConnection(EnumFacing side) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side.getIndex()] != null){
			return interfaceConnection[side.getIndex()].getTile();
		}
		return null;
	}
	public String getInterfaceString(EnumFacing side) {
		side = toInsideWorldSide(side);
		if(interfaceConnection[side.getIndex()] != null && interfaceConnection[side.getIndex()].getTile() != null){
			return interfaceConnection[side.getIndex()].getTile().getNameForHUD();
		}
		return null;
	}

	public void changeSidePowered(EnumFacing side, boolean powered) {
		if(worldObj.isRemote) return;
		if(powered){
			sidePowered[side.getIndex()] = true;
		}else{
			int xxx = side.getFrontOffsetX() != 0 ? 0 : 1;
			int yyy = side.getFrontOffsetY() != 0 ? 0 : 1;
			int zzz = side.getFrontOffsetZ() != 0 ? 0 : 1;
			sidePowered[side.getIndex()] = false;
			flag: for (int xxx2 = -1; xxx2 < (side.getFrontOffsetX() == 0 ? 2 : 0); xxx2++){
				for (int yyy2 = -1; yyy2 < (side.getFrontOffsetY() == 0 ? 2 : 0); yyy2++){
					for (int zzz2 = -1; zzz2 < (side.getFrontOffsetZ() == 0 ? 2 : 0); zzz2++){
						int power = worldObj.getRedstonePower(new BlockPos(pos.getX() + xxx * xxx2 + side.getFrontOffsetX() * 2, pos.getY() + yyy * yyy2 + side.getFrontOffsetY() * 2, pos.getZ() + zzz * zzz2 + side.getFrontOffsetZ() * 2), side.getOpposite());
						if(power != 0){
							sidePowered[side.getIndex()] = true;
							break flag;
						}
					}
				}
			}
		}

		System.out.println(side + " : " + sidePowered[side.getIndex()]);

		//		if(this.interfaceConnection[side] != null && this.interfaceConnection[side].getTile() != null){
		//			this.interfaceConnection[side].getTile().isStopped = sidePowered[side];
		//		}
	}

	public int sendEnergy(int x, int y, int z, int maxSend) {
		int send = energyStorage.extractEnergy(maxSend, false);
		if(send > 0){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("id", 1);
			tag.setInteger("x", x);
			tag.setInteger("y", y);
			tag.setInteger("z", z);
			tag.setInteger("send", send);
			TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT2, tag));
		}
		//return send;
		return maxSend;
	}

	/**
	 * @param x : spawn x (inside coord)
	 * @param y : spawn y (inside coord)
	 * @param z : spawn z (inside coord)
	 * @param particleId
	 * @param objects
	 */
	public void spawnDotParticle(int x, int y, int z, int color) {
		double x1 = x + Math.random() * 0.5 - 0.25;
		double y1 = y + Math.random() * 0.5 - 0.25;
		double z1 = z + Math.random() * 0.5 - 0.25;
		double x2 = toReadWorldX(x1 + 0.5, y1 + 0.5, z1 + 0.5);
		double y2 = toReadWorldY(x1 + 0.5, y1 + 0.5, z1 + 0.5);
		double z2 = toReadWorldZ(x1 + 0.5, y1 + 0.5, z1 + 0.5);
		float size = 1.5f / Math.max(xSize, Math.max(ySize, zSize));
		TestCore.spawnParticle(worldObj, TestCore.PARTICLE_DOT, x2, y2, z2, color, size);
	}

	@Override
	public Object getGuiElement(EntityPlayer player, int side, boolean serverSide) {
		side = toInsideWorldSide(EnumFacing.getFront(side)).getIndex();
		if(interfaceConnection[side] != null && interfaceConnection[side].getTile() != null){
			return interfaceConnection[side].getTile().getGuiElement(player, serverSide);
		}
		return null;
	}

	//helper
	public ConstructPartBase getPart(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0 || x >= xSize || y >= ySize || z >= zSize) return null;
		return parts[x][y][z];
	}
	public double toReadWorldX(double x, double y, double z) {
		int maxLength = Math.max(xSize, Math.max(ySize, zSize));
		double xxx = -(xSize / 2f - x) * 2d / maxLength;
		double zzz = -(zSize / 2f - z) * 2d / maxLength;
		double angle = Math.PI / 2 * getBlockMetadata();
		return xxx * Math.cos(angle) - zzz * Math.sin(angle) + pos.getX() + 0.5d;
	}
	public double toReadWorldY(double x, double y, double z) {
		int maxLength = Math.max(xSize, Math.max(ySize, zSize));
		double yyy = pos.getY() + 0.5d - (ySize / 2f - y) * 2d / maxLength;
		return yyy;
	}
	public double toReadWorldZ(double x, double y, double z) {
		int maxLength = Math.max(xSize, Math.max(ySize, zSize));
		double xxx = -(xSize / 2f - x) * 2d / maxLength;
		double zzz = -(zSize / 2f - z) * 2d / maxLength;
		double angle = Math.PI / 2 * getBlockMetadata();
		return xxx * Math.sin(angle) + zzz * Math.cos(angle) + pos.getZ() + 0.5d;
	}
	public EnumFacing toRealWorldSide(EnumFacing side) {
		if(side == null) return null;
		for (int i = 0; i < getBlockMetadata(); i++){
			side = side.rotateYCCW();
		}
		return side;
	}
	public EnumFacing toInsideWorldSide(EnumFacing side) {
		if(side == null) return null;
		for (int i = 0; i < getBlockMetadata(); i++){
			side = side.rotateY();
		}
		return side;
	}

	public void markForContentUpdate(int x, int y, int z) {
		if(getPart(x, y, z) != null){
			contentUpdateMarked[x][y][z] = true;
			TestCore.proxy.markForTileUpdate(pos, PacketType.NBT_CONTENT);
		}
	}

	//server only
	public void sendPacket(PacketType type, int x, int y, int z, NBTTagCompound value) {
		if(worldObj.isRemote){
			System.err.println("Illegal side access");
			return;
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", type.id);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		tag.setTag("value", value);
		TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.NBT_MULTIBLOCK, tag));
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void markDirty() {
		super.markDirty();
		for (int side = 0; side < 6; side++){
			if(interfaceConnection[side] != null && interfaceConnection[side].getTile() != null){
				markForContentUpdate(interfaceConnection[side].x, interfaceConnection[side].y, interfaceConnection[side].z);
			}
		}
	}

	@Override
	public SimpleTilePacket getPacket(PacketType type) {
		if(type == PacketType.NBT_CONTENT){
			NBTTagCompound baseTag = new NBTTagCompound();
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < xSize; i++){
				for (int j = 0; j < ySize; j++){
					for (int k = 0; k < zSize; k++){
						if(contentUpdateMarked[i][j][k] && parts[i][j][k] != null){
							NBTTagCompound tag = parts[i][j][k].getContentUpdateTag();
							if(tag != null){
								tag.setInteger("x", i);
								tag.setInteger("y", j);
								tag.setInteger("z", k);
								list.appendTag(tag);
							}
							contentUpdateMarked[i][j][k] = false;
						}
					}
				}
			}
			baseTag.setTag("list", list);
			return new SimpleTilePacket(this, PacketType.NBT_CONTENT, baseTag);
		}
		return null;
	}
	@Override
	public void processCommand(PacketType type, Object value) {
		if(type == PacketType.OTHER){
			connected = value.equals("1");
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}else if(type == PacketType.NBT_MULTIBLOCK && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			int id = tag.getInteger("type");
			int x = tag.getInteger("x");
			int y = tag.getInteger("y");
			int z = tag.getInteger("z");
			NBTTagCompound valueTag = tag.getCompoundTag("value");
			if(getPart(x, y, z) != null){
				getPart(x, y, z).processCommand(PacketType.getFromId(id), valueTag);
			}
		}else if(type == PacketType.NBT_CONTENT && value instanceof NBTTagCompound){
			NBTTagList list = ((NBTTagCompound) value).getTagList("list", Constants.NBT.TAG_COMPOUND);
			if(list != null){
				for (int tagCounter = 0; tagCounter < list.tagCount(); ++tagCounter){
					NBTTagCompound tag = list.getCompoundTagAt(tagCounter);
					int x = tag.getInteger("x");
					int y = tag.getInteger("y");
					int z = tag.getInteger("z");
					ConstructPartBase part = getPart(x, y, z);
					if(part != null){
						part.processCommand(PacketType.NBT_CONTENT, tag);
					}
				}
			}
		}else if(type == PacketType.EFFECT2 && value instanceof NBTTagCompound){
			NBTTagCompound tag = (NBTTagCompound) value;
			int id = tag.getInteger("id");
			if(id == 1){
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				int send = tag.getInteger("send");
				Random rand = worldObj.rand;
				for (int i = 0; i < (int) (send / 100F); i++){
					double startX = pos.getX() + 0.4 + rand.nextDouble() * 0.2;
					double startY = pos.getY() + 0.4 + rand.nextDouble() * 0.2;
					double startZ = pos.getZ() + 0.4 + rand.nextDouble() * 0.2;
					double endX = toReadWorldX(x + 0.5, y + 0.5, z + 0.5) - startX;
					double endY = toReadWorldY(x + 0.5, y + 0.5, z + 0.5) - startY;
					double endZ = toReadWorldZ(x + 0.5, y + 0.5, z + 0.5) - startZ;
					TestCore.spawnParticle(worldObj, TestCore.PARTICLE_ENERGY, startX, startY, startZ, endX, endY, endZ);
				}
			}
		}
	}
	@Override
	public BlockPos getPosition() {
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

	private int attemptSide = 0;

	@Override
	public int getSizeInventory() {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			return ((IInventory) interfaceConnection[attemptSide].getTile()).getSizeInventory();
		}
		return 0;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			return ((IInventory) interfaceConnection[attemptSide].getTile()).getStackInSlot(slot);
		}
		return null;
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			return ((IInventory) interfaceConnection[attemptSide].getTile()).decrStackSize(slot, amount);
		}
		return null;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			((IInventory) interfaceConnection[attemptSide].getTile()).setInventorySlotContents(slot, itemStack);
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
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			return ((IInventory) interfaceConnection[attemptSide].getTile()).getName();
		}
		return null;
	}
	@Override
	public boolean hasCustomName() {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			return ((IInventory) interfaceConnection[attemptSide].getTile()).hasCustomName();
		}
		return false;
	}
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
	}
	@Override
	public int getInventoryStackLimit() {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			return ((IInventory) interfaceConnection[attemptSide].getTile()).getInventoryStackLimit();
		}
		return 0;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			return ((IInventory) interfaceConnection[attemptSide].getTile()).isUseableByPlayer(player);
		}
		return false;
	}
	@Override
	public void openInventory(EntityPlayer player) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			((IInventory) interfaceConnection[attemptSide].getTile()).openInventory(player);
		}
	}
	@Override
	public void closeInventory(EntityPlayer player) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			((IInventory) interfaceConnection[attemptSide].getTile()).closeInventory(player);
		}
	}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			return ((IInventory) interfaceConnection[attemptSide].getTile()).isItemValidForSlot(slot, itemStack);
		}
		return false;
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		side = toInsideWorldSide(side);
		attemptSide = side.getIndex();
		if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
			return ((ISidedInventory) interfaceConnection[attemptSide].getTile()).getSlotsForFace(side);
		}
		return new int[0];
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
	public boolean canInsertItem(int slot, ItemStack itemStack, EnumFacing side) {
		if(connected){
			side = toInsideWorldSide(side);
			attemptSide = side.getIndex();
			if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
				int io = interfaceConnection[attemptSide].getTile().flagIO[side.getIndex()];
				interfaceConnection[attemptSide].getTile().flagIO[side.getIndex()] = 0;
				boolean bbb = ((ISidedInventory) interfaceConnection[attemptSide].getTile()).canInsertItem(slot, itemStack, side);
				interfaceConnection[attemptSide].getTile().flagIO[side.getIndex()] = io;
				return bbb;
			}
		}
		return false;
	}
	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, EnumFacing side) {
		if(connected){
			side = toInsideWorldSide(side);
			attemptSide = side.getIndex();
			if(interfaceConnection[attemptSide] != null && interfaceConnection[attemptSide].getTile() != null){
				int io = interfaceConnection[attemptSide].getTile().flagIO[side.getIndex()];
				interfaceConnection[attemptSide].getTile().flagIO[side.getIndex()] = 1;
				boolean bbb = ((ISidedInventory) interfaceConnection[attemptSide].getTile()).canExtractItem(slot, itemStack, side);
				interfaceConnection[attemptSide].getTile().flagIO[side.getIndex()] = io;
				return bbb;
			}
		}
		return false;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return connected;
	}
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return energyStorage.receiveEnergy(maxReceive, simulate);
	}
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return energyStorage.extractEnergy(maxExtract, simulate);
	}
	@Override
	public int getEnergyStored(EnumFacing from) {
		return energyStorage.getEnergyStored();
	}
	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB bb = INFINITE_EXTENT_AABB;
		return bb;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void writeToNBTForItemDrop(ItemStack itemStack) {
		NBTTagCompound itemTag = itemStack.getTagCompound() == null ? new NBTTagCompound() : itemStack.getTagCompound();
		writeToNBT(itemTag);
		itemStack.setTagCompound(itemTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		connected = tag.getBoolean("connected");
		isPausing = tag.getBoolean("pause");
		if(tag.hasKey("renderDetail")){
			renderDetail = tag.getBoolean("renderDetail");
		}else{
			renderDetail = true;
		}

		//content
		xSize = tag.getInteger("xSize");
		ySize = tag.getInteger("ySize");
		zSize = tag.getInteger("zSize");
		parts = new ConstructPartBase[xSize][ySize][zSize];

		NBTTagList blockTagList = tag.getTagList("blockList", Constants.NBT.TAG_COMPOUND);
		RectangularSolid solid = new RectangularSolid(xSize, ySize, zSize);
		int eStorage = 0;
		int eTransfer = 0;
		for (int tagCounter = 0; tagCounter < blockTagList.tagCount(); ++tagCounter){
			NBTTagCompound blockTagCompound = blockTagList.getCompoundTagAt(tagCounter);
			int index = blockTagCompound.getInteger("index");
			BlockPos p = solid.toCoord(index);
			if(parts[p.getX()][p.getY()][p.getZ()] == null){
				parts[p.getX()][p.getY()][p.getZ()] = ConstructPartRegistry.getNewPartFromNBT(this, blockTagCompound, solid);
			}else{
				String name = blockTagCompound.getString("name");
				if(parts[p.getX()][p.getY()][p.getZ()].getNameForNBT().equals(name)){
					parts[p.getX()][p.getY()][p.getZ()].readFromNBT(tag, solid);
				}
			}
			parts[p.getX()][p.getY()][p.getZ()].xCoord = p.getX();
			parts[p.getX()][p.getY()][p.getZ()].yCoord = p.getY();
			parts[p.getX()][p.getY()][p.getZ()].zCoord = p.getZ();
			if(parts[p.getX()][p.getY()][p.getZ()] instanceof EnergyProviderPart){
				eStorage += EnergyProviderPart.storage[parts[p.getX()][p.getY()][p.getZ()].grade];
				eTransfer = Math.max(eTransfer, EnergyProviderPart.transfer[parts[p.getX()][p.getY()][p.getZ()].grade]);
			}
		}
		if(eStorage != 0 && eTransfer != 0){
			energyStorage = new EnergyStorage(eStorage, eTransfer);
			energyStorage.readFromNBT(tag);
		}else{
			energyStorage = new EnergyStorage(0, 0);
		}

		NBTTagList interfaceTagList = tag.getTagList("interface", Constants.NBT.TAG_COMPOUND);
		for (int tagCounter = 0; tagCounter < interfaceTagList.tagCount(); ++tagCounter){
			NBTTagCompound interfaceTag = interfaceTagList.getCompoundTagAt(tagCounter);
			int side = interfaceTag.getInteger("side");
			int x = interfaceTag.getInteger("x");
			int y = interfaceTag.getInteger("y");
			int z = interfaceTag.getInteger("z");
			interfaceGrade[side] = interfaceTag.getInteger("grade");
			if(getPart(x, y, z) instanceof ISidedInventory){
				interfaceConnection[side] = new ConnectionEntry<ConstructPartBase>(getPart(x, y, z), x, y, z, -1);
			}else{
				interfaceConnection[side] = new ConnectionEntry<ConstructPartBase>(null, -1, -1, -1);
			}
		}

		contentUpdateMarked = new boolean[xSize][ySize][zSize];
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("connected", connected);
		tag.setBoolean("pause", isPausing);
		tag.setBoolean("renderDetail", renderDetail);

		//content
		tag.setInteger("xSize", xSize);
		tag.setInteger("ySize", ySize);
		tag.setInteger("zSize", zSize);

		NBTTagList blocksTagList = new NBTTagList();
		RectangularSolid solid = new RectangularSolid(xSize, ySize, zSize);
		for (int index = 0; index < solid.getIndexSize(); index++){
			BlockPos p = solid.toCoord(index);
			if(getPart(p.getX(), p.getY(), p.getZ()) != null){
				NBTTagCompound partTag = new NBTTagCompound();
				partTag.setInteger("index", index);
				partTag.setString("name", parts[p.getX()][p.getY()][p.getZ()].getNameForNBT());
				parts[p.getX()][p.getY()][p.getZ()].writeToNBT(partTag, solid);
				blocksTagList.appendTag(partTag);
			}
		}
		tag.setTag("blockList", blocksTagList);

		if(energyStorage != null){
			energyStorage.writeToNBT(tag);
		}

		NBTTagList interfaceTagList = new NBTTagList();
		for (int side = 0; side < 6; side++){
			if(interfaceConnection[side] != null){
				NBTTagCompound interfaceTag = new NBTTagCompound();
				interfaceTag.setInteger("side", side);
				interfaceTag.setInteger("x", interfaceConnection[side].x);
				interfaceTag.setInteger("y", interfaceConnection[side].y);
				interfaceTag.setInteger("z", interfaceConnection[side].z);
				interfaceTag.setInteger("grade", interfaceGrade[side]);
				interfaceTagList.appendTag(interfaceTag);
			}
		}
		tag.setTag("interface", interfaceTagList);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static MultiBlockCoreTileEntity createTileFromNBT(NBTTagCompound tag) {
		MultiBlockCoreTileEntity tile = new MultiBlockCoreTileEntity();
		tile.readFromNBT(tag);
		return tile;
	}


}
