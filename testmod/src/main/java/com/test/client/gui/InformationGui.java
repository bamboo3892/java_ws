package com.test.client.gui;

import org.lwjgl.opengl.GL11;

import com.test.item.TestItem;
import com.test.server.gui.DummyContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class InformationGui extends GuiContainer{

	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation("gui:textures/gui/container/base.png");
	private int xSize1;
	private int ySize1;
	private EntityPlayer player;

	public InformationGui(EntityPlayer player) {
		super(new DummyContainer());
		this.xSize1 = 176;
		this.ySize1 = 166;
		this.player = player;
	}

	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse){
		Item item = this.player.getCurrentEquippedItem().getItem();
		if(!(item instanceof TestItem)) return;
		TestItem item2 = (TestItem)item;
		String[] messages = item2.getMessage(player.worldObj, player);
		for(int i=0;i<messages.length;i++){
			this.fontRendererObj.drawString(messages[i], 8, 6+10*i, 0x404040);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialRenderTick, int xMouse, int yMouse) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int x = (this.width  - this.xSize1) / 2;
		int y = (this.height - this.ySize1) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize1, this.ySize1);
	}

}
