package com.okina.multiblock.construct.block;

import java.util.List;

import com.okina.block.ITestModBlock;
import com.okina.block.properties.PropertyConnection;
import com.okina.client.IToolTipUser;
import com.okina.main.TestCore;
import com.okina.multiblock.BlockBaseFrame;
import com.okina.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.okina.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class ConstructFunctionalBase extends BlockContainer implements ITestModBlock, IToolTipUser {

	public static String[] gradeName = { "WOOD", "IRON", "GOLD", "DIAMOND", "EMERALD" };
	public static final PropertyConnection CONNECTION = new PropertyConnection("connection");

	public final int grade;

	public ConstructFunctionalBase(int grade) {
		super(Material.rock);
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
		setBlockBounds(2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F);
		this.grade = grade;
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if(!(world.getTileEntity(pos) instanceof ConstructBaseTileEntity)) return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
		ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(pos);
		tile.updateIsNeighberBaseBlock();
		return super.onBlockPlaced(world, pos, facing, hitX, hitY, hitZ, meta, placer);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos tilePos) {
		super.onNeighborChange(world, pos, tilePos);
		if(!(world.getTileEntity(pos) instanceof ConstructBaseTileEntity)) return;
		ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(pos);
		tile.updateIsNeighberBaseBlock();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() == null){
			if(!player.isSneaking()){
				return onRightClicked(world, pos, state, player, side, hitX, hitY, hitZ);
			}else{
				return onShiftRightClicked(world, pos, state, player, side, hitX, hitY, hitZ);
			}
		}else if(player.getHeldItem().getItem() instanceof IToolWrench){
			return onRightClickedByWrench(world, pos, state, player, side, hitX, hitY, hitZ);
		}else{
			return onRightClicked(world, pos, state, player, side, hitX, hitY, hitZ);
		}
	}

	protected boolean onRightClicked(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(pos) instanceof ConstructBaseTileEntity){
			return ((ConstructBaseTileEntity) world.getTileEntity(pos)).onRightClicked(state, player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	protected boolean onShiftRightClicked(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(pos) instanceof ConstructBaseTileEntity){
			return ((ConstructBaseTileEntity) world.getTileEntity(pos)).onShiftRightClicked(state, player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	protected boolean onRightClickedByWrench(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(pos) instanceof ConstructBaseTileEntity){
			return ((ConstructBaseTileEntity) world.getTileEntity(pos)).onRightClickedByWrench(state, player, side, hitX, hitY, hitZ);
		}
		return false;
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		MovingObjectPosition mop = UtilMethods.getMovingObjectPositionFromPlayer(world, player, true);
		if(mop.getBlockPos().equals(pos) && world.getTileEntity(pos) instanceof ConstructBaseTileEntity){
			((ConstructBaseTileEntity) world.getTileEntity(pos)).onLeftClicked(player, mop);
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
	public int getComparatorInputOverride(World p_149736_1_, BlockPos pos) {
		return 0;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if(world.getTileEntity(pos) instanceof ConstructBaseTileEntity){
			ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(pos);
			tile.onTileRemoved();
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	protected BlockState createBlockState() {
		IProperty[] listedProperties = new IProperty[0];
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { CONNECTION };
		ExtendedBlockState state = new ExtendedBlockState(this, listedProperties, unlistedProperties);
		return state;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if(state instanceof IExtendedBlockState){
			boolean[] b = new boolean[6];
			for (EnumFacing dir : EnumFacing.VALUES){
				b[dir.getIndex()] = world.getBlockState(pos.add(dir.getDirectionVec())).getBlock() instanceof ConstructFunctionalBase || world.getBlockState(pos.add(dir.getDirectionVec())).getBlock() instanceof BlockBaseFrame;
			}
			return ((IExtendedBlockState) state).withProperty(CONNECTION, UtilMethods.convertConnectionInfo(b));
		}
		return state;
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return grade < 2 ? EnumRarity.COMMON : (grade == 2 ? EnumRarity.UNCOMMON : grade == 3 ? EnumRarity.RARE : EnumRarity.EPIC);
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {}
	@Override
	public int getNeutralLines() {
		return 0;
	}
	@Override
	public int getShiftLines() {
		return 0;
	}

	@Override
	/**return TileEntity extending FunctionalConstructBaseTileEntity*/
	public abstract TileEntity createNewTileEntity(World world, int meta);

}
