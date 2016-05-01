package com.test.multiblock;

import com.test.block.ITestModBlock;
import com.test.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class DisassemblyTable extends BlockContainer implements ITestModBlock {

	public DisassemblyTable() {
		super(Material.iron);
		this.setCreativeTab(TestCore.testCreativeTab);
		this.setLightOpacity(0);
		this.setHardness(1.5F);
		this.setBlockTextureName("stone");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(x, y, z) instanceof DisassemblyTableTileEntity){
			DisassemblyTableTileEntity table = (DisassemblyTableTileEntity) world.getTileEntity(x, y, z);
			if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IToolWrench){
				table.onRightClickedByWrench(player, side, hitX, hitY, hitZ);
				return true;
			}else{
				table.onRightClicked(player, side, hitX, hitY, hitZ);
				return true;
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if(world.getTileEntity(x, y, z) instanceof DisassemblyTableTileEntity){
			DisassemblyTableTileEntity tile = (DisassemblyTableTileEntity) world.getTileEntity(x, y, z);
			tile.onTileRemoved();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new DisassemblyTableTileEntity();
	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return EnumRarity.epic;
	}

}
