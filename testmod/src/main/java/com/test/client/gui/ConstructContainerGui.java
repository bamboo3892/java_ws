package com.test.client.gui;

import org.lwjgl.opengl.GL11;

import com.test.main.TestCore;
import com.test.server.gui.ConstructContainerContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class ConstructContainerGui extends GuiContainer {

	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(TestCore.MODID + ":textures/gui/container/container.png");

	private IInventory containerInv;

	public ConstructContainerGui(InventoryPlayer playerInv, IInventory containerInv) {
		super(new ConstructContainerContainer(playerInv, containerInv));
		this.containerInv = containerInv;
		this.xSize = 176;
		this.ySize = 166;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRendererObj.drawString(this.containerInv.hasCustomInventoryName() ? this.containerInv.getInventoryName() : I18n.format(this.containerInv.getInventoryName(), new Object[0]), 8, 6, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}

}
