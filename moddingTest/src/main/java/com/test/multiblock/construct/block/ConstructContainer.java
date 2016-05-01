package com.test.multiblock.construct.block;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructContainer extends ConstructFunctionalBase {

	public ConstructContainer(int grade) {
		super(grade);
		this.setBlockName("container");
	}

	 public void onBlockAdded(World world, int x, int y, int z) {
		 TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof ConstructContainerTileEntity){
				((ConstructContainerTileEntity) tile).checkNeighberBlockConnection();
				world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
			}
	 }

	/**called server side*/
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof ConstructContainerTileEntity){
			((ConstructContainerTileEntity) tile).checkNeighberBlockConnection();
			world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
		}
	}

	@Override
	public int getRenderType() {
		return TestCore.CONTAINER_RENDER_ID;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(TestCore.MODID + ":null");
		this.front = register.registerIcon(TestCore.MODID + ":null");
		this.top = register.registerIcon(TestCore.MODID + ":null");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructContainerTileEntity(grade);
	}

}
