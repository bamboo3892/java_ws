package com.test.multiblock.construct.block;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructVirtualGrowerTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ConstructVirtualGlower extends ConstructFunctionalBase {

	private IIcon top;
	private IIcon side;

	public ConstructVirtualGlower(int grade) {
		super(grade);
		this.setBlockName("virtualGrower");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.top : (side == 0 ? this.side : this.blockIcon);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = Blocks.log.getBlockTextureFromSide(2);
		this.side = Blocks.log.getBlockTextureFromSide(0);
		this.top = register.registerIcon(TestCore.MODID + ":grass_top");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructVirtualGrowerTileEntity(grade);
	}

}
