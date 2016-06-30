package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import com.okina.multiblock.construct.tileentity.ConstructContainerTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ConstructContainer extends ConstructFunctionalBase {

	public ConstructContainer(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".container");
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof ConstructContainerTileEntity){
			((ConstructContainerTileEntity) tile).checkNeighberBlockConnection();
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	/**called server side*/
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof ConstructContainerTileEntity){
			((ConstructContainerTileEntity) tile).checkNeighberBlockConnection();
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	//	@Override
	//	public int getRenderType() {
	//		return TestCore.CONTAINER_RENDER_ID;
	//	}

	@Override
	public int getShiftLines() {
		return 5;
	}

	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = register.registerIcon(TestCore.MODID + ":null");
	//	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructContainerTileEntity(grade);
	}

}
