package com.test.multiblock;

import java.util.Random;

import com.test.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBaseFrame extends Block {

	public static final String[] blockName = { "wood", "iron", "gold", "diamond", "emerald" };
	public static final String[] frameTextureName = { "log_oak", "iron_block", "gold_block", "diamond_block", "emerald_block" };
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

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if((int) (rand.nextDouble() * 3) == 0){
			double spawnX = x + 0.4 + rand.nextDouble() * 0.2;
			double spawnY = y + 0.4 + rand.nextDouble() * 0.2;
			double spawnZ = z + 0.4 + rand.nextDouble() * 0.2;
			TestCore.spawnParticle(world, TestCore.PARTICLE_DOT, spawnX, spawnY, spawnZ, 0xffffff, 0.5f);
		}
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
			this.icons[i] = register.registerIcon(frameTextureName[i]);
		}
	}

}
