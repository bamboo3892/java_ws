package com.test.client.gui;

import org.lwjgl.opengl.GL11;

import com.test.main.TestCore;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.server.gui.SliderInputContainer;
import com.test.tileentity.IGuiSliderUser;
import com.test.utils.Position;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiSliderInput extends GuiContainer {

	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(TestCore.MODID + ":textures/gui/container/sliderinput.png");

	public IGuiSliderUser tile;
	public GuiSlider slider;

	public GuiSliderInput(IGuiSliderUser tile) {
		super(new SliderInputContainer(tile));
		this.tile = tile;
		this.xSize = 146;
		this.ySize = 40;
	}

	@Override
	public void initGui() {
		super.initGui();
		int i = width - xSize >> 1;
		int j = height - ySize >> 1;

		buttonList.add(new GuiButton(0, i + 4, j + 16, 15, 20, "-"));
		buttonList.add(new GuiButton(1, i + 127, j + 16, 15, 20, "+"));
		slider = new GuiSlider(2, i + 23, j + 16, 100, 20, tile);
		buttonList.add(slider);
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if(!guiButton.enabled){
			return;
		}
		int value = slider.getValue();
		if(guiButton.id == 0){
			value = value == 0 ? 0 : value - 1;
		}else if(guiButton.id == 1){
			value = value == tile.getMaxValue() ? value : value + 1;
		}
		slider.setValue(value);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container." + tile.getContainerName()), 4, 4, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int xMouse, int yMouse) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.zLevel = 10;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void onGuiClosed() {
		Position p = tile.getPosition();
		TestCore.proxy.sendPacket(new SimpleTilePacket(tile, PacketType.SLIDER_INPUT, slider.getValue()));
		super.onGuiClosed();
	}

	@Override
	protected void keyTyped(char p_73869_1_, int key) {
		if(key == 1 || key == this.mc.gameSettings.keyBindInventory.getKeyCode()){
			this.mc.thePlayer.closeScreen();
		}
	}

}
