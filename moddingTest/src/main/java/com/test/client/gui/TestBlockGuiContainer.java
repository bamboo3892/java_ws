package com.test.client.gui;

import com.test.server.gui.TestBlockContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

public class TestBlockGuiContainer extends GuiContainer{

	public TestBlockGuiContainer(InventoryPlayer inventoryPlayer) {
		super(new TestBlockContainer(inventoryPlayer));
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {

	}

}
