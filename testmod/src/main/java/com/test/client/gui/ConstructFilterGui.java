package com.test.client.gui;

import org.lwjgl.opengl.GL11;

import com.test.inventory.FilterInventory;
import com.test.inventory.IFilterUser;
import com.test.main.TestCore;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.server.gui.ConstructFilterContainer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class ConstructFilterGui extends GuiContainer {

	private final static ResourceLocation GUI_BACKGROUND = new ResourceLocation(TestCore.MODID + ":textures/gui/container/filter.png");
	private static final ResourceLocation buttonTextures = new ResourceLocation(TestCore.MODID + ":textures/gui/container/filter_buttons.png");
	private IInventory player;
	private IFilterUser tile;
	private FilterInventory filter;
	private int side;
	private GuiTextureButton[] buttons = new GuiTextureButton[4];
	private int priority;

	public ConstructFilterGui(IInventory player, IFilterUser tile, int side) {
		super(new ConstructFilterContainer(player, (FilterInventory) tile.getFilter(side)));
		this.player = player;
		this.tile = tile;
		this.filter = (FilterInventory) tile.getFilter(side);
		this.priority = filter.priority;
		this.side = side;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	public void initGui() {
		super.initGui();
		int i = width - xSize >> 1;
		int j = height - ySize >> 1;

		buttons[0] = new GuiTextureButton(0, i + 121, j + 21, buttonTextures, 0, 0);
		buttons[1] = new GuiTextureButton(1, i + 146, j + 21, buttonTextures, 16, 0);
		buttons[2] = new GuiTextureButton(2, i + 121, j + 45, buttonTextures, 32, 0);
		buttons[3] = new GuiTextureButton(3, i + 146, j + 45, buttonTextures, 48, 0);
		buttons[0].riseUp = filter.useDamage;
		buttons[1].riseUp = filter.useNBT;
		buttons[2].riseUp = filter.useOreDictionary;
		buttons[3].riseUp = filter.filterBan;
		buttonList.add(buttons[0]);
		buttonList.add(buttons[1]);
		buttonList.add(buttons[2]);
		buttonList.add(buttons[3]);

		buttonList.add(new GuiButton(4, i + 8, j + 39, 15, 20, "-"));
		buttonList.add(new GuiButton(5, i + 42, j + 39, 15, 20, "+"));
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if(!guiButton.enabled){
			return;
		}
		if(guiButton instanceof GuiTextureButton){
			GuiTextureButton button = (GuiTextureButton) guiButton;
			button.riseUp = !button.riseUp;
		}
		if(guiButton.id == 4){
			this.priority = this.priority <= 1 ? 1 : this.priority - 1;
		}else if(guiButton.id == 5){
			this.priority = this.priority >= 6 ? 6 : this.priority + 1;
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse) {
		this.fontRendererObj.drawString(StatCollector.translateToLocal(this.filter.getInventoryName()), 8, 6, 0x404040);
		this.fontRendererObj.drawString(StatCollector.translateToLocal("container.filter"), 8, this.ySize - 96 + 2, 0x404040);
		this.fontRendererObj.drawString("Priority", 12, 22, 0x404040);
		this.fontRendererObj.drawString("" + priority, 30, 46, 0xFF4040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialRenderTick, int xMouse, int yMouse) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void onGuiClosed() {
		String value = side + "";
		for (int i = 0; i < 4; i++){
			value += buttons[i].riseUp ? 1 : 0;
		}
		value += priority;
		TestCore.proxy.sendPacket(new SimpleTilePacket(tile.getPosition(), PacketType.FILTER, value));
		super.onGuiClosed();
		//Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void keyTyped(char p_73869_1_, int key) {
		if(key == 1 || key == this.mc.gameSettings.keyBindInventory.getKeyCode()){
			this.mc.thePlayer.closeScreen();
		}
	}

}
