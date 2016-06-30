package com.okina.multiblock;

import static com.okina.main.TestCore.*;

import com.okina.block.properties.PropertyConnection;
import com.okina.main.TestCore;
import com.okina.utils.UtilMethods;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockFrameLine extends Block {

	public static final PropertyConnection CONNECTION = new PropertyConnection("connection");

	public BlockFrameLine() {
		super(Material.iron);
		setUnlocalizedName(AUTHER + ".blockFrameLine");
		//		setBlockTextureName("stone");
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
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
				b[dir.getIndex()] = world.getBlockState(pos.add(dir.getDirectionVec())).getBlock() instanceof BlockFrameLine;
			}
			return ((IExtendedBlockState) state).withProperty(CONNECTION, UtilMethods.convertConnectionInfo(b));
		}
		return state;
	}

}
