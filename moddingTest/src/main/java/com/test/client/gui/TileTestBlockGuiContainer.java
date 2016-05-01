package com.test.client.gui;

import com.test.server.gui.TileTestBlockContainer;
import com.test.tileentity.TileTestBlockTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class TileTestBlockGuiContainer extends GuiContainer{

	public TileTestBlockGuiContainer(InventoryPlayer inventoryPlayer, TileEntity pTileEntity) {
		super(new TileTestBlockContainer(inventoryPlayer, (TileTestBlockTileEntity) pTileEntity));
		TileTestBlockTileEntity tileEntity = (TileTestBlockTileEntity) pTileEntity;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {

	}



}
