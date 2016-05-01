package com.test.multiblock.construct.block;

import java.util.List;

import com.test.block.ITestModBlock;
import com.test.client.IToolTipUser;
import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.test.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class ConstructFunctionalBase extends BlockContainer implements ITestModBlock, IToolTipUser {

	public static String[] gradeName = { "WOOD", "IRON", "GOLD", "DIAMOND", "EMERALD" };
	public static IIcon iconPane;

	public final int grade;

	public ConstructFunctionalBase(int grade) {
		super(Material.rock);
		this.setCreativeTab(TestCore.testCreativeTab);
		this.setLightOpacity(0);
		this.setHardness(1.5F);
		this.setBlockBounds(2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F);
		this.grade = grade;
	}

	/**when game starting , this is called before TileEntity.readFromNBT*/
	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
		super.onPostBlockPlaced(world, x, y, z, meta);
		if(!(world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity)) return;
		ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(x, y, z);
		tile.updateIsNeighberBaseBlock();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
		if(!(world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity)) return;
		ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(x, y, z);
		tile.updateIsNeighberBaseBlock();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() == null){
			if(!player.isSneaking()){
				return onRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
			}else{
				return onShiftRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
			}
		}else if(player.getHeldItem().getItem() instanceof IToolWrench){
			return onRightClickedByWrench(world, x, y, z, player, side, hitX, hitY, hitZ);
		}else{
			return onRightClicked(world, x, y, z, player, side, hitX, hitY, hitZ);
		}
	}

	protected boolean onRightClicked(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity){
			return ((ConstructBaseTileEntity) world.getTileEntity(x, y, z)).onRightClicked(player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	protected boolean onShiftRightClicked(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity){
			return ((ConstructBaseTileEntity) world.getTileEntity(x, y, z)).onShiftRightClicked(player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	protected boolean onRightClickedByWrench(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity){
			return ((ConstructBaseTileEntity) world.getTileEntity(x, y, z)).onRightClickedByWrench(player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		MovingObjectPosition mop = UtilMethods.getMovingObjectPositionFromPlayer(world, player, true);
		if(mop.blockX == x && mop.blockY == y && mop.blockZ == z && world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity){
			((ConstructBaseTileEntity) world.getTileEntity(x, y, z)).onLeftClicked(player, mop.sideHit, mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
		}
	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from
	 * getComparatorInputOverride instead of the actual redstone signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride() {
		return false;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
	 * strength when this block inputs to a comparator.
	 */
	@Override
	public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_) {
		return 0;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if(world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity){
			ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(x, y, z);
			tile.onTileRemoved();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public int getRenderType() {
		return TestCore.CONSTRUCTBASE_RENDER_ID;
	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return grade < 2 ? EnumRarity.common : (grade == 2 ? EnumRarity.uncommon : grade == 3 ? EnumRarity.rare : EnumRarity.epic);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return this.blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public abstract void registerBlockIcons(IIconRegister register);

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {

	}

	@Override
	/**return TileEntity extending FunctionalConstructBaseTileEntity*/
	public abstract TileEntity createNewTileEntity(World world, int meta);

}
