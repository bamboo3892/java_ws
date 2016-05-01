package com.test.multiblock.construct.block;

import com.test.inventory.FilterInventory;
import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructDispatcherTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ConstructDispatcher extends ConstructFunctionalBase {

	public static IIcon filteredPane;

	public ConstructDispatcher(int grade) {
		super(grade);
		setBlockName("dispatcher");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		if(world.getTileEntity(x, y, z) instanceof ConstructDispatcherTileEntity){
			ConstructDispatcherTileEntity tile = (ConstructDispatcherTileEntity) world.getTileEntity(x, y, z);
			if(tile.getFilter(side) instanceof FilterInventory){
				return filteredPane;
			}
		}
		return this.getIcon(side, world.getBlockMetadata(x, y, z));
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = Blocks.lapis_block.getIcon(0, 0);
		filteredPane = register.registerIcon(TestCore.MODID + ":filtered_dispatcher");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructDispatcherTileEntity(grade);
	}

}