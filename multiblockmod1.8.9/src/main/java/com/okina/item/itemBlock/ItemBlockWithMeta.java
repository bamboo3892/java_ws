package com.okina.item.itemBlock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockWithMeta extends ItemBlock {

	//	protected Block block;

	public ItemBlockWithMeta(Block block) {
		super(block);
		//		this.block = block;
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return super.getUnlocalizedName() + "." + itemStack.getItemDamage();
	}

	//	@SideOnly(Side.CLIENT)
	//	@Override
	//	public IIcon getIconFromDamage(int p_77617_1_) {
	//		return this.block.getIcon(2, p_77617_1_);
	//	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

}
