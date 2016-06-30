package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import com.okina.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructFurnaceTileEntity;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ConstructFurnace extends ConstructFunctionalBase {

	public static final PropertyInteger DIRECTION = PropertyInteger.create("direction", 0, 6);

	//	protected IIcon top;
	//	protected IIcon front;

	public ConstructFurnace(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".furnace");
		setDefaultState(blockState.getBaseState().withProperty(DIRECTION, Integer.valueOf(0)));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int l = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		if(l == 0){
			world.setBlockState(pos, state.withProperty(DIRECTION, Integer.valueOf(2)), 2);
		}else if(l == 1){
			world.setBlockState(pos, state.withProperty(DIRECTION, Integer.valueOf(5)), 2);
		}else if(l == 2){
			world.setBlockState(pos, state.withProperty(DIRECTION, Integer.valueOf(3)), 2);
		}else if(l == 3){
			world.setBlockState(pos, state.withProperty(DIRECTION, Integer.valueOf(4)), 2);
		}
		if(!(world.getTileEntity(pos) instanceof ConstructBaseTileEntity)) return;
		ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(pos);
		tile.updateIsNeighberBaseBlock();
	}

	//	@SideOnly(Side.CLIENT)
	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = register.registerIcon("furnace_side");
	//		this.front = register.registerIcon(TestCore.MODID + ":furnace_front_off");
	//		this.top = register.registerIcon("furnace_top");
	//	}

	//	@SideOnly(Side.CLIENT)
	//	@Override
	//	public IIcon getIcon(int side, int meta) {
	//		return side == 1 ? this.top : (side == 0 ? this.top : (side != meta ? this.blockIcon : this.front));
	//	}

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
	public int getShiftLines() {
		return 4;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructFurnaceTileEntity(grade);
	}

}
