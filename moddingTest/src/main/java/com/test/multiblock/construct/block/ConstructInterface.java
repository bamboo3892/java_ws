package com.test.multiblock.construct.block;

import com.test.multiblock.construct.tileentity.ConstructInterfaceTileEntity;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructInterface extends ConstructFunctionalBase {

	public ConstructInterface(int grade) {
		super(grade);
		setBlockName("interface");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructInterfaceTileEntity(grade);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = Blocks.enchanting_table.getIcon(1, 0);
		this.front = Blocks.enchanting_table.getIcon(1, 0);
		this.top = Blocks.enchanting_table.getIcon(1, 0);
	}

}
