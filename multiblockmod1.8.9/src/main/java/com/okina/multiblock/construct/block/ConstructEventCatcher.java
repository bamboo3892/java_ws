package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import com.okina.multiblock.construct.tileentity.ConstructEventCatcherTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructEventCatcher extends ConstructFunctionalBase {

	public ConstructEventCatcher(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".eventCatcher");
	}

	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = register.registerIcon(TestCore.MODID + ":event_catcher");
	//	}

	@Override
	public int getShiftLines() {
		return 2;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructEventCatcherTileEntity(grade);
	}

}
