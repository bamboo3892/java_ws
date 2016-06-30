package com.okina.client.renderer;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.okina.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.okina.utils.RenderingHelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class TileConstructBaseRenderer extends TileEntitySpecialRenderer<ConstructBaseTileEntity> {

	private static final ResourceLocation WOOL_ORANGE = new ResourceLocation("textures/blocks/wool_colored_orange.png");
	private static final ResourceLocation WOOL_CYAN = new ResourceLocation("textures/blocks/wool_colored_cyan.png");

	@Override
	public void renderTileEntityAt(ConstructBaseTileEntity tile, double tileX, double tileY, double tileZ, float partialTicks, int destroyStage) {
		float ticks = tile.getWorld().getTotalWorldTime() + partialTicks;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		GlStateManager.pushMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		//io
		int[] flagIO = tile.flagIO;
		if(flagIO != null){
			GlStateManager.translate(tileX, tileY, tileZ);
			if(flagIO[0] == 0){
				bindTexture(WOOL_CYAN);
				RenderingHelper.renderCube(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
			}else if(flagIO[0] == 1){
				bindTexture(WOOL_ORANGE);
				RenderingHelper.renderCube(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
			}
			if(flagIO[1] == 0){
				bindTexture(WOOL_CYAN);
				RenderingHelper.renderCube(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
			}else if(flagIO[1] == 1){
				bindTexture(WOOL_ORANGE);
				RenderingHelper.renderCube(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
			}
			if(flagIO[2] == 0){
				bindTexture(WOOL_CYAN);
				RenderingHelper.renderCube(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
			}else if(flagIO[2] == 1){
				bindTexture(WOOL_ORANGE);
				RenderingHelper.renderCube(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
			}
			if(flagIO[3] == 0){
				bindTexture(WOOL_CYAN);
				RenderingHelper.renderCube(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
			}else if(flagIO[3] == 1){
				bindTexture(WOOL_ORANGE);
				RenderingHelper.renderCube(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
			}
			if(flagIO[4] == 0){
				bindTexture(WOOL_CYAN);
				RenderingHelper.renderCube(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
			}else if(flagIO[4] == 1){
				bindTexture(WOOL_ORANGE);
				RenderingHelper.renderCube(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
			}
			if(flagIO[5] == 0){
				bindTexture(WOOL_CYAN);
				RenderingHelper.renderCube(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
			}else if(flagIO[5] == 1){
				bindTexture(WOOL_ORANGE);
				RenderingHelper.renderCube(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
			}
			GlStateManager.translate(-tileX, -tileY, -tileZ);
		}

		//connection check box
		if(tile.restRenderTicks != 0){
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.disableTexture2D();
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
			rgb = color.getRGBColorComponents(null);
			float alpha = tile.restRenderTicks < 20 ? tile.restRenderTicks / 20f : 1f;

			GL11.glColor4f(rgb[0], rgb[1], rgb[2], alpha);

//			worldrenderer.putColorMultiplier(rgb[0], rgb[1], rgb[2], (int) (alpha * 255));
			worldrenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
//			GlStateManager.color(rgb[0], rgb[1], rgb[2], alpha);
//	        worldrenderer.pos(x + d4, y + (double)k, z + d5).tex(1.0D, d15).color(f1, f2, f3, 1.0F).endVertex();
//	        worldrenderer.startDrawing(GL11.GL_LINES);
//			worldrenderer.color(1, 1, 1, 1);
			//y neg
			worldrenderer.pos(tileX, tileY, tileZ).endVertex();
			worldrenderer.pos(tileX, tileY, tileZ + d).endVertex();
			worldrenderer.pos(tileX, tileY, tileZ + d).endVertex();
			worldrenderer.pos(tileX + d, tileY, tileZ + d).endVertex();
			worldrenderer.pos(tileX + d, tileY, tileZ + d).endVertex();
			worldrenderer.pos(tileX + d, tileY, tileZ).endVertex();
			worldrenderer.pos(tileX + d, tileY, tileZ).endVertex();
			worldrenderer.pos(tileX, tileY, tileZ).endVertex();
			//y pos
			worldrenderer.pos(tileX, tileY + d, tileZ).endVertex();
			worldrenderer.pos(tileX, tileY + d, tileZ + d).endVertex();
			worldrenderer.pos(tileX, tileY + d, tileZ + d).endVertex();
			worldrenderer.pos(tileX + d, tileY + d, tileZ + d).endVertex();
			worldrenderer.pos(tileX + d, tileY + d, tileZ + d).endVertex();
			worldrenderer.pos(tileX + d, tileY + d, tileZ).endVertex();
			worldrenderer.pos(tileX + d, tileY + d, tileZ).endVertex();
			worldrenderer.pos(tileX, tileY + d, tileZ).endVertex();
			//virtical
			worldrenderer.pos(tileX, tileY, tileZ).endVertex();
			worldrenderer.pos(tileX, tileY + d, tileZ).endVertex();
			worldrenderer.pos(tileX + d, tileY, tileZ).endVertex();
			worldrenderer.pos(tileX + d, tileY + d, tileZ).endVertex();
			worldrenderer.pos(tileX + d, tileY, tileZ + d).endVertex();
			worldrenderer.pos(tileX + d, tileY + d, tileZ + d).endVertex();
			worldrenderer.pos(tileX, tileY, tileZ + d).endVertex();
			worldrenderer.pos(tileX, tileY + d, tileZ + d).endVertex();

			tessellator.draw();

			tileX += 0.1D / 3.0D;
			tileY += 0.1D / 3.0D;
			tileZ += 0.1D / 3.0D;

			if(tile.renderSide != -1){
				alpha = tile.restRenderTicks < 20 ? tile.restRenderTicks / 40f : 0.5f;
				d = 1.0D;
				d2 = 1.0D;
				GL11.glColor4f(rgb[0], rgb[1], rgb[2], alpha);
				worldrenderer.begin(7, DefaultVertexFormats.POSITION);
				if(tile.renderSide == 0){
					//y neg
					worldrenderer.pos(tileX, tileY - 0.1D / 3.0D, tileZ).endVertex();
					worldrenderer.pos(tileX, tileY - 0.1D / 3.0D, tileZ + d).endVertex();
					worldrenderer.pos(tileX + d, tileY - 0.1D / 3.0D, tileZ + d).endVertex();
					worldrenderer.pos(tileX + d, tileY - 0.1D / 3.0D, tileZ).endVertex();
				}else if(tile.renderSide == 1){
					//y pos
					worldrenderer.pos(tileX, tileY + d + 0.1D / 3.0D, tileZ).endVertex();
					worldrenderer.pos(tileX + d, tileY + d + 0.1D / 3.0D, tileZ).endVertex();
					worldrenderer.pos(tileX + d, tileY + d + 0.1D / 3.0D, tileZ + d).endVertex();
					worldrenderer.pos(tileX, tileY + d + 0.1D / 3.0D, tileZ + d).endVertex();
				}else if(tile.renderSide == 4){
					//z neg
					worldrenderer.pos(tileX - 0.1D / 3.0D, tileY, tileZ).endVertex();
					worldrenderer.pos(tileX - 0.1D / 3.0D, tileY + d, tileZ).endVertex();
					worldrenderer.pos(tileX - 0.1D / 3.0D, tileY + d, tileZ + d).endVertex();
					worldrenderer.pos(tileX - 0.1D / 3.0D, tileY, tileZ + d).endVertex();
				}else if(tile.renderSide == 5){
					//z pos
					worldrenderer.pos(tileX + d + 0.1D / 3.0D, tileY, tileZ).endVertex();
					worldrenderer.pos(tileX + d + 0.1D / 3.0D, tileY, tileZ + d).endVertex();
					worldrenderer.pos(tileX + d + 0.1D / 3.0D, tileY + d, tileZ + d).endVertex();
					worldrenderer.pos(tileX + d + 0.1D / 3.0D, tileY + d, tileZ).endVertex();
				}else if(tile.renderSide == 2){
					//x neg
					worldrenderer.pos(tileX, tileY, tileZ - 0.1D / 3.0D).endVertex();
					worldrenderer.pos(tileX + d, tileY, tileZ - 0.1D / 3.0D).endVertex();
					worldrenderer.pos(tileX + d, tileY + d, tileZ - 0.1D / 3.0D).endVertex();
					worldrenderer.pos(tileX, tileY + d, tileZ - 0.1D / 3.0D).endVertex();
				}else if(tile.renderSide == 3){
					///x pos
					worldrenderer.pos(tileX, tileY, tileZ + d + 0.1D / 3.0D).endVertex();
					worldrenderer.pos(tileX, tileY + d, tileZ + d + 0.1D / 3.0D).endVertex();
					worldrenderer.pos(tileX + d, tileY + d, tileZ + d + 0.1D / 3.0D).endVertex();
					worldrenderer.pos(tileX + d, tileY, tileZ + d + 0.1D / 3.0D).endVertex();
				}
				tessellator.draw();
			}

			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			GlStateManager.enableCull();
			GlStateManager.enableLighting();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}

		GlStateManager.popMatrix();

//		if(tile instanceof ConstructContainerTileEntity){
//			this.renderContainer((ConstructContainerTileEntity) tile, tileX, tileY, tileZ, partialTicks);
//		}else if(tile instanceof ConstructCrusherTileEntity){
//			this.renderCrusher((ConstructCrusherTileEntity) tile, tileX, tileY, tileZ, partialTicks);
//		}
	}

}

//	private void renderContainer(ConstructContainerTileEntity container, double tileX, double tileY, double tileZ, float partialTicks) {
//		if(container.getStackInSlot(0) != null){
//			float ticks = container.getWorldObj().getTotalWorldTime() + partialTicks;
//
//			GL11.glPushMatrix();
//			GL11.glTranslatef((float) tileX + 0.5F, (float) tileY + 0.3F, (float) tileZ + 0.5F);
//			GL11.glScalef(2F, 2F, 2F);
//			GL11.glRotatef(0.1F * ticks % 180.0F, 0.0F, 1.0F, 0.0F);
//
//			EntityItem entityitem = null;
//			ItemStack is = container.getStackInSlot(0).copy();
//			is.stackSize = 1;
//			entityitem = new EntityItem(container.getWorldObj(), 0.0D, 0.0D, 0.0D, is);
//			entityitem.hoverStart = 0.0F;
//
//			RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
//
//			if(container.mode == 2 && container.items[0].stackSize >= 2 && Block.getBlockFromItem(container.items[0].getItem()) != null){
//				RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.25D, 0.0D, 0.0F, 0.0F);
//			}
//			GL11.glPopMatrix();
//		}
//	}
//
//	private void renderCrusher(ConstructCrusherTileEntity crusher, double tileX, double tileY, double tileZ, float partialTicks) {
//		if(crusher.cactus != null){
//			float ticks = crusher.getWorldObj().getTotalWorldTime() + partialTicks;
//			float rotateSpeed;
//			float progress;
//			if(crusher.container == null || crusher.container.isInvalid() || crusher.container.processingTicks == -1){
//				rotateSpeed = 3;
//				progress = 0;
//			}else{
//				rotateSpeed = 10;
//				progress = crusher.container.processingTicks + partialTicks;
//			}
//			GL11.glPushMatrix();
//			GL11.glTranslatef((float) tileX + 0.5F, (float) tileY + 0.5F, (float) tileZ + 0.5F);
//			int meta = crusher.getBlockMetadata();
//			if(meta == 0){
//				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 0.0F, 1.0F, 0.0F);
//				GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
//				GL11.glScalef(2F, 3F, 2F);
//			}else if(meta == 1){
//				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 0.0F, 1.0F, 0.0F);
//				GL11.glScalef(2F, 3F, 2F);
//			}else if(meta == 2){
//				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 0.0F, 0.0F, 1.0F);
//				GL11.glRotatef(270F, 1.0F, 0.0F, 0.0F);
//				GL11.glScalef(2F, 3F, 2F);
//			}else if(meta == 3){
//				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 0.0F, 0.0F, 1.0F);
//				GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
//				GL11.glScalef(2F, 3F, 2F);
//			}else if(meta == 4){
//				GL11.glRotatef(-rotateSpeed * ticks % 360.0F, 1.0F, 0.0F, 0.0F);
//				GL11.glRotatef(90F, 0.0F, 0.0F, 1.0F);
//				GL11.glScalef(2F, 3F, 2F);
//			}else if(meta == 5){
//				GL11.glRotatef(rotateSpeed * ticks % 360.0F, 1.0F, 0.0F, 0.0F);
//				GL11.glRotatef(270F, 0.0F, 0.0F, 1.0F);
//				GL11.glScalef(2F, 3F, 2F);
//			}
//			GL11.glTranslatef(0, (float) (0.078 * (1 - Math.cos(progress / 10 * Math.PI * 2))) - 0.055F, 0);
//			EntityItem entity = new EntityItem(crusher.getWorldObj(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.cactus, 1));
//			entity.hoverStart = 0.0F;
//			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
//			GL11.glPopMatrix();
//		}
//	}
