package com.test.multiblock;

import com.test.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockBaseFrame extends Block {

	public static final String[] blockName = { "wood", "iron", "gold", "diamond", "emerald" };
	public static final String[] textureName = { "log_oak", "iron_block", "gold_block", "diamond_block", "emerald_block" };
	public static final IIcon[] icons = new IIcon[blockName.length];

	public int grade;

	public BlockBaseFrame(int grade) {
		super(Material.iron);
		this.setBlockName("baseFrame");
		this.setCreativeTab(TestCore.testCreativeTab);
		this.setLightOpacity(0);
		this.setHardness(1.5F);
		this.grade = grade;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public int getRenderType() {
		return TestCore.CONSTRUCTBASE_RENDER_ID;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return this.icons[grade];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (int i = 0; i < blockName.length; i++){
			this.icons[i] = register.registerIcon(textureName[i]);
		}
	}

}
