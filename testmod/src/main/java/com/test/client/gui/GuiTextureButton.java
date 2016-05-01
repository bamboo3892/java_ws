package com.test.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class GuiTextureButton extends GuiButton {

	private final ResourceLocation texture;
	private final int offsetX;
	private final int offsetY;
	public boolean riseUp;
	private static final String __OBFID = "CL_00000743";

	protected GuiTextureButton(int buttonId, int startX, int startY, ResourceLocation texture, int offsetX, int offsetY) {
		super(buttonId, startX, startY, 20, 20, "");
		this.texture = texture;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if(this.visible){
			FontRenderer fontrenderer = minecraft.fontRenderer;
			minecraft.getTextureManager().bindTexture(buttonTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			int k = this.getHoverState(this.field_146123_n);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			if(riseUp){
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
				this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
				minecraft.getTextureManager().bindTexture(this.texture);
				this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, this.offsetX, this.offsetY, this.width - 4, this.height - 4);
			}else{
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46, this.width / 2, this.height);
				this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46, this.width / 2, this.height);
				minecraft.getTextureManager().bindTexture(this.texture);
				this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, this.offsetX, this.offsetY + 16, this.width - 4, this.height - 4);
			}

			this.mouseDragged(minecraft, mouseX, mouseY);
		}
	}

}
