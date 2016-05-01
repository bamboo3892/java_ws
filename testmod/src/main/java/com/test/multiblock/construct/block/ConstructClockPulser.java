package com.test.multiblock.construct.block;

import com.test.multiblock.construct.tileentity.ConstructClockPulserTileEntity;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructClockPulser extends ConstructFunctionalBase {

	public ConstructClockPulser(int grade) {
		super(grade);
		setBlockName("clockPulser");
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = Blocks.redstone_block.getIcon(0, 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructClockPulserTileEntity(grade);
	}

}
