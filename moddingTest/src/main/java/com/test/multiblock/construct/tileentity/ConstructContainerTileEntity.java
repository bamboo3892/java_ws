package com.test.multiblock.construct.tileentity;

import java.util.Random;

import com.test.main.TestCore;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.register.CrusherRecipeRegister;
import com.test.register.CrusherRecipeRegister.CrusherRecipe;
import com.test.register.VirtualGrowerRecipeRegister;
import com.test.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class ConstructContainerTileEntity extends ConstructInventoryBaseTileEntity implements ISidedInventory, ISignalReceiver {

	public static String nameForNBT = "container";

	/**0 : no connection 1 : crusher 2 : virtualglower*/
	public int mode = 0;
	/**-1 : no connection**/
	public int connectDirection = -1;
	/**-1 : not started*/
	public int processingTicks = -1;

	public ConstructContainerTileEntity() {
		this(0);
	}

	public ConstructContainerTileEntity(int grade) {
		super(grade);
	}

	public void onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote) return;
		if(this.doubled){
			ItemStack content = this.getStackInSlot(0).copy();
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, content));
			this.doubled = false;
			this.markDirty();
			TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "d" + (doubled ? 1 : 0)));
		}else if(this.items[0] != null){
			ItemStack content = this.getStackInSlot(0).copy();
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.0, zCoord + 0.5, content));
			this.decrStackSize(0, 1);
			this.markDirty();
		}else{
			if(this.isItemValidForSlot(0, player.getHeldItem())){
				ItemStack set = player.getHeldItem().copy();
				set.stackSize = 1;
				this.setInventorySlotContents(0, set);
				player.getHeldItem().stackSize -= 1;
			}
		}
	}

	@Override
	public void updateEntity() {
		if(needUpdateEntry){
			checkNeighberBlockConnection();
		}
		super.updateEntity();
		if(mode == 1){
			crushProgress();
		}else if(mode == 2){
			growerProgress();
		}
	}

	public void checkNeighberBlockConnection() {
		if(this.mode == 1){
			checkExistingCrusherConnection();
		}else if(this.mode == 2){
			checkGrowerConnection();
		}
		if(this.mode == 0){
			if(!checkNewCrusherConnection()){
				if(!checkGrowerConnection()){
					//other mode connection check
				}
			}
		}
		if(!worldObj.isRemote) TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "m" + mode + "" + connectDirection));
	}

	//crusher------------------------------------------------

	private ConstructCrusherTileEntity crusher1 = null;
	private ConstructCrusherTileEntity crusher2 = null;

	private boolean checkNewCrusherConnection() {
		mode = 0;
		connectDirection = -1;
		processingTicks = -1;
		crusher1 = null;
		crusher2 = null;
		for (int i = 1; i < 6;){
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof ConstructCrusherTileEntity){
				crusher2 = (ConstructCrusherTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(crusher2.getBlockMetadata() == dir.getOpposite().ordinal() && crusher2.grade == this.grade){
					dir = dir.getOpposite();
					if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof ConstructCrusherTileEntity){
						crusher1 = (ConstructCrusherTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
						if(crusher1.getBlockMetadata() == dir.getOpposite().ordinal() && crusher2.grade == this.grade){
							mode = 1;
							connectDirection = dir.ordinal();
							crusher1.container = this;
							crusher2.container = this;
							return true;
						}
					}
				}
			}
			i += 2;
		}
		return false;
	}

	private void checkExistingCrusherConnection() {
		if(!isConnectedToCrusher()){
			mode = 0;
			connectDirection = -1;
			processingTicks = -1;
			crusher1 = null;
			crusher2 = null;
			return;
		}
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) == crusher1){
			if(crusher1.getBlockMetadata() == dir.getOpposite().ordinal() && crusher2.grade == this.grade){
				dir = dir.getOpposite();
				if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) == crusher2){
					if(crusher2.getBlockMetadata() == dir.getOpposite().ordinal() && crusher2.grade == this.grade){
						return;
					}
				}
			}
		}
		mode = 0;
		connectDirection = -1;
		processingTicks = -1;
		crusher1 = null;
		crusher2 = null;
	}

	/**called by crusher*/
	/**server only*/
	public void startCrush() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(readyToCrash()){
			if(processingTicks == -1){
				processingTicks = 0;
				TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "t" + this.processingTicks));
			}
		}
	}

	private void crushProgress() {
		//not started
		if(processingTicks == -1){
			return;
		}
		//stop
		if(!readyToCrash()){
			processingTicks = -1;
			if(!worldObj.isRemote) TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "t" + this.processingTicks));
			return;
		}
		//process
		if(worldObj.isRemote){
			crusher1.spawnCrushingParticle();
			crusher2.spawnCrushingParticle();
		}
		processingTicks++;
		if(processingTicks >= 10){
			CrusherRecipe recipe = CrusherRecipeRegister.instance.findRecipe(items[0]);
			if(recipe != null){
				if(!worldObj.isRemote){
					crusher1.doCrash();
					crusher2.doCrash();
					items[0] = recipe.product;
					dispatchEventOnNextTick();
					this.markDirty();
					TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.EFFECT, 0));
					TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "t" + this.processingTicks));
				}
			}
			processingTicks = -1;
		}
	}

	private boolean isConnectedToCrusher() {
		return !(connectDirection == -1 || crusher1 == null || crusher2 == null || crusher1.isInvalid() || crusher2.isInvalid());
	}
	private boolean readyToCrash() {
		return isConnectedToCrusher() && CrusherRecipeRegister.instance.findRecipe(items[0]) != null && crusher1.readyToCrush() && crusher2.readyToCrush();
	}

	public void spawnCrushedParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		Random random = worldObj.rand;
		double d3 = 0.5D;
		int l1 = (int) (150.0D * d3);
		for (int i2 = 0; i2 < l1; ++i2){
			float f3 = MathHelper.randomFloatClamp(random, 0.0F, ((float) Math.PI * 2F));
			double d5 = (double) MathHelper.randomFloatClamp(random, 0.75F, 1.0F);
			double d6 = 0.20000000298023224D + d3 / 100.0D;
			double d7 = (double) (MathHelper.cos(f3) * 0.2F) * d5 * d5 * (d3 + 0.2D);
			double d8 = (double) (MathHelper.sin(f3) * 0.2F) * d5 * d5 * (d3 + 0.2D);
			String str = "iconcrack_" + Item.getIdFromItem(items[0].getItem()) + "_" + items[0].getItemDamage();
			worldObj.spawnParticle(str, (double) ((float) xCoord + 0.5F), (double) ((float) yCoord + 0.2F), (double) ((float) zCoord + 0.5F), d7, d6, d8);
		}
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, Blocks.cactus.stepSound.getBreakSound(), 1.0F, 0.8F);
	}

	//virtual glower------------------------------------------------

	private ConstructVirtualGrowerTileEntity glower = null;
	private VirtualGrowerRecipe recipe = null;
	public boolean doubled = false;

	private boolean checkGrowerConnection() {
		if(worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof ConstructVirtualGrowerTileEntity){
			glower = (ConstructVirtualGrowerTileEntity) worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
			if(glower.grade == this.grade){
				glower.container = this;
				mode = 2;
				return true;
			}
		}
		mode = 0;
		processingTicks = -1;
		glower = null;
		doubled = false;
		return false;
	}

	/**called by grower*/
	/**server only*/
	public void startGrow() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(readyToGrow()){
			if(processingTicks == -1){
				processingTicks = 0;
				TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "t" + this.processingTicks));
			}
		}
	}

	private void growerProgress() {
		if(items[0] == null){
			this.recipe = null;
			doubled = false;
		}
		//not started
		if(processingTicks == -1){
			return;
		}
		//stop
		if(!readyToGrow()){
			processingTicks = -1;
			if(!worldObj.isRemote) TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "t" + this.processingTicks));
			return;
		}
		if(recipe == null) this.recipe = VirtualGrowerRecipeRegister.instance.findRecipe(items[0]);
		//process
		if(worldObj.isRemote){
			spawnGrowingParticle();
		}
		processingTicks++;
		if(processingTicks >= recipe.time){
			if(items[0] != null){
				if(!worldObj.isRemote){
					glower.doGrow();
					doubled = true;
					this.markDirty();
					TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.EFFECT, 1));
					TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "t" + this.processingTicks));
					TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "d" + (doubled ? 1 : 0)));
				}
			}
			this.recipe = null;
			processingTicks = -1;
		}
	}

	private boolean readyToGrow() {
		return glower != null && !glower.isInvalid() && glower.readyToGlow() && VirtualGrowerRecipeRegister.instance.findRecipe(items[0]) != null && !doubled;
	}

	private void spawnGrowingParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(recipe == null) return;
		if(processingTicks <= recipe.time - 50){
			if(processingTicks % (int) ((recipe.time / recipe.energy * 4) + 1) == 0){
				Random rand = worldObj.rand;
				double angle1 = rand.nextDouble() * Math.PI;
				double angle2 = rand.nextDouble() * Math.PI * 2;
				double range = rand.nextDouble() + 1;
				double x = range * Math.sin(angle1) * Math.cos(angle2);
				double y = range * Math.sin(angle1) * Math.sin(angle2);
				double z = range * Math.cos(angle1);
				TestCore.proxy.spawnParticle(worldObj, 0, xCoord + 0.5, yCoord + 0.8, zCoord + 0.5, x, y, z);
			}
		}
	}

	private void spawnGrowedParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 4; i++){
			Random rand = worldObj.rand;
			double angle = rand.nextDouble() * Math.PI * 2;
			double range = rand.nextDouble() * 0.4 + 0.4;
			double x = xCoord + 0.5 + range * Math.cos(angle);
			double y = yCoord + 0.5 + rand.nextDouble() * 0.1;
			double z = zCoord + 0.5 + range * Math.sin(angle);
			//Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTest2(worldObj, xCoord + 0.5, yCoord + 0.8, zCoord + 0.5, -x, -y, -z));
			worldObj.spawnParticle("happyVillager", x, y, z, 0, 0, 0);
		}
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, Blocks.cactus.stepSound.getBreakSound(), 1.0F, 0.8F);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(worldObj.isRemote) return;
		ItemStack item = null;
		if(items[0] != null) item = items[0].copy();
		if(itemTransfer()){
			if(doubled){
				items[0] = item;
				doubled = false;
				TestCore.packetDispatcher.sendToAll(new SimpleTilePacket(xCoord, yCoord, zCoord, PacketType.OTHER, "d" + (doubled ? 1 : 0)));
			}
		}
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.OTHER && value instanceof String){
			String str = (String) value;
			if(str.length() >= 1){
				if(str.charAt(0) == 'm'){
					this.mode = Character.getNumericValue(str.charAt(1));
					this.connectDirection = Character.getNumericValue(str.charAt(2));
					worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
				}else if(str.charAt(0) == 't'){
					str = str.substring(1);
					this.processingTicks = Integer.decode(str);
				}else if(str.charAt(0) == 'd'){
					this.doubled = Character.getNumericValue(str.charAt(1)) == 1;
				}
			}
		}else if(type == PacketType.EFFECT && value instanceof Integer){
			int id = (Integer) value;
			if(id == 0){
				this.spawnCrushedParticle();
			}else if(id == 1){
				this.spawnGrowedParticle();
			}
		}
	}

	@Override
	protected Class<IConstructInventory> getTargetClass() {
		return IConstructInventory.class;
	}
	@Override
	protected boolean shouldDistinguishSide() {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? items[0] : null;
	}
	@Override
	public ItemStack decrStackSize(int slot, int maxAmount) {
		if(slot == 0){
			ItemStack removed = items[0];
			items[0] = null;
			this.markDirty();
			return removed;
		}
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		items[0] = itemStack;
		this.markDirty();
	}
	@Override
	public String getInventoryName() {
		return "container.cotainer";
	}
	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return false;
	}
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return slot == 0 && item != null;
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0 };
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.mode = tag.getInteger("mode");
		this.connectDirection = tag.getInteger("connectionDirection");
		this.doubled = tag.getBoolean("doubled");
		this.processingTicks = tag.getInteger("processingTicks");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("mode", mode);
		tag.setInteger("connectionDirection", connectDirection);
		tag.setBoolean("doubled", doubled);
		tag.setInteger("processingTicks", processingTicks);
	}

}
