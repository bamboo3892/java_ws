package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import java.util.Random;

import com.okina.multiblock.construct.tileentity.ConstructEnergyProviderTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConstructEnergyProvider extends ConstructFunctionalBase {

	//	public static IIcon[] energyIcon = new IIcon[16];

	public ConstructEnergyProvider(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".energyprovider");
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if((int) (rand.nextDouble() * 3) == 0){
			double spawnX = x + 0.4 + rand.nextDouble() * 0.2;
			double spawnY = y + 0.8 + rand.nextDouble() * 0.2;
			double spawnZ = z + 0.4 + rand.nextDouble() * 0.2;
			world.spawnParticle(EnumParticleTypes.PORTAL, spawnX, spawnY, spawnZ, 0, 0, 0, null);
		}
	}

	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = Blocks.portal.getIcon(0, 0);
	//		iconPane = register.registerIcon(TestCore.MODID + ":construct_pane");
	//		for (int i = 0; i < energyIcon.length; i++){
	//			energyIcon[i] = register.registerIcon(TestCore.MODID + ":energy/energy" + i);
	//		}
	//	}

	@Override
	public int getShiftLines() {
		return 3;
	}

	//	@Override
	//	public int getRenderType() {
	//		return TestCore.ENERGYPROVIDER_RENDER_ID;
	//	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructEnergyProviderTileEntity(grade);
	}

}
