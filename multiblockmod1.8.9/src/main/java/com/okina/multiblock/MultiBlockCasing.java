package com.okina.multiblock;

import static com.okina.main.TestCore.*;

import com.okina.block.properties.PropertyConnection;
import com.okina.main.TestCore;
import com.okina.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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

public class MultiBlockCasing extends BlockContainer {

	public static final PropertyConnection CONNECTION = new PropertyConnection("connection");

	public MultiBlockCasing() {
		super(Material.iron);
		setCreativeTab(TestCore.testCreativeTab);
		setUnlocalizedName(AUTHER + ".multiBlockCasing");
		setLightOpacity(0);
		setHardness(1F);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		for (int i = -1; i < 2; i++){
			for (int j = -1; j < 2; j++){
				for (int k = -1; k < 2; k++){
					if(i == 0 && j == 0 && k == 0) continue;
					TileEntity tile = world.getTileEntity(pos.add(i, j, k));
					if(tile instanceof MultiBlockCoreTileEntity){
						if(((MultiBlockCoreTileEntity) tile).connect()) return;
					}
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(pos) instanceof MultiBlockCasingTileEntity){
			MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) world.getTileEntity(pos);
			if(player.getHeldItem() == null){
				if(!player.isSneaking()){
					return tile.onRightClicked(state, player, side, hitX, hitY, hitZ);
				}else{
					return tile.onShiftRightClicked(state, player, side, hitX, hitY, hitZ);
				}
			}else if(player.getHeldItem().getItem() instanceof IToolWrench){
				return tile.onRightClickedByWrench(state, player, side, hitX, hitY, hitZ);
			}else{
				return tile.onRightClicked(state, player, side, hitX, hitY, hitZ);
			}
		}
		return false;
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		MovingObjectPosition mop = UtilMethods.getMovingObjectPositionFromPlayer(world, player, true);
		if(mop.equals(pos) && world.getTileEntity(pos) instanceof MultiBlockCasingTileEntity){
			((MultiBlockCasingTileEntity) world.getTileEntity(pos)).onLeftClicked(player, mop);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		if(!(block instanceof MultiBlockCasing || block instanceof MultiBlockCore) && block != null && block.canProvidePower()){
			if(world.getTileEntity(pos) instanceof MultiBlockCasingTileEntity){
				MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) world.getTileEntity(pos);
				if(tile.isConnected()){
					for (EnumFacing dir : EnumFacing.VALUES){
						if(!(world.getTileEntity(pos.add(dir.getDirectionVec())) instanceof MultiBlockCasingTileEntity || world.getTileEntity(pos.add(dir.getDirectionVec())) instanceof MultiBlockCoreTileEntity)){
							int power = world.getRedstonePower(pos.add(dir.getDirectionVec()), dir.getOpposite());
							tile.getCoreTie().changeSidePowered(dir, power != 0);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if(!world.isRemote){
			for (int i = -1; i < 2; i++){
				for (int j = -1; j < 2; j++){
					for (int k = -1; k < 2; k++){
						if(i == 0 && j == 0 && k == 0) continue;
						TileEntity tile = world.getTileEntity(pos.add(i, j, k));
						if(tile instanceof MultiBlockCoreTileEntity){
							((MultiBlockCoreTileEntity) tile).disconnect();
						}
					}
				}
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public int getMobilityFlag() {
		return 2;
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
	public int getRenderType() {
		return 3;
	}

	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	//	@Override@Override
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
			if(world.getTileEntity(pos) instanceof MultiBlockCasingTileEntity){
				if(((MultiBlockCasingTileEntity) world.getTileEntity(pos)).isConnected()){
					return ((IExtendedBlockState) state).withProperty(CONNECTION, UtilMethods.convertConnectionInfo(b));
				}
			}
			for (EnumFacing dir : EnumFacing.VALUES){
				b[dir.getIndex()] = world.getBlockState(pos.add(dir.getDirectionVec())).getBlock() instanceof MultiBlockCasing || world.getBlockState(pos.add(dir.getDirectionVec())).getBlock() instanceof MultiBlockCore;
			}
			return ((IExtendedBlockState) state).withProperty(CONNECTION, UtilMethods.convertConnectionInfo(b));
		}
		return state;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new MultiBlockCasingTileEntity();
	}

}
