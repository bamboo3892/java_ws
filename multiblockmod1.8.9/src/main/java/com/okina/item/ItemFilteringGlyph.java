package com.okina.item;

import java.awt.Color;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFilteringGlyph extends Item {

	private float baseColor;

	public ItemFilteringGlyph() {
		this(0x000000);
	}

	public ItemFilteringGlyph(float baseColor) {
		this.baseColor = baseColor;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		double ticks = FMLClientHandler.instance().getWorldClient().getTotalWorldTime();
		float b = (float) Math.sin((ticks % 40d) / 40d * 2 * Math.PI) * 0.3f + 0.7f;
		return Color.HSBtoRGB(baseColor, 1f, b);
	}

}
