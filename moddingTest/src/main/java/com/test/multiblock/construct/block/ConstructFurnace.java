package com.test.multiblock.construct.block;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructFurnaceTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructFurnace extends ConstructFunctionalBase {

	public ConstructFurnace(int grade) {
		super(grade);
		setBlockName("furnace");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon("furnace_side");
		this.front = register.registerIcon(TestCore.MODID + ":furnace_front_off");
		this.top = register.registerIcon("furnace_top");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructFurnaceTileEntity(grade);
	}

}
