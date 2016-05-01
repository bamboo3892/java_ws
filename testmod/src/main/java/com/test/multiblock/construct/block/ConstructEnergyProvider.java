package com.test.multiblock.construct.block;

import java.util.Random;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructEnergyProviderTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ConstructEnergyProvider extends ConstructFunctionalBase {

	public static IIcon[] energyIcon = new IIcon[16];

	public ConstructEnergyProvider(int grade) {
		super(grade);
		this.setBlockName("energyprovider");
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if((int) (rand.nextDouble() * 3) == 0){
			double spawnX = x + 0.4 + rand.nextDouble() * 0.2;
			double spawnY = y + 0.8 + rand.nextDouble() * 0.2;
			double spawnZ = z + 0.4 + rand.nextDouble() * 0.2;
			world.spawnParticle("portal", spawnX, spawnY, spawnZ, 0, 0, 0);
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = Blocks.portal.getIcon(0, 0);
		iconPane = register.registerIcon(TestCore.MODID + ":construct_pane");
		for (int i = 0; i < energyIcon.length; i++){
			energyIcon[i] = register.registerIcon(TestCore.MODID + ":energy/energy" + i);
		}
	}

	@Override
	public int getRenderType() {
		return TestCore.ENERGYPROVIDER_RENDER_ID;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructEnergyProviderTileEntity(grade);
	}

}
