package com.test.client.renderer;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.test.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;
import com.test.multiblock.construct.tileentity.ConstructCrusherTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileConstructBaseRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tile, double tileX, double tileY, double tileZ, float partialTicks) {
		if(tile instanceof ConstructBaseTileEntity){
			ConstructBaseTileEntity baseTile = (ConstructBaseTileEntity) tile;
			float ticks = tile.getWorldObj().getTotalWorldTime() + partialTicks;
			Tessellator tessellator = Tessellator.instance;

			if(baseTile.restRenderTicks != 0){
				//onnection check box
				GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDepthMask(false);
				GL11.glDisable(GL11.GL_CULL_FACE);
				float pastWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
				GL11.glLineWidth(3.0f);

				double d;
				double d2;
				d = 1.0D + 0.2D / 3.0D;
				d2 = 1.0D + 0.2D / 3.0D;
				tileX -= 0.1D / 3.0D;
				tileY -= 0.1D / 3.0D;
				tileZ -= 0.1D / 3.0D;
				float hue = (float) ((ticks % 100D) / 100D);
				Color color = Color.getHSBColor(hue, 1f, 1f);
				float[] rgb = null;
				rgb = color.getRGBColorComponents(rgb);
				float alpha = baseTile.restRenderTicks < 20 ? baseTile.restRenderTicks / 20f : 1f;

				tessellator.startDrawing(GL11.GL_LINES);
				tessellator.setColorRGBA_F(rgb[0], rgb[1], rgb[2], alpha);
				//y neg
				tessellator.addVertex(tileX, tileY, tileZ);
				tessellator.addVertex(tileX, tileY, tileZ + d);
				tessellator.addVertex(tileX, tileY, tileZ + d);
				tessellator.addVertex(tileX + d, tileY, tileZ + d);
				tessellator.addVertex(tileX + d, tileY, tileZ + d);
				tessellator.addVertex(tileX + d, tileY, tileZ);
				tessellator.addVertex(tileX + d, tileY, tileZ);
				tessellator.addVertex(tileX, tileY, tileZ);
				//y pos
				tessellator.addVertex(tileX, tileY + d, tileZ);
				tessellator.addVertex(tileX, tileY + d, tileZ + d);
				tessellator.addVertex(tileX, tileY + d, tileZ + d);
				tessellator.addVertex(tileX + d, tileY + d, tileZ + d);
				tessellator.addVertex(tileX + d, tileY + d, tileZ + d);
				tessellator.addVertex(tileX + d, tileY + d, tileZ);
				tessellator.addVertex(tileX + d, tileY + d, tileZ);
				tessellator.addVertex(tileX, tileY + d, tileZ);
				//virtical
				tessellator.addVertex(tileX, tileY, tileZ);
				tessellator.addVertex(tileX, tileY + d, tileZ);
				tessellator.addVertex(tileX + d, tileY, tileZ);
				tessellator.addVertex(tileX + d, tileY + d, tileZ);
				tessellator.addVertex(tileX + d, tileY, tileZ + d);
				tessellator.addVertex(tileX + d, tileY + d, tileZ + d);
				tessellator.addVertex(tileX, tileY, tileZ + d);
				tessellator.addVertex(tileX, tileY + d, tileZ + d);
				tessellator.draw();

				tileX += 0.1D / 3.0D;
				tileY += 0.1D / 3.0D;
				tileZ += 0.1D / 3.0D;

				if(baseTile.renderSide != -1){
					alpha = baseTile.restRenderTicks < 20 ? baseTile.restRenderTicks / 40f : 0.5f;
					d = 1.0D;
					d2 = 1.0D;
					tessellator.startDrawing(7);
					tessellator.setColorRGBA_F(rgb[0], rgb[1], rgb[2], alpha);
					if(baseTile.renderSide == 0){
						//y neg
						tessellator.addVertex(tileX, tileY - 0.1D / 3.0D, tileZ);
						tessellator.addVertex(tileX, tileY - 0.1D / 3.0D, tileZ + d);
						tessellator.addVertex(tileX + d, tileY - 0.1D / 3.0D, tileZ + d);
						tessellator.addVertex(tileX + d, tileY - 0.1D / 3.0D, tileZ);
					}else if(baseTile.renderSide == 1){
						//y pos
						tessellator.addVertex(tileX, tileY + d + 0.1D / 3.0D, tileZ);
						tessellator.addVertex(tileX + d, tileY + d + 0.1D / 3.0D, tileZ);
						tessellator.addVertex(tileX + d, tileY + d + 0.1D / 3.0D, tileZ + d);
						tessellator.addVertex(tileX, tileY + d + 0.1D / 3.0D, tileZ + d);
					}else if(baseTile.renderSide == 4){
						//z neg
						tessellator.addVertex(tileX - 0.1D / 3.0D, tileY, tileZ);
						tessellator.addVertex(tileX - 0.1D / 3.0D, tileY + d, tileZ);
						tessellator.addVertex(tileX - 0.1D / 3.0D, tileY + d, tileZ + d);
						tessellator.addVertex(tileX - 0.1D / 3.0D, tileY, tileZ + d);
					}else if(baseTile.renderSide == 5){
						//z pos
						tessellator.addVertex(tileX + d + 0.1D / 3.0D, tileY, tileZ);
						tessellator.addVertex(tileX + d + 0.1D / 3.0D, tileY, tileZ + d);
						tessellator.addVertex(tileX + d + 0.1D / 3.0D, tileY + d, tileZ + d);
						tessellator.addVertex(tileX + d + 0.1D / 3.0D, tileY + d, tileZ);
					}else if(baseTile.renderSide == 2){
						//x neg
						tessellator.addVertex(tileX, tileY, tileZ - 0.1D / 3.0D);
						tessellator.addVertex(tileX + d, tileY, tileZ - 0.1D / 3.0D);
						tessellator.addVertex(tileX + d, tileY + d, tileZ - 0.1D / 3.0D);
						tessellator.addVertex(tileX, tileY + d, tileZ - 0.1D / 3.0D);
					}else if(baseTile.renderSide == 3){
						///x pos
						tessellator.addVertex(tileX, tileY, tileZ + d + 0.1D / 3.0D);
						tessellator.addVertex(tileX, tileY + d, tileZ + d + 0.1D / 3.0D);
						tessellator.addVertex(tileX + d, tileY + d, tileZ + d + 0.1D / 3.0D);
						tessellator.addVertex(tileX + d, tileY, tileZ + d + 0.1D / 3.0D);
					}
					tessellator.draw();
				}
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glLineWidth(pastWidth);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();
			}

			if(tile instanceof ConstructContainerTileEntity){
				this.renderContainer((ConstructContainerTileEntity) tile, tileX, tileY, tileZ, partialTicks);
			}else if(tile instanceof ConstructCrusherTileEntity){
				this.renderCrusher((ConstructCrusherTileEntity) tile, tileX, tileY, tileZ, partialTicks);
			}
		}

	}

	private void renderContainer(ConstructContainerTileEntity container, double tileX, double tileY, double tileZ, float partialTicks) {
		if(container.getStackInSlot(0) != null){
			float ticks = container.getWorldObj().getTotalWorldTime() + partialTicks;

			GL11.glPushMatrix();
			GL11.glTranslatef((float) tileX + 0.5F, (float) tileY + 0.3F, (float) tileZ + 0.5F);
			GL11.glScalef(2F, 2F, 2F);
			GL11.glRotatef(0.1F * ticks % 180.0F, 0.0F, 1.0F, 0.0F);

			EntityItem entityitem = null;
			ItemStack is = container.getStackInSlot(0).copy();
			is.stackSize = 1;
			entityitem = new EntityItem(container.getWorldObj(), 0.0D, 0.0D, 0.0D, is);
			entityitem.hoverStart = 0.0F;

			RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

			if(container.mode == 2 && container.items[0].stackSize >= 2 && Block.getBlockFromItem(container.items[0].getItem()) == Blocks.cactus){
				RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.25D, 0.0D, 0.0F, 0.0F);
			}
			GL11.glPopMatrix();
		}
	}

	private void renderCrusher(ConstructCrusherTileEntity crusher, double tileX, double tileY, double tileZ, float partialTicks) {
		if(crusher.cactus != null){
			float ticks = crusher.getWorldObj().getTotalWorldTime() + partialTicks;
			float rotateSpeed;
			float progress;
			if(crusher.container == null || crusher.container.isInvalid() || crusher.container.processingTicks == -1){
				rotateSpeed = 3;
				progress = 0;
			}else{
				rotateSpeed = 10;
				progress = crusher.container.processingTicks + partialTicks;
			}
			GL11.glPushMatrix();
			GL11.glTranslatef((float) tileX + 0.5F, (float) tileY + 0.5F, (float) tileZ + 0.5F);
			int meta = crusher.getBlockMetadata();
			if(meta == 0){
				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 1){
				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 2){
				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(270F, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 3){
				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 4){
				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
				GL11.glScalef(2F, 3F, 2F);
			}else if(meta == 5){
				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(270F, 0.0F, 0.0F, 1.0F);
				GL11.glScalef(2F, 3F, 2F);
			}
			GL11.glTranslatef(0, (float) (0.078 * (1 - Math.cos(progress / 10 * Math.PI * 2))) - 0.055F, 0);
			EntityItem entity = new EntityItem(crusher.getWorldObj(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.cactus, 1));
			entity.hoverStart = 0.0F;
			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
			GL11.glPopMatrix();
		}
	}

}