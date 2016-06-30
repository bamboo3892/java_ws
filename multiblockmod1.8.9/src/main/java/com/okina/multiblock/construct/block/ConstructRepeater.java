package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import com.okina.multiblock.construct.tileentity.ConstructRepeaterTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructRepeater extends ConstructFunctionalBase {

	public ConstructRepeater(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".repeater");
	}

	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = Blocks.quartz_block.getIcon(2, 0);
	//	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructRepeaterTileEntity(grade);
	}

	@Override
	public int getShiftLines() {
		return 1;
	}

}
