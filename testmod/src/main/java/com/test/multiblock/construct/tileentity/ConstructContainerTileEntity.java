package com.test.multiblock.construct.tileentity;

import java.util.Random;

import com.test.main.TestCore;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.register.CrusherRecipeRegister;
import com.test.register.CrusherRecipeRegister.CrusherRecipe;
import com.test.register.EnergyProdeceRecipeRegister;
import com.test.register.EnergyProdeceRecipeRegister.EnergyProduceRecipe;
import com.test.register.VirtualGrowerRecipeRegister;
import com.test.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;
import com.test.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class ConstructContainerTileEntity extends ConstructInventoryBaseTileEntity implements ISignalReceiver {

	public static String nameForNBT = "container";

	public static final int NORMAL_MODE = 0;
	public static final int CRUSHER_MODE = 1;
	public static final int GROWER_MODE = 2;
	public static final int ENERGY_PROVIDER_MODE = 3;
	public static final int FURNACE_MODE = 4;

	/**0 : no connection 1 : crusher 2 : virtualglower*/
	public int mode = NORMAL_MODE;
	/**-1 : no connection**/
	public int connectDirection = -1;
	/**-1 : not started*/
	public int processingTicks = -1;

	private boolean waitForTransfer = false;

	public ConstructContainerTileEntity() {
		this(0);
	}

	public ConstructContainerTileEntity(int grade) {
		super(grade);
	}

	public boolean onRightClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			return !(this.items[0] == null && player.getHeldItem() == null);
		}else{
			if(this.items[0] != null){
				ItemStack content = this.getStackInSlot(0).copy();
				content.stackSize = 1;
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord + dir.offsetX * 0.5, yCoord + dir.offsetY * 0.5, zCoord + dir.offsetZ * 0.5, content));
				this.decrStackSize(0, 1);
				this.markDirty();
				return true;
			}else{
				if(this.isItemValidForSlot(0, player.getHeldItem())){
					ItemStack set = player.getHeldItem().copy();
					set.stackSize = 1;
					this.setInventorySlotContents(0, set);
					player.getHeldItem().stackSize -= 1;
					return true;
				}else{
					return false;
				}
			}
		}
	}

	@Override
	public void updateEntity() {
		if(needUpdateEntry){
			checkNeighberBlockConnection();
		}
		super.updateEntity();
		if(waitForTransfer){
			if(!worldObj.isRemote){
				if(this.mode == GROWER_MODE){
					itemTransfer(1);
				}else{
					itemTransfer(64);
				}
				if(this.getStackInSlot(0) == null) waitForTransfer = false;
			}else{
				waitForTransfer = false;
			}
		}else{
			if(mode == CRUSHER_MODE){
				crushProgress();
			}else if(mode == GROWER_MODE){
				growerProgress();
			}else if(mode == ENERGY_PROVIDER_MODE){
				produceEnergyProgress();
			}else if(mode == FURNACE_MODE){
				furnaceProgress();
			}
		}
	}

	public void checkNeighberBlockConnection() {
		if(this.mode == CRUSHER_MODE){
			checkExistingCrusherConnection();
		}else if(this.mode == GROWER_MODE){
			checkGrowerConnection();
		}else if(this.mode == ENERGY_PROVIDER_MODE){
			checkExistingProviderConnection();
		}else if(this.mode == FURNACE_MODE){
			checkFurnaceConnection();
		}
		if(this.mode == NORMAL_MODE){
			if(!checkNewCrusherConnection()){
				if(!checkGrowerConnection()){
					if(!checkNewProviderConnection()){
						if(!checkFurnaceConnection()){
							//other mode connection check
						}
					}
				}
			}
		}
		if(!worldObj.isRemote) TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "m" + mode + "" + connectDirection));
	}

	//crusher------------------------------------------------

	private ConstructCrusherTileEntity crusher1 = null;
	private ConstructCrusherTileEntity crusher2 = null;

	private boolean checkNewCrusherConnection() {
		mode = NORMAL_MODE;
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
							mode = CRUSHER_MODE;
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
			mode = NORMAL_MODE;
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
		mode = NORMAL_MODE;
		connectDirection = -1;
		processingTicks = -1;
		crusher1 = null;
		crusher2 = null;
	}

	/**called by crusher*/
	/**server only*/
	public void startCrush() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(readyToCrush() && mode == CRUSHER_MODE){
			if(processingTicks == -1){
				processingTicks = 0;
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
			}
		}
	}

	private void crushProgress() {
		//not started
		if(processingTicks == -1){
			return;
		}
		//stop
		if(!readyToCrush()){
			processingTicks = -1;
			if(!worldObj.isRemote) TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
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
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, CRUSHER_MODE));
					this.markDirty();
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
				}
			}
			processingTicks = -1;
		}
	}

	private boolean isConnectedToCrusher() {
		return !(connectDirection == -1 || crusher1 == null || crusher2 == null || crusher1.isInvalid() || crusher2.isInvalid());
	}
	private boolean readyToCrush() {
		return isConnectedToCrusher() && CrusherRecipeRegister.instance.findRecipe(items[0]) != null && crusher1.readyToCrush() && crusher2.readyToCrush();
	}

	public void spawnCrushedParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		String str = "";
		if(Block.getBlockFromItem(items[0].getItem()) != null){
			str = "blockcrack_" + Block.getIdFromBlock(Block.getBlockFromItem(items[0].getItem())) + "_" + items[0].getItemDamage();
		}else{
			str = "iconcrack_" + Item.getIdFromItem(items[0].getItem()) + "_" + items[0].getItemDamage();
		}
		Random random = worldObj.rand;
		double scale = 0.5D;
		int particleCount = (int) (150.0D * scale);
		for (int i2 = 0; i2 < particleCount; ++i2){
			float direction = MathHelper.randomFloatClamp(random, 0.0F, ((float) Math.PI * 2F));
			double speed = (double) MathHelper.randomFloatClamp(random, 0.75F, 1.0F);
			double vecY = 0.20000000298023224D + scale / 100.0D;
			double vecX = (double) (MathHelper.cos(direction) * 0.2F) * speed * speed * (scale + 0.2D);
			double vecZ = (double) (MathHelper.sin(direction) * 0.2F) * speed * speed * (scale + 0.2D);
			worldObj.spawnParticle(str, (double) ((float) xCoord + 0.5F), (double) ((float) yCoord + 0.2F), (double) ((float) zCoord + 0.5F), vecX, vecY, vecZ);
		}
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, Blocks.cactus.stepSound.getBreakSound(), 1.0F, 0.8F);
	}

	//virtual glower------------------------------------------------

	private ConstructVirtualGrowerTileEntity glower = null;
	private VirtualGrowerRecipe growerRecipe = null;

	private boolean checkGrowerConnection() {
		if(worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof ConstructVirtualGrowerTileEntity){
			glower = (ConstructVirtualGrowerTileEntity) worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
			if(glower.grade == this.grade){
				glower.container = this;
				mode = GROWER_MODE;
				return true;
			}
		}
		mode = NORMAL_MODE;
		processingTicks = -1;
		glower = null;
		return false;
	}

	/**called by grower*/
	/**server only*/
	public void startGrow() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(readyToGrow() && mode == GROWER_MODE){
			if(processingTicks == -1){
				processingTicks = 0;
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
			}
		}
	}

	private void growerProgress() {
		if(items[0] == null){
			this.growerRecipe = null;
		}
		//not started
		if(processingTicks == -1){
			return;
		}
		//stop
		if(!readyToGrow()){
			processingTicks = -1;
			if(!worldObj.isRemote) TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
			return;
		}
		if(growerRecipe == null) this.growerRecipe = VirtualGrowerRecipeRegister.instance.findRecipe(items[0]);
		//process
		if(worldObj.isRemote){
			spawnGrowingParticle();
		}
		processingTicks++;
		if(processingTicks >= growerRecipe.getTime(grade)){
			if(items[0] != null){
				if(!worldObj.isRemote){
					glower.doGrow();
					items[0].stackSize++;
					this.dispatchEventOnNextTick();
					this.markDirty();
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, GROWER_MODE));
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
				}
			}
			this.growerRecipe = null;
			processingTicks = -1;
		}
	}

	private boolean readyToGrow() {
		return glower != null && !glower.isInvalid() && glower.readyToGlow() && VirtualGrowerRecipeRegister.instance.findRecipe(items[0]) != null && items[0].stackSize == 1;
	}

	private void spawnGrowingParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(growerRecipe == null) return;
		if(processingTicks <= growerRecipe.getTime(grade) - 50){
			if(processingTicks % (int) ((growerRecipe.getTime(grade) / growerRecipe.energy * 4) + 1) == 0){
				Random rand = worldObj.rand;
				double angle1 = rand.nextDouble() * Math.PI;
				double angle2 = rand.nextDouble() * Math.PI * 2;
				double range = rand.nextDouble() + 1;
				double x = range * Math.sin(angle1) * Math.cos(angle2);
				double y = range * Math.sin(angle1) * Math.sin(angle2);
				double z = range * Math.cos(angle1);
				TestCore.spawnParticle(worldObj, TestCore.PARTICLE_GROWER, xCoord + 0.5, yCoord + 0.8, zCoord + 0.5, x, y, z);
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

	//energy provider-----------------------------------------------------------------------------

	private ConstructEnergyProviderTileEntity provider;
	private EnergyProduceRecipe energyProvideRecipe;

	private boolean checkNewProviderConnection() {
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof ConstructEnergyProviderTileEntity){
				provider = (ConstructEnergyProviderTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(provider.grade == this.grade){
					mode = ENERGY_PROVIDER_MODE;
					connectDirection = dir.ordinal();
					return true;
				}
			}
		}
		mode = NORMAL_MODE;
		connectDirection = -1;
		processingTicks = -1;
		provider = null;
		return false;
	}

	private boolean checkExistingProviderConnection() {
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof ConstructEnergyProviderTileEntity){
			provider = (ConstructEnergyProviderTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(provider.grade == this.grade){
				mode = ENERGY_PROVIDER_MODE;
				return true;
			}
		}
		mode = NORMAL_MODE;
		connectDirection = -1;
		processingTicks = -1;
		provider = null;
		return false;
	}

	/**called by provider*/
	/**server only*/
	public void startProduceEnergy() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(readyToProduceEnergy() && mode == ENERGY_PROVIDER_MODE){
			if(processingTicks == -1){
				processingTicks = 0;
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
			}
		}
	}

	private void produceEnergyProgress() {
		if(!worldObj.isRemote) startProduceEnergy();
		if(items[0] == null){
			this.energyProvideRecipe = null;
		}
		//not started
		if(processingTicks == -1){
			return;
		}
		//stop
		if(!readyToProduceEnergy()){
			processingTicks = -1;
			if(!worldObj.isRemote) TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
			return;
		}
		if(energyProvideRecipe == null) this.energyProvideRecipe = EnergyProdeceRecipeRegister.instance.findRecipe(items[0]);
		//process
		if(worldObj.isRemote){
			spawnEnergyProduceingParticle();
		}
		processingTicks++;
		if(processingTicks >= energyProvideRecipe.getTime(grade)){
			if(items[0] != null){
				if(!worldObj.isRemote){
					if(provider.receiveEnergy(ForgeDirection.getOrientation(connectDirection).getOpposite(), energyProvideRecipe.produceEnergy, true) == energyProvideRecipe.produceEnergy){
						provider.receiveEnergy(ForgeDirection.getOrientation(connectDirection).getOpposite(), energyProvideRecipe.produceEnergy, false);
						items[0].stackSize--;
						if(items[0].stackSize == 0) items[0] = null;
						this.dispatchEventOnNextTick();
						this.markDirty();
						TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, ENERGY_PROVIDER_MODE));
					}
					processingTicks = -1;
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
				}
				this.energyProvideRecipe = null;
			}
			processingTicks = -1;
		}
	}

	private boolean readyToProduceEnergy() {
		EnergyProduceRecipe recipe = EnergyProdeceRecipeRegister.instance.findRecipe(items[0]);
		return provider != null && !provider.isInvalid() && recipe != null && provider.receiveEnergy(ForgeDirection.getOrientation(connectDirection).getOpposite(), recipe.produceEnergy, true) == recipe.produceEnergy;
	}

	private void spawnEnergyProduceingParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(energyProvideRecipe == null) return;
		if(processingTicks % (int) ((energyProvideRecipe.getTime(grade) / energyProvideRecipe.produceEnergy) * 10 + 1) == 0){
			IIcon icon = null;
			if(Block.getBlockFromItem(items[0].getItem()) != null){
				icon = Block.getBlockFromItem(items[0].getItem()).getIcon((int) (Math.random() * 6), items[0].getItemDamage());
			}else{
				icon = items[0].getItem().getIcon(items[0], 0);
			}
			ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
			TestCore.spawnParticle(worldObj, TestCore.PARTICLE_CRUCK, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, (double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5, icon);
		}
	}

	private void spawnEnergyProducedParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(energyProvideRecipe == null) return;
		for (int i = 0; i < 10; i++){
			IIcon icon = null;
			if(Block.getBlockFromItem(items[0].getItem()) != null){
				icon = Block.getBlockFromItem(items[0].getItem()).getIcon((int) (Math.random() * 6), items[0].getItemDamage());
			}else{
				icon = items[0].getItem().getIcon(items[0], 0);
			}
			ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
			TestCore.spawnParticle(worldObj, TestCore.PARTICLE_CRUCK, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, (double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5, icon);
		}
	}

	//furnace-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public static final int[] furnaceSpeed = { 5, 10, 20, 40, 200 };
	public ConstructFurnaceTileEntity[] furnace = new ConstructFurnaceTileEntity[6];

	private boolean checkFurnaceConnection() {
		mode = NORMAL_MODE;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			if(worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof ConstructFurnaceTileEntity){
				ConstructFurnaceTileEntity tile = (ConstructFurnaceTileEntity) worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(tile.grade == this.grade){
					tile.container = this;
					furnace[dir.ordinal()] = tile;
					mode = FURNACE_MODE;
				}else{
					furnace[dir.ordinal()] = null;
				}
			}else{
				furnace[dir.ordinal()] = null;
			}
		}
		if(mode == FURNACE_MODE){
			return true;
		}else{
			for (ConstructFurnaceTileEntity tile : furnace){
				tile = null;
			}
			connectDirection = -1;
			processingTicks = -1;
			return false;
		}
	}

	/**called by furnace*/
	/**server only*/
	public void startFurnace() {
		if(worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(readyToFurnace() && mode == FURNACE_MODE){
			if(processingTicks == -1){
				processingTicks = 0;
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
			}
		}
	}

	private void furnaceProgress() {
		//not started
		if(processingTicks == -1){
			return;
		}
		//stop
		if(!readyToFurnace()){
			processingTicks = -1;
			if(!worldObj.isRemote) TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
			return;
		}
		//process
		if(worldObj.isRemote){
			spawnFurnacingParticle();
		}
		if(processingTicks < 200){
			processingTicks += getFurnaceSpeed();
		}else{
			if(items[0] != null){
				if(!worldObj.isRemote){
					this.items[0] = FurnaceRecipes.smelting().getSmeltingResult(this.items[0]).copy();
					for (ConstructFurnaceTileEntity tile : furnace){
						if(tile != null && !tile.isInvalid()){
							tile.doSmelt();
						}
					}
					processingTicks = -1;
					this.dispatchEventOnNextTick();
					this.markDirty();
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, FURNACE_MODE));
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + this.processingTicks));
				}
			}
			processingTicks = -1;
		}
	}

	private boolean readyToFurnace() {
		if(this.items[0] == null || this.items[0].stackSize != 1){
			return false;
		}else{
			ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.items[0]);
			if(itemstack == null) return false;
			for (ConstructFurnaceTileEntity tile : furnace){
				if(tile != null && !tile.isInvalid() && tile.readyToFurnace()) return true;
			}
			return false;
		}
	}

	private int getFurnaceSpeed() {
		int speed = 0;
		for (ConstructFurnaceTileEntity tile : furnace){
			if(tile != null && !tile.isInvalid() && tile.readyToFurnace()) speed += furnaceSpeed[tile.grade];
		}
		return speed;
	}

	private void spawnFurnacingParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int side = 0; side < 6; side++){
			ConstructFurnaceTileEntity tile = furnace[side];
			if(tile != null && !tile.isInvalid()){
				tile.spawnFurnacingParticle(ForgeDirection.OPPOSITES[side]);
			}
		}
	}

	private void spawnFurnacedParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		ForgeDirection dir = ForgeDirection.getOrientation((int) (Math.random() * 6));
		worldObj.spawnParticle("flame", xCoord + 0.5 + dir.offsetX * 0.2, yCoord + 0.5 + dir.offsetY * 0.2, zCoord + 0.5 + dir.offsetZ * 0.2, 0, 0, 0);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(worldObj.isRemote) return;
		this.waitForTransfer = true;
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
				}
			}
		}else if(type == PacketType.EFFECT && value instanceof Integer){
			int id = (Integer) value;
			if(id == CRUSHER_MODE){
				this.spawnCrushedParticle();
			}else if(id == GROWER_MODE){
				this.spawnGrowedParticle();
			}else if(id == ENERGY_PROVIDER_MODE){
				this.spawnEnergyProducedParticle();
			}else if(id == FURNACE_MODE){
				this.spawnFurnacedParticle();
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
	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		if(flagIO[side] == 0){
			if(this.mode == CRUSHER_MODE){
				return CrusherRecipeRegister.instance.findRecipe(item) != null;
			}else if(this.mode == GROWER_MODE){
				return VirtualGrowerRecipeRegister.instance.findRecipe(item) != null;
			}else if(this.mode == ENERGY_PROVIDER_MODE){
				return EnergyProdeceRecipeRegister.instance.findRecipe(item) != null;
			}else if(this.mode == FURNACE_MODE){
				if(item != null){
					return FurnaceRecipes.smelting().getSmeltingResult(item) != null;
				}else{
					return false;
				}
			}else{
				return true;
			}
		}else{
			return false;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		tag.setInteger("mode", mode);
		tag.setInteger("connectionDirection", connectDirection);
		tag.setInteger("processingTicks", processingTicks);
		tag.setBoolean("waitForTransfer", waitForTransfer);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.mode = tag.getInteger("mode");
		this.connectDirection = tag.getInteger("connectionDirection");
		this.processingTicks = tag.getInteger("processingTicks");
		this.waitForTransfer = tag.getBoolean("waitForTransfer");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("mode", mode);
		tag.setInteger("connectionDirection", connectDirection);
		tag.setInteger("processingTicks", processingTicks);
		tag.setBoolean("waitForTransfer", waitForTransfer);
	}

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

}
