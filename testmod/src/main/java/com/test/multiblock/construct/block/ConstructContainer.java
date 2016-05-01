package com.test.multiblock.construct.block;

import java.util.List;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		if(advancedToolTip){
			toolTip.add("Make some functions when connecting with some kinds of blocks.");
			toolTip.add("This can serve as one function mode at the same time");
			toolTip.add("When this receives a signal, this will stop its function");
			toolTip.add("and wait until this completes transfering item in this.");
		}
		if(shiftPressed || advancedToolTip){
			toolTip.add("Available Mode :");
			toolTip.add("  CRUSHER");
			toolTip.add("  VIRTUAL GROWER");
			toolTip.add("  ENERGY PROVIDER");
			toolTip.add("  FURNACE");
			toolTip.add("See each block for more information");
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructContainerTileEntity(grade);
	}

}