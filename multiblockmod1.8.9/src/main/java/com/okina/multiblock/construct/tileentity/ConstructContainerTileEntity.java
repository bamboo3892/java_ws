package com.okina.multiblock.construct.tileentity;

import java.util.Random;

import com.okina.main.TestCore;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.register.CrusherRecipeRegister;
import com.okina.register.CrusherRecipeRegister.CrusherRecipe;
import com.okina.register.EnergyProdeceRecipeRegister;
import com.okina.register.EnergyProdeceRecipeRegister.EnergyProduceRecipe;
import com.okina.register.VirtualGrowerRecipeRegister;
import com.okina.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

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
	public EnumFacing connectDirection = null;
	/**-1 : not started*/
	public int processingTicks = -1;

	private boolean waitForTransfer = false;

	public ConstructContainerTileEntity() {
		this(0);
	}

	public ConstructContainerTileEntity(int grade) {
		super(grade);
	}

	@Override
	public boolean onRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(worldObj.isRemote){
			return !(items[0] == null && player.getHeldItem() == null);
		}else{
			if(items[0] != null){
				ItemStack content = getStackInSlot(0).copy();
				content.stackSize = 1;
				worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + side.getFrontOffsetX() * 0.5, pos.getY() + side.getFrontOffsetY() * 0.5, pos.getZ() + side.getFrontOffsetZ() * 0.5, content));
				decrStackSize(0, 1);
				markDirty();
				return true;
			}else{
				if(isItemValidForSlot(0, player.getHeldItem())){
					ItemStack set = player.getHeldItem().copy();
					set.stackSize = 1;
					setInventorySlotContents(0, set);
					player.getHeldItem().stackSize -= 1;
					return true;
				}else{
					return false;
				}
			}
		}
	}
	@Override
	public boolean onShiftRightClicked(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}
	@Override
	public boolean onRightClickedByWrench(IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public void update() {
		if(needUpdateEntry){
			checkNeighberBlockConnection();
		}
		super.update();
		if(waitForTransfer){
			if(!worldObj.isRemote){
				if(mode == GROWER_MODE){
					itemTransfer(1);
					if(getStackInSlot(0) == null || getStackInSlot(0).stackSize == 1) waitForTransfer = false;
				}else{
					itemTransfer(64);
				}
				if(getStackInSlot(0) == null) waitForTransfer = false;
			}else{
				waitForTransfer = false;
			}
		}
		if(!waitForTransfer){
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
		if(mode == CRUSHER_MODE){
			checkExistingCrusherConnection();
		}else if(mode == GROWER_MODE){
			checkGrowerConnection();
		}else if(mode == ENERGY_PROVIDER_MODE){
			checkExistingProviderConnection();
		}else if(mode == FURNACE_MODE){
			checkFurnaceConnection();
		}
		if(mode == NORMAL_MODE){
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
		connectDirection = null;
		processingTicks = -1;
		crusher1 = null;
		crusher2 = null;
		for (int i = 0; i < 6; i++){
			EnumFacing side = EnumFacing.getFront(i);
			if(worldObj.getTileEntity(pos.add(side.getDirectionVec())) instanceof ConstructCrusherTileEntity){
				crusher2 = (ConstructCrusherTileEntity) worldObj.getTileEntity(pos.add(side.getDirectionVec()));
				if(crusher2.getBlockMetadata() == side.getOpposite().getIndex() && crusher2.grade == grade){
					side = side.getOpposite();
					if(worldObj.getTileEntity(pos.add(side.getDirectionVec())) instanceof ConstructCrusherTileEntity){
						crusher1 = (ConstructCrusherTileEntity) worldObj.getTileEntity(pos.add(side.getDirectionVec()));
						if(crusher1.getBlockMetadata() == side.getOpposite().getIndex() && crusher2.grade == grade){
							mode = CRUSHER_MODE;
							connectDirection = side;
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
			connectDirection = null;
			processingTicks = -1;
			crusher1 = null;
			crusher2 = null;
			return;
		}
		if(worldObj.getTileEntity(pos.add(connectDirection.getDirectionVec())) == crusher1){
			if(crusher1.getBlockMetadata() == connectDirection.getOpposite().getIndex() && crusher2.grade == grade){
				EnumFacing dir2 = connectDirection.getOpposite();
				if(worldObj.getTileEntity(pos.add(connectDirection.getDirectionVec())) == crusher2){
					if(crusher2.getBlockMetadata() == dir2.getOpposite().getIndex() && crusher2.grade == grade){
						return;
					}
				}
			}
		}
		mode = NORMAL_MODE;
		connectDirection = null;
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
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
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
			if(!worldObj.isRemote) TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
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
					items[0] = recipe.getProduct();
					dispatchEventOnNextTick();
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, CRUSHER_MODE));
					markDirty();
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
				}
			}
			processingTicks = -1;
		}
	}

	private boolean isConnectedToCrusher() {
		return !(connectDirection == null || crusher1 == null || crusher2 == null || crusher1.isInvalid() || crusher2.isInvalid());
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
			double speed = MathHelper.randomFloatClamp(random, 0.75F, 1.0F);
			double vecY = 0.20000000298023224D + scale / 100.0D;
			double vecX = MathHelper.cos(direction) * 0.2F * speed * speed * (scale + 0.2D);
			double vecZ = MathHelper.sin(direction) * 0.2F * speed * speed * (scale + 0.2D);
			//			worldObj.spawnParticle(str, (double) ((float) xCoord + 0.5F), (double) ((float) yCoord + 0.2F), (double) ((float) zCoord + 0.5F), vecX, vecY, vecZ);
		}
		worldObj.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Blocks.cactus.stepSound.getBreakSound(), 1.0F, 0.8F);
	}

	//virtual glower------------------------------------------------

	private ConstructVirtualGrowerTileEntity glower = null;
	private VirtualGrowerRecipe growerRecipe = null;

	private boolean checkGrowerConnection() {
		if(worldObj.getTileEntity(pos.down()) instanceof ConstructVirtualGrowerTileEntity){
			glower = (ConstructVirtualGrowerTileEntity) worldObj.getTileEntity(pos.down());
			if(glower.grade == grade){
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
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
			}
		}
	}

	private void growerProgress() {
		if(items[0] == null){
			growerRecipe = null;
		}
		//not started
		if(processingTicks == -1){
			return;
		}
		//stop
		if(!readyToGrow()){
			processingTicks = -1;
			if(!worldObj.isRemote) TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
			return;
		}
		if(growerRecipe == null) growerRecipe = VirtualGrowerRecipeRegister.instance.findRecipe(items[0]);
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
					dispatchEventOnNextTick();
					markDirty();
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, GROWER_MODE));
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
				}
			}
			growerRecipe = null;
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
			if(processingTicks % ((growerRecipe.getTime(grade) / growerRecipe.energy * 4) + 1) == 0){
				Random rand = worldObj.rand;
				double angle1 = rand.nextDouble() * Math.PI;
				double angle2 = rand.nextDouble() * Math.PI * 2;
				double range = rand.nextDouble() + 1;
				double x = range * Math.sin(angle1) * Math.cos(angle2);
				double y = range * Math.sin(angle1) * Math.sin(angle2);
				double z = range * Math.cos(angle1);
				TestCore.spawnParticle(worldObj, TestCore.PARTICLE_GROWER, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, x, y, z);
			}
		}
	}

	private void spawnGrowedParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		for (int i = 0; i < 4; i++){
			Random rand = worldObj.rand;
			double angle = rand.nextDouble() * Math.PI * 2;
			double range = rand.nextDouble() * 0.4 + 0.4;
			double x = pos.getX() + 0.5 + range * Math.cos(angle);
			double y = pos.getY() + 0.5 + rand.nextDouble() * 0.1;
			double z = pos.getZ() + 0.5 + range * Math.sin(angle);
			//Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTest2(worldObj, xCoord + 0.5, yCoord + 0.8, zCoord + 0.5, -x, -y, -z));
			worldObj.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, x, y, z, 0, 0, 0);
		}
		worldObj.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Blocks.cactus.stepSound.getBreakSound(), 1.0F, 0.8F);
	}

	//energy provider-----------------------------------------------------------------------------

	private ConstructEnergyProviderTileEntity provider;
	private EnergyProduceRecipe energyProvideRecipe;

	private boolean checkNewProviderConnection() {
		for (EnumFacing dir : EnumFacing.VALUES){
			if(worldObj.getTileEntity(pos.add(dir.getDirectionVec())) instanceof ConstructEnergyProviderTileEntity){
				provider = (ConstructEnergyProviderTileEntity) worldObj.getTileEntity(pos.add(dir.getDirectionVec()));
				if(provider.grade == grade){
					mode = ENERGY_PROVIDER_MODE;
					connectDirection = dir;
					return true;
				}
			}
		}
		mode = NORMAL_MODE;
		connectDirection = null;
		processingTicks = -1;
		provider = null;
		return false;
	}

	private boolean checkExistingProviderConnection() {
		if(worldObj.getTileEntity(pos.add(connectDirection.getDirectionVec())) instanceof ConstructEnergyProviderTileEntity){
			provider = (ConstructEnergyProviderTileEntity) worldObj.getTileEntity(pos.add(connectDirection.getDirectionVec()));
			if(provider.grade == grade){
				mode = ENERGY_PROVIDER_MODE;
				return true;
			}
		}
		mode = NORMAL_MODE;
		connectDirection = null;
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
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
			}
		}
	}

	private void produceEnergyProgress() {
		if(!worldObj.isRemote) startProduceEnergy();
		if(items[0] == null){
			energyProvideRecipe = null;
		}
		//not started
		if(processingTicks == -1){
			return;
		}
		//stop
		if(!readyToProduceEnergy()){
			processingTicks = -1;
			if(!worldObj.isRemote) TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
			return;
		}
		if(energyProvideRecipe == null) energyProvideRecipe = EnergyProdeceRecipeRegister.instance.findRecipe(items[0]);
		//process
		if(worldObj.isRemote){
			spawnEnergyProduceingParticle();
		}
		processingTicks++;
		if(processingTicks >= energyProvideRecipe.getTime(grade)){
			if(items[0] != null){
				if(!worldObj.isRemote){
					if(provider.receiveEnergy(connectDirection.getOpposite(), energyProvideRecipe.produceEnergy, true) == energyProvideRecipe.produceEnergy){
						provider.receiveEnergy(connectDirection.getOpposite(), energyProvideRecipe.produceEnergy, false);
						items[0].stackSize--;
						if(items[0].stackSize == 0) items[0] = null;
						dispatchEventOnNextTick();
						markDirty();
						TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, ENERGY_PROVIDER_MODE));
					}
					processingTicks = -1;
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
				}
				energyProvideRecipe = null;
			}
			processingTicks = -1;
		}
	}

	private boolean readyToProduceEnergy() {
		EnergyProduceRecipe recipe = EnergyProdeceRecipeRegister.instance.findRecipe(items[0]);
		return provider != null && !provider.isInvalid() && recipe != null && provider.receiveEnergy(connectDirection.getOpposite(), recipe.produceEnergy, true) == recipe.produceEnergy;
	}

	private void spawnEnergyProduceingParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(energyProvideRecipe == null) return;
		if(processingTicks % ((energyProvideRecipe.getTime(grade) / energyProvideRecipe.produceEnergy) * 10 + 1) == 0){
			//			IIcon icon = null;
			//			if(Block.getBlockFromItem(items[0].getItem()) != null){
			//				icon = Block.getBlockFromItem(items[0].getItem()).getIcon((int) (Math.random() * 6), items[0].getItemDamage());
			//			}else{
			//				icon = items[0].getItem().getIcon(items[0], 0);
			//			}
			//			ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
			//			TestCore.spawnParticle(worldObj, TestCore.PARTICLE_CRUCK, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, (double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5, icon);
		}
	}

	private void spawnEnergyProducedParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		if(energyProvideRecipe == null) return;
		//		for (int i = 0; i < 10; i++){
		//			IIcon icon = null;
		//			if(Block.getBlockFromItem(items[0].getItem()) != null){
		//				icon = Block.getBlockFromItem(items[0].getItem()).getIcon((int) (Math.random() * 6), items[0].getItemDamage());
		//			}else{
		//				icon = items[0].getItem().getIcon(items[0], 0);
		//			}
		//			ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		//			TestCore.spawnParticle(worldObj, TestCore.PARTICLE_CRUCK, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, (double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5, icon);
		//		}
	}

	//furnace-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public static final int[] furnaceSpeed = { 5, 10, 20, 40, 200 };
	public ConstructFurnaceTileEntity[] furnace = new ConstructFurnaceTileEntity[6];

	private boolean checkFurnaceConnection() {
		mode = NORMAL_MODE;
		for (EnumFacing dir : EnumFacing.VALUES){
			if(worldObj.getTileEntity(pos.add(dir.getDirectionVec())) instanceof ConstructFurnaceTileEntity){
				ConstructFurnaceTileEntity tile = (ConstructFurnaceTileEntity) worldObj.getTileEntity(pos.add(dir.getDirectionVec()));
				if(tile.grade == grade){
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
			connectDirection = null;
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
				TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
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
			if(!worldObj.isRemote) TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
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
					items[0] = FurnaceRecipes.instance().getSmeltingResult(items[0]).copy();
					for (ConstructFurnaceTileEntity tile : furnace){
						if(tile != null && !tile.isInvalid()){
							tile.doSmelt();
						}
					}
					processingTicks = -1;
					dispatchEventOnNextTick();
					markDirty();
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.EFFECT, FURNACE_MODE));
					TestCore.proxy.sendPacket(new SimpleTilePacket(this, PacketType.OTHER, "t" + processingTicks));
				}
			}
			processingTicks = -1;
		}
	}

	private boolean readyToFurnace() {
		if(items[0] == null || items[0].stackSize != 1){
			return false;
		}else{
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(items[0]);
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
				tile.spawnFurnacingParticle(EnumFacing.getFront(side).getOpposite());
			}
		}
	}

	private void spawnFurnacedParticle() {
		if(!worldObj.isRemote) throw new NullPointerException("called on invalid side : " + (worldObj.isRemote ? "Client" : "Server"));
		EnumFacing dir = EnumFacing.random(worldObj.rand);
		worldObj.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + 0.5 + dir.getFrontOffsetX() * 0.2, pos.getY() + 0.5 + dir.getFrontOffsetY() * 0.2, pos.getZ() + 0.5 + dir.getFrontOffsetZ() * 0.2, 0, 0, 0);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		if(worldObj.isRemote) return;
		waitForTransfer = true;
	}

	@Override
	public void processCommand(PacketType type, Object value) {
		super.processCommand(type, value);
		if(type == PacketType.OTHER && value instanceof String){
			String str = (String) value;
			if(str.length() >= 1){
				if(str.charAt(0) == 'm'){
					mode = Character.getNumericValue(str.charAt(1));
					connectDirection = EnumFacing.getFront(Character.getNumericValue(str.charAt(2)));
					worldObj.markBlockRangeForRenderUpdate(pos, pos);
				}else if(str.charAt(0) == 't'){
					str = str.substring(1);
					processingTicks = Integer.decode(str);
				}
			}
		}else if(type == PacketType.EFFECT && value instanceof Integer){
			int id = (Integer) value;
			if(id == CRUSHER_MODE){
				spawnCrushedParticle();
			}else if(id == GROWER_MODE){
				spawnGrowedParticle();
			}else if(id == ENERGY_PROVIDER_MODE){
				spawnEnergyProducedParticle();
			}else if(id == FURNACE_MODE){
				spawnFurnacedParticle();
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
	public int getInventoryStackLimit() {
		return 1;
	}
	@Override
	public int getSizeInventory() {
		return 1;
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
		return "Container";
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}
	@Override
	public void openInventory(EntityPlayer player) {}
	@Override
	public void closeInventory(EntityPlayer player) {}
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return slot == 0 && item != null;
	}
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0 };
	}
	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		if(flagIO[side.getIndex()] == 0){
			if(mode == CRUSHER_MODE){
				return CrusherRecipeRegister.instance.findRecipe(item) != null;
			}else if(mode == GROWER_MODE){
				return VirtualGrowerRecipeRegister.instance.findRecipe(item) != null;
			}else if(mode == ENERGY_PROVIDER_MODE){
				return EnergyProdeceRecipeRegister.instance.findRecipe(item) != null;
			}else if(mode == FURNACE_MODE){
				if(item != null){
					return FurnaceRecipes.instance().getSmeltingResult(item) != null;
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void writeDetailToNBTForItemStack(NBTTagCompound tag, RectangularSolid solid) {
		super.writeDetailToNBTForItemStack(tag, solid);
		tag.setInteger("mode", mode);
		tag.setInteger("connectionDirection", connectDirection == null ? -1 : connectDirection.getIndex());
		tag.setInteger("processingTicks", processingTicks);
		tag.setBoolean("waitForTransfer", waitForTransfer);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		mode = tag.getInteger("mode");
		int dir = tag.getInteger("connectionDirection");
		connectDirection = dir == -1 ? null : EnumFacing.getFront(dir);
		processingTicks = tag.getInteger("processingTicks");
		waitForTransfer = tag.getBoolean("waitForTransfer");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("mode", mode);
		tag.setInteger("connectionDirection", connectDirection == null ? -1 : connectDirection.getIndex());
		tag.setInteger("processingTicks", processingTicks);
		tag.setBoolean("waitForTransfer", waitForTransfer);
	}

	@Override
	public String getNameForNBT() {
		return nameForNBT;
	}

}
