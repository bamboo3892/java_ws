package com.test.multiblock.construct.block;

import com.test.inventory.CraftingFilterInventory;
import com.test.inventory.FilterInventory;
import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructFilterUserTileEntity;
import com.test.multiblock.construct.tileentity.ConstructStorageTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ConstructStorage extends ConstructFunctionalBase {

	public static IIcon filteredPane;
	public static IIcon craftingFilteredPane;

	public ConstructStorage(int grade) {
		super(grade);
		setBlockName("storage");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructStorageTileEntity(grade);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		if(world.getTileEntity(x, y, z) instanceof ConstructFilterUserTileEntity){
			ConstructFilterUserTileEntity tile = (ConstructFilterUserTileEntity)world.getTileEntity(x, y, z);
			if(tile.getFilter(side) instanceof FilterInventory){
				return filteredPane;
			}else if(tile.getFilter(side) instanceof CraftingFilterInventory){
				return craftingFilteredPane;
			}
		}
		return this.getIcon(side, world.getBlockMetadata(x, y, z));
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = Blocks.planks.getIcon(0, 0);
		filteredPane = register.registerIcon(TestCore.MODID + ":filtered_storage");
		craftingFilteredPane = register.registerIcon(TestCore.MODID + ":crafting_filtered_storage");
	}

}
