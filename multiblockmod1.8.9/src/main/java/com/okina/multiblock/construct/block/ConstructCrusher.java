package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import com.okina.multiblock.construct.tileentity.ConstructCrusherTileEntity;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ConstructCrusher extends ConstructFunctionalBase {

	public static final PropertyInteger DIRECTION = PropertyInteger.create("direction", 0, 5);
	//	private IIcon front;

	public ConstructCrusher(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".crusher");
		setDefaultState(blockState.getBaseState().withProperty(DIRECTION, Integer.valueOf(0)));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		EnumFacing dir = BlockPistonBase.getFacingFromEntity(world, pos, placer);
		world.setBlockState(pos, state.withProperty(DIRECTION, Integer.valueOf(dir.getIndex())));
	}

	//	@SideOnly(Side.CLIENT)
	//	@Override
	//	public IIcon getIcon(int side, int meta) {
	//		return side != meta ? this.blockIcon : this.front;
	//	}

	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = Blocks.cactus.getIcon(2, 0);
	//		this.front = Blocks.cactus.getIcon(0, 0);
	//	}

	@Override
	public int getShiftLines() {
		return 4;
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, DIRECTION);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(DIRECTION, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(DIRECTION);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructCrusherTileEntity(grade);
	}

}
