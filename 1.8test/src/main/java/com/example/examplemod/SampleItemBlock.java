package com.example.examplemod;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SampleItemBlock extends ItemBlock {
	public SampleItemBlock(Block block) {
		super(block);
	}

	/**
    Layerが機能しているかの確認。
    */
   @SideOnly(Side.CLIENT)
   public int getColorFromItemStack(ItemStack stack, int renderPass){
       return block.colorMultiplier(null, null, renderPass);
   }
	
}
