package com.test.client.renderer;

import org.lwjgl.opengl.GL11;

import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileContainerRenderer extends TileEntitySpecialRenderer {

	public TileContainerRenderer() {

	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		if(tileEntity instanceof ConstructContainerTileEntity){
			ConstructContainerTileEntity container = (ConstructContainerTileEntity) tileEntity;
			if(container.getStackInSlot(0) != null){
				EntityItem entityitem = null;
				float ticks = container.getWorldObj().getTotalWorldTime() + partialTicks;
				GL11.glPushMatrix();
				GL11.glTranslatef((float) x + 0.5F, (float) y + 0.3F, (float) z + 0.5F);
				GL11.glScalef(2F, 2F, 2F);
				GL11.glRotatef(0.1F * ticks % 180.0F, 0.0F, 1.0F, 0.0F);

				ItemStack is = container.getStackInSlot(0);
				is.stackSize = 1;
				entityitem = new EntityItem(container.getWorldObj(), 0.0D, 0.0D, 0.0D, is);
				entityitem.hoverStart = 0.0F;

				if(container.mode == 2){
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glDepthMask(true);
					GL11.glColor4f(1F, 1F, 1F, 0.5F);
				}

				GL11.glDisable(GL11.GL_CULL_FACE);
				RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
				if(container.mode == 2 && container.doubled && Block.getBlockFromItem(container.items[0].getItem()) == Blocks.cactus){
					RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.25D, 0.0D, 0.0F, 0.0F);
				}
				GL11.glPopMatrix();
			}
		}
	}

}
