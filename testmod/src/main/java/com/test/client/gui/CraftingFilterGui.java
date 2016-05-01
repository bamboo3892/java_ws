package com.test.client.gui;

import org.lwjgl.opengl.GL11;

import com.test.inventory.CraftingFilterInventory;
import com.test.main.TestCore;
import com.test.server.gui.CraftingFilterContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CraftingFilterGui extends GuiContainer {

	private static final ResourceLocation designTableGuiTextures = new ResourceLocation(TestCore.MODID + ":textures/gui/container/design_table.png");

	public CraftingFilterGui(InventoryPlayer inventoryPlayer, CraftingFilterInventory filter, World world) {
		super(new CraftingFilterContainer(inventoryPlayer, filter, world));
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRendererObj.drawString("Crafting Filter", 28, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}

	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(designTableGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

}
