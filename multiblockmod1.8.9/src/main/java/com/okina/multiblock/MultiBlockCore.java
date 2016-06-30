package com.okina.multiblock;

import static com.okina.main.TestCore.*;

import java.util.ArrayList;
import java.util.List;

import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MultiBlockCore extends BlockContainer {

	//	public static IIcon background;
	public static final PropertyInteger DIRECTION = PropertyInteger.create("direction", 0, 5);

	public MultiBlockCore() {
		super(Material.iron);
		setCreativeTab(TestCore.testCreativeTab);
		setUnlocalizedName(AUTHER + ".multiBlockCore");
		setLightOpacity(0);
		setHardness(1F);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int l = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockState(pos, state.withProperty(DIRECTION, Integer.valueOf(l)));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(pos) instanceof MultiBlockCoreTileEntity){
			MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) world.getTileEntity(pos);
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
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		ItemStack stack = new ItemStack(TestCore.multiBlockCore, 1);
		if(world.getTileEntity(pos) instanceof MultiBlockCoreTileEntity){
			MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) world.getTileEntity(pos);
			tile.writeToNBTForItemDrop(stack);
		}
		world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack));
		super.breakBlock(world, pos, state);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		return ret;
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
	public int getRenderType() {
		return 3;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new MultiBlockCoreTileEntity();
	}

}
