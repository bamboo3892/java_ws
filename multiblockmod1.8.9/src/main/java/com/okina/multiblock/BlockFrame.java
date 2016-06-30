package com.okina.multiblock;

import static com.okina.main.TestCore.*;

import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFrame extends BlockContainer {

	public BlockFrame() {
		super(Material.iron);
		setUnlocalizedName(AUTHER + ".blockFrame");
		//		setBlockTextureName("stone");
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IToolWrench){
			if(world.getTileEntity(pos) instanceof BlockFrameTileEntity){
				((BlockFrameTileEntity) world.getTileEntity(pos)).tryConstruct(player, side);
			}
			return true;
		}
		return false;
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
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new BlockFrameTileEntity();
	}

}
