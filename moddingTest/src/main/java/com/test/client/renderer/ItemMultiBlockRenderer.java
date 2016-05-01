package com.test.client.renderer;

import org.lwjgl.opengl.GL11;

import com.test.main.TestCore;
import com.test.utils.RenderingHelper;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemMultiBlockRenderer implements IItemRenderer{

	public ItemMultiBlockRenderer() {
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type){
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		switch(type){
			case ENTITY:
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
			case INVENTORY:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper){
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		switch (helper){
			case INVENTORY_BLOCK:
			case ENTITY_BOBBING:
			case ENTITY_ROTATION:
				return true;
			default:
				return false;
		}
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data){
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		if (handleRenderType(item, type)){
			GL11.glPushMatrix();
			switch(type){
				case INVENTORY:
					glMatrixForRenderInInventory(); break;
				case EQUIPPED:
				case EQUIPPED_FIRST_PERSON:
					glMatrixForRenderInEquipped();
					break;
				case ENTITY:
					glMatrixForRenderInEntity();
			case FIRST_PERSON_MAP:
				break;
			default:
				break;
			}
			RenderBlocks.getInstance().setOverrideBlockTexture(Blocks.cobblestone.getBlockTextureFromSide(0));
			RenderingHelper.renderInvCubeFrame(RenderBlocks.getInstance(), TestCore.multiBlock, 0, 0, 0, 1, 1, 1, 3F/16F);
			RenderBlocks.getInstance().clearOverrideBlockTexture();
			GL11.glPopMatrix();
		}
	}

	private void glMatrixForRenderInInventory(){
		GL11.glRotatef(-180F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, -1.0F, 0.0F);
	}

	private void glMatrixForRenderInEquipped(){
		GL11.glRotatef(-245F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.25F, -1.0F, 0.0F);
	}

	private void glMatrixForRenderInEntity(){
		GL11.glRotatef(-180F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, -1.5F, 0.0F);
	}

}