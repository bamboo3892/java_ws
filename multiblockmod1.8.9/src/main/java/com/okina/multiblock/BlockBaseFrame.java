package com.okina.multiblock;

import static com.okina.main.TestCore.*;

import java.util.Random;

import com.okina.block.properties.PropertyConnection;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.block.ConstructFunctionalBase;
import com.okina.utils.UtilMethods;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBaseFrame extends Block {

	public static final String[] blockName = { "wood", "iron", "gold", "diamond", "emerald" };
	public static final String[] frameTextureName = { "log_oak", "iron_block", "gold_block", "diamond_block", "emerald_block" };
	public static final PropertyConnection CONNECTION = new PropertyConnection("connection");

	public int grade;

	public BlockBaseFrame(int grade) {
		super(Material.iron);
		setUnlocalizedName(AUTHER + ".baseFrame");
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
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
	protected BlockState createBlockState() {
		IProperty[] listedProperties = new IProperty[0];
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { CONNECTION };
		ExtendedBlockState state = new ExtendedBlockState(this, listedProperties, unlistedProperties);
		return state;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if(state instanceof IExtendedBlockState){
			boolean[] b = new boolean[6];
			for (EnumFacing dir : EnumFacing.VALUES){
				b[dir.getIndex()] = world.getBlockState(pos.add(dir.getDirectionVec())).getBlock() instanceof BlockBaseFrame || world.getBlockState(pos.add(dir.getDirectionVec())).getBlock() instanceof ConstructFunctionalBase;
			}
			return ((IExtendedBlockState) state).withProperty(CONNECTION, UtilMethods.convertConnectionInfo(b));
		}
		return state;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

}
