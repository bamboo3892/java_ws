package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import com.okina.multiblock.construct.tileentity.ConstructClockPulserTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructClockPulser extends ConstructFunctionalBase {

	public ConstructClockPulser(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".clockPulser");
	}

	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = Blocks.redstone_block.getIcon(0, 0);
	//	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructClockPulserTileEntity(grade);
	}

	@Override
	public int getShiftLines() {
		return 2;
	}

}
