package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import com.okina.multiblock.construct.tileentity.ConstructDispatcherTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructDispatcher extends ConstructFunctionalBase {

	//	public static IIcon filteredPane;

	public ConstructDispatcher(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".dispatcher");
	}

	//	@SideOnly(Side.CLIENT)
	//	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
	//		if(world.getTileEntity(x, y, z) instanceof ConstructDispatcherTileEntity){
	//			ConstructDispatcherTileEntity tile = (ConstructDispatcherTileEntity) world.getTileEntity(x, y, z);
	//			if(tile.getFilter(side) instanceof FilterInventory){
	//				return filteredPane;
	//			}
	//		}
	//		return this.getIcon(side, world.getBlockMetadata(x, y, z));
	//	}

	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = Blocks.lapis_block.getIcon(0, 0);
	//		filteredPane = register.registerIcon(TestCore.MODID + ":filtered_dispatcher");
	//	}

	@Override
	public int getShiftLines() {
		return 1;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructDispatcherTileEntity(grade);
	}

}
