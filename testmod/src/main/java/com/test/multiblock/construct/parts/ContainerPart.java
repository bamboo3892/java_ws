package com.test.multiblock.construct.parts;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.test.client.gui.ConstructContainerGui;
import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;
import com.test.multiblock.construct.tileentity.ISignalReceiver;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.register.CrusherRecipeRegister;
import com.test.register.CrusherRecipeRegister.CrusherRecipe;
import com.test.register.EnergyProdeceRecipeRegister;
import com.test.register.EnergyProdeceRecipeRegister.EnergyProduceRecipe;
import com.test.register.VirtualGrowerRecipeRegister;
import com.test.register.VirtualGrowerRecipeRegister.VirtualGrowerRecipe;
import com.test.server.gui.ConstructContainerContainer;
import com.test.utils.RectangularSolid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class ContainerPart extends InventoryPartBase implements ISignalReceiver {

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

	public ContainerPart() {

	}

	@Override
	public boolean isOpenGuiOnClicked() {
		return true;
	}

	@Override
	public Object getGuiElement(EntityPlayer player, boolean serverSide) {
		if(serverSide){
			return new ConstructContainerContainer(player.inventory, this);
		}else{
			return new ConstructContainerGui(player.inventory, this);
		}
	}

	@Override
	public void updatePart() {
		if(needUpdateEntry){
			checkNeighberBlockConnection();
		}
		super.updatePart();
		if(waitForTransfer){
			if(this.mode == GROWER_MODE){
				itemTransfer(1);
			}else{
				itemTransfer(64);
			}
			if(this.getStackInSlot(0) == null) waitForTransfer = false;
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
			checkCrusherConnection();
		}else if(this.mode == GROWER_MODE){
			checkGrowerConnection();
		}else if(this.mode == ENERGY_PROVIDER_MODE){
			checkProviderConnection();
		}else if(this.mode == FURNACE_MODE){
			checkFurnaceConnection();
		}
	}

	//crusher------------------------------------------------

	private CrusherPart crusher1 = null;
	private CrusherPart crusher2 = null;

	private boolean checkCrusherConnection() {
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(coreTile.getPart(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof CrusherPart){
			crusher2 = (CrusherPart) coreTile.getPart(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if(crusher2.direction == dir.getOpposite().ordinal() && crusher2.grade == this.grade){
				dir = dir.getOpposite();
				if(coreTile.getPart(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof CrusherPart){
					crusher1 = (CrusherPart) coreTile.getPart(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
					if(crusher1.direction == dir.getOpposite().ordinal() && crusher2.grade == this.grade){
						mode = CRUSHER_MODE;
						crusher1.container = this;
						crusher2.container = this;
						return true;
					}
				}
			}
		}
		mode = NORMAL_MODE;
		connectDirection = -1;
		processingTicks = -1;
		crusher1 = null;
		crusher2 = null;
		return false;
	}

	/**called by crusher*/
	/**server only*/
	public void startCrush() {
		if(readyToCrash() && mode == CRUSHER_MODE){
			if(processingTicks == -1){
				processingTicks = 0;
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
			return;
		}
		//process
		processingTicks++;
		if(processingTicks >= 10){
			CrusherRecipe recipe = CrusherRecipeRegister.instance.findRecipe(items[0]);
			if(recipe != null){
				crusher1.doCrash();
				crusher2.doCrash();
				items[0] = recipe.product;
				dispatchEventOnNextTick();
				this.markDirty();
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("id", CRUSHER_MODE);
				coreTile.sendPacket(PacketType.EFFECT, xCoord, yCoord, zCoord, tag);
			}
			processingTicks = -1;
		}
	}

	private boolean isConnectedToCrusher() {
		return !(connectDirection == -1 || crusher1 == null || crusher2 == null);
	}
	private boolean readyToCrash() {
		return isConnectedToCrusher() && CrusherRecipeRegister.instance.findRecipe(items[0]) != null && crusher1.readyToCrush() && crusher2.readyToCrush();
	}

	//virtual glower------------------------------------------------

	private VirtualGlowerPart glower = null;
	private VirtualGrowerRecipe recipe = null;

	private boolean checkGrowerConnection() {
		if(coreTile.getPart(xCoord, yCoord - 1, zCoord) instanceof VirtualGlowerPart){
			glower = (VirtualGlowerPart) coreTile.getPart(xCoord, yCoord - 1, zCoord);
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
		if(readyToGrow() && mode == GROWER_MODE){
			if(processingTicks == -1){
				processingTicks = 0;
			}
		}
	}

	private void growerProgress() {
		if(items[0] == null){
			this.recipe = null;
		}
		//not started
		if(processingTicks == -1){
			return;
		}
		//stop
		if(!readyToGrow()){
			processingTicks = -1;
			return;
		}
		if(recipe == null) this.recipe = VirtualGrowerRecipeRegister.instance.findRecipe(items[0]);
		//process
		processingTicks++;
		if(processingTicks >= recipe.getTime(grade)){
			if(items[0] != null){
				glower.doGrow();
				items[0].stackSize++;
				this.dispatchEventOnNextTick();
				this.markDirty();
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("id", GROWER_MODE);
				coreTile.sendPacket(PacketType.EFFECT, xCoord, yCoord, zCoord, tag);
			}
			this.recipe = null;
			processingTicks = -1;
		}
	}

	private boolean readyToGrow() {
		return glower != null && glower.readyToGlow() && VirtualGrowerRecipeRegister.instance.findRecipe(items[0]) != null && items[0].stackSize == 1;
	}

	//energy provider-----------------------------------------------------------------------------

	private EnergyProviderPart provider;
	private EnergyProduceRecipe energyProvideRecipe;

	private boolean checkProviderConnection() {
		ForgeDirection dir = ForgeDirection.getOrientation(connectDirection);
		if(coreTile.getPart(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof EnergyProviderPart){
			provider = (EnergyProviderPart) coreTile.getPart(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
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
		if(readyToProduceEnergy() && mode == ENERGY_PROVIDER_MODE){
			if(processingTicks == -1){
				processingTicks = 0;
			}
		}
	}

	private void produceEnergyProgress() {
		startProduceEnergy();
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
			return;
		}
		if(energyProvideRecipe == null) this.energyProvideRecipe = EnergyProdeceRecipeRegister.instance.findRecipe(items[0]);
		//process
		processingTicks++;
		if(processingTicks >= energyProvideRecipe.getTime(grade)){
			if(items[0] != null){
				if(coreTile.receiveEnergy(ForgeDirection.getOrientation(connectDirection).getOpposite(), energyProvideRecipe.produceEnergy, true) == energyProvideRecipe.produceEnergy){
					coreTile.receiveEnergy(ForgeDirection.getOrientation(connectDirection).getOpposite(), energyProvideRecipe.produceEnergy, false);
					items[0].stackSize--;
					if(items[0].stackSize == 0) items[0] = null;
					this.dispatchEventOnNextTick();
					this.markDirty();
					NBTTagCompound tag = new NBTTagCompound();
					tag.setInteger("id", ENERGY_PROVIDER_MODE);
					coreTile.sendPacket(PacketType.EFFECT, xCoord, yCoord, zCoord, tag);
				}
				this.energyProvideRecipe = null;
			}
			processingTicks = -1;
		}
	}

	private boolean readyToProduceEnergy() {
		EnergyProduceRecipe recipe = EnergyProdeceRecipeRegister.instance.findRecipe(items[0]);
		return provider != null && recipe != null && coreTile.receiveEnergy(ForgeDirection.getOrientation(connectDirection).getOpposite(), recipe.produceEnergy, true) == recipe.produceEnergy;
	}

	//furnace-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public static final int[] furnaceSpeed = { 5, 10, 20, 40, 200 };
	public FurnacePart[] furnace = new FurnacePart[6];

	private boolean checkFurnaceConnection() {
		mode = NORMAL_MODE;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			if(coreTile.getPart(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof FurnacePart){
				FurnacePart part = (FurnacePart) coreTile.getPart(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if(part.grade == this.grade){
					furnace[dir.ordinal()] = part;
					part.container = this;
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
			for (FurnacePart part : furnace){
				part = null;
			}
			connectDirection = -1;
			processingTicks = -1;
			return false;
		}
	}

	/**called by furnace*/
	/**server only*/
	public void startFurnace() {
		if(readyToFurnace() && mode == FURNACE_MODE){
			if(processingTicks == -1){
				processingTicks = 0;
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
			return;
		}
		//process
		if(processingTicks < 200){
			processingTicks += getFurnaceSpeed();
		}else{
			if(items[0] != null){
				this.items[0] = FurnaceRecipes.smelting().getSmeltingResult(this.items[0]).copy();
				for (FurnacePart part : furnace){
					if(part != null){
						part.doSmelt();
					}
				}
				this.dispatchEventOnNextTick();
				this.markDirty();
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
			for (FurnacePart part : furnace){
				if(part != null && part.readyToFurnace()) return true;
			}
			return false;
		}
	}

	private int getFurnaceSpeed() {
		int speed = 0;
		for (FurnacePart part : furnace){
			if(part != null && part.readyToFurnace()) speed += furnaceSpeed[part.grade];
		}
		return speed;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSignalReceived() {
		this.waitForTransfer = true;
	}

	@Override
	public void processCommand(PacketType type, NBTTagCompound tag) {
		if(type == PacketType.EFFECT && coreTile.renderDetail){
			int id = tag.getInteger("id");
			if(id == CRUSHER_MODE){
				String str = "";
				if(Block.getBlockFromItem(items[0].getItem()) != null){
					str = "blockcrack_" + Block.getIdFromBlock(Block.getBlockFromItem(items[0].getItem())) + "_" + items[0].getItemDamage();
				}else{
					str = "iconcrack_" + Item.getIdFromItem(items[0].getItem()) + "_" + items[0].getItemDamage();
				}
				Random random = coreTile.getWorldObj().rand;
				double scale = 0.01D;
				int particleCount = (int) (150.0D * scale);
				for (int i2 = 0; i2 < particleCount; ++i2){
					float direction = MathHelper.randomFloatClamp(random, 0.0F, ((float) Math.PI * 2F));
					double speed = (double) MathHelper.randomFloatClamp(random, 0.75F, 1.0F);
					double vecY = 0.20000000298023224D + scale / 100.0D;
					double vecX = (double) (MathHelper.cos(direction) * 0.2F) * speed * speed * (scale + 0.2D);
					double vecZ = (double) (MathHelper.sin(direction) * 0.2F) * speed * speed * (scale + 0.2D);
					double x = coreTile.toReadWorldX(xCoord + 0.5, yCoord + 0.2, zCoord + 0.5);
					double y = coreTile.toReadWorldY(xCoord + 0.5, yCoord + 0.2, zCoord + 0.5);
					double z = coreTile.toReadWorldZ(xCoord + 0.5, yCoord + 0.2, zCoord + 0.5);
					coreTile.getWorldObj().spawnParticle(str, x, y, z, vecX, vecY, vecZ);
				}
			}else if(id == GROWER_MODE){
				Random rand = coreTile.getWorldObj().rand;
				double angle = rand.nextDouble() * Math.PI * 2;
				double range = rand.nextDouble() * 0.2 + 0.2;
				double x = coreTile.toReadWorldX(xCoord + 0.5 + range * Math.cos(angle), yCoord + 0.5 + rand.nextDouble() * 0.1, zCoord + 0.5 + range * Math.sin(angle));
				double y = coreTile.toReadWorldY(xCoord + 0.5 + range * Math.cos(angle), yCoord + 0.5 + rand.nextDouble() * 0.1, zCoord + 0.5 + range * Math.sin(angle));
				double z = coreTile.toReadWorldZ(xCoord + 0.5 + range * Math.cos(angle), yCoord + 0.5 + rand.nextDouble() * 0.1, zCoord + 0.5 + range * Math.sin(angle));
				coreTile.getWorldObj().spawnParticle("happyVillager", x, y, z, 0, 0, 0);
			}else if(id == ENERGY_PROVIDER_MODE){
				energyProvideRecipe = EnergyProdeceRecipeRegister.instance.findRecipe(items[0]);
				if(energyProvideRecipe == null) return;
				for (int i = 0; i < 3; i++){
					IIcon icon = null;
					if(Block.getBlockFromItem(items[0].getItem()) != null){
						icon = Block.getBlockFromItem(items[0].getItem()).getIcon((int) (Math.random() * 6), items[0].getItemDamage());
					}else{
						icon = items[0].getItem().getIcon(items[0], 0);
					}
					ForgeDirection dir = ForgeDirection.getOrientation(coreTile.toRealWorldSide(connectDirection));
					double x = coreTile.toReadWorldX(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
					double y = coreTile.toReadWorldY(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
					double z = coreTile.toReadWorldZ(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
					double vecX = coreTile.toReadWorldX((double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5);
					double vecY = coreTile.toReadWorldY((double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5);
					double vecZ = coreTile.toReadWorldZ((double) dir.offsetX + xCoord + 0.5, (double) dir.offsetY + yCoord + 0.5, (double) dir.offsetZ + zCoord + 0.5);
					TestCore.spawnParticle(coreTile.getWorldObj(), TestCore.PARTICLE_CRUCK, x, y, z, vecX, vecY, vecZ, icon);
				}
			}else if(id == FURNACE_MODE){
				ForgeDirection dir = ForgeDirection.getOrientation((int) (Math.random() * 6));
				double x = coreTile.toReadWorldX(xCoord + 0.5 + dir.offsetX * 0.2, yCoord + 0.5 + dir.offsetY * 0.2, zCoord + 0.5 + dir.offsetZ * 0.2);
				double y = coreTile.toReadWorldY(xCoord + 0.5 + dir.offsetX * 0.2, yCoord + 0.5 + dir.offsetY * 0.2, zCoord + 0.5 + dir.offsetZ * 0.2);
				double z = coreTile.toReadWorldZ(xCoord + 0.5 + dir.offsetX * 0.2, yCoord + 0.5 + dir.offsetY * 0.2, zCoord + 0.5 + dir.offsetZ * 0.2);
				coreTile.getWorldObj().spawnParticle("flame", x, y, z, 0, 0, 0);
			}
		}
		super.processCommand(type, tag);
	}

	@Override
	protected Class<ISidedInventory> getTargetClass() {
		return ISidedInventory.class;
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
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
				return EnergyProdeceRecipeRegister.instance.findRecipe(item) != null;
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

	@Override
	public String getNameForHUD() {
		if(this.mode == CRUSHER_MODE){
			return ColorCode[grade] + "CRUSHER";
		}else if(this.mode == GROWER_MODE){
			return ColorCode[grade] + "VIRTUAL GROWER";
		}else if(this.mode == ENERGY_PROVIDER_MODE){
			return ColorCode[grade] + "ENERGY PROVIDER";
		}else if(this.mode == FURNACE_MODE){
			return ColorCode[grade] + "FURNACE";
		}else{
			return ColorCode[grade] + "CONTAINER";
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.readFromNBT(tag, solid);
		this.mode = tag.getInteger("mode");
		this.connectDirection = tag.getInteger("connectionDirection");
		this.processingTicks = tag.getInteger("processingTicks");
		this.waitForTransfer = tag.getBoolean("waitForTransfer");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, RectangularSolid solid) {
		super.writeToNBT(tag, solid);
		tag.setInteger("mode", mode);
		tag.setInteger("connectionDirection", connectDirection);
		tag.setInteger("processingTicks", processingTicks);
		tag.setBoolean("waitForTransfer", waitForTransfer);
	}

	@Override
	public String getNameForNBT() {
		return ConstructContainerTileEntity.nameForNBT;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private double rand1 = Math.random() * Math.PI;
	private float rand2 = (float) (Math.random() * 30f) - 15f;
	private float rand3 = (float) (Math.random() * 30f) - 15f;

	@SideOnly(Side.CLIENT)
	@Override
	public void renderPart(double totalTicks) {
		float yOffset = (float) Math.sin(this.rand1 + (totalTicks / 40d) % (2d * Math.PI)) * Amplitude - 0.25f;
		GL11.glTranslatef(0, yOffset, 0);
		GL11.glRotatef(rand2, 0, 1, 0);
		GL11.glRotatef(rand3, 1, 0, 0);
		GL11.glScalef(2, 2, 2);
		EntityItem entityitem1 = null;
		entityitem1 = new EntityItem(coreTile.getWorldObj(), 0.0D, 0.0D, 0.0D, new ItemStack(getRenderBlock(), 1, getRenderMeta()));
		entityitem1.hoverStart = 0.0F;
		RenderManager.instance.renderEntityWithPosYaw(entityitem1, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
		GL11.glScalef(0.5f, 0.5f, 0.5f);

		if(this.getStackInSlot(0) != null){
			EntityItem entityitem2 = null;
			GL11.glRotatef((float) (0.1F * totalTicks % 180.0F), 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0, 0.08f, 0);

			ItemStack is = this.getStackInSlot(0).copy();
			is.stackSize = 1;
			entityitem2 = new EntityItem(coreTile.getWorldObj(), 0.0D, 0.0D, 0.0D, is);
			entityitem2.hoverStart = 0.0F;

			RenderManager.instance.renderEntityWithPosYaw(entityitem2, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

			if(this.mode == GROWER_MODE && this.items[0].stackSize >= 2 && Block.getBlockFromItem(this.items[0].getItem()) == Blocks.cactus){
				RenderManager.instance.renderEntityWithPosYaw(entityitem2, 0.0D, 0.25D, 0.0D, 0.0F, 0.0F);
			}
			GL11.glTranslatef(0, -0.08f, 0);
			GL11.glRotatef((float) (-0.1F * totalTicks % 180.0F), 0.0F, 1.0F, 0.0F);
		}

		GL11.glRotatef(-rand3, 1, 0, 0);
		GL11.glRotatef(-rand2, 0, 1, 0);
		GL11.glTranslatef(0, -yOffset, 0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected Block getRenderBlock() {
		return TestCore.constructContainer[grade];
	}

}