package com.test.multiblock.construct.block;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructEventCatcherTileEntity;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructEventCatcher extends ConstructFunctionalBase {

	public ConstructEventCatcher(int grade) {
		super(grade);
		setBlockName("eventCatcher");
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(TestCore.MODID + ":event_catcher");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructEventCatcherTileEntity(grade);
	}

}
