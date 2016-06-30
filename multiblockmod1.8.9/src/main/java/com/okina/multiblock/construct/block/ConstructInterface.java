package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import com.okina.multiblock.construct.tileentity.ConstructInterfaceTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructInterface extends ConstructFunctionalBase {

	public ConstructInterface(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".interface");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructInterfaceTileEntity(grade);
	}

	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = Blocks.enchanting_table.getIcon(1, 0);
	//	}

	@Override
	public int getShiftLines() {
		return 4;
	}

}
