package com.okina.multiblock;

import static com.okina.main.TestCore.*;

import com.okina.block.ITestModBlock;
import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class DisassemblyTable extends BlockContainer implements ITestModBlock {

	public DisassemblyTable() {
		super(Material.iron);
		setCreativeTab(TestCore.testCreativeTab);
		setLightOpacity(0);
		setHardness(1.5F);
		setUnlocalizedName(AUTHER + ".disassemblyTable");
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(pos) instanceof DisassemblyTableTileEntity){
			DisassemblyTableTileEntity table = (DisassemblyTableTileEntity) world.getTileEntity(pos);
			if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IToolWrench){
				table.onRightClickedByWrench(state, player, side, hitX, hitY, hitZ);
				return true;
			}else{
				table.onRightClicked(state, player, side, hitX, hitY, hitZ);
				return true;
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if(world.getTileEntity(pos) instanceof DisassemblyTableTileEntity){
			DisassemblyTableTileEntity tile = (DisassemblyTableTileEntity) world.getTileEntity(pos);
			tile.onTileRemoved();
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new DisassemblyTableTileEntity();
	}

	@Override
	public int getRenderType() {
		return 3;
	}
	
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
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
	public EnumRarity getRarity(ItemStack itemStack) {
		return EnumRarity.EPIC;
	}

}
