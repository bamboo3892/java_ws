package com.test.multiblock.construct.block;

import com.test.multiblock.construct.tileentity.ConstructRepeaterTileEntity;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructRepeater extends ConstructFunctionalBase {

	public ConstructRepeater(int grade) {
		super(grade);
		this.setBlockName("repeater");
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = Blocks.quartz_block.getIcon(2, 0);
		this.front = Blocks.quartz_block.getIcon(2, 0);
		this.top = Blocks.quartz_block.getIcon(0, 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructRepeaterTileEntity(grade);
	}

}
