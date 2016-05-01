package com.test.server.gui;

import com.test.tileentity.TileTestBlockTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class TileTestBlockContainer extends Container {

	public TileTestBlockContainer(InventoryPlayer inventoryPlayer, TileEntity pTileEntity) {
		TileTestBlockTileEntity tileEntity = (TileTestBlockTileEntity) pTileEntity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return true;
	}

}
