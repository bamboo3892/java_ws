package com.test.client.renderer;

import org.lwjgl.opengl.GL11;

import com.test.multiblock.construct.tileentity.ConstructCrusherTileEntity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileCrusherRenderer extends TileEntitySpecialRenderer {

	public TileCrusherRenderer() {

	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
		if(tile instanceof ConstructCrusherTileEntity){
			ConstructCrusherTileEntity crusher = (ConstructCrusherTileEntity) tile;
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
				GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
				int meta = tile.getBlockMetadata();
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
				EntityItem entity = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.cactus, 1));
				entity.hoverStart = 0.0F;
				RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
				GL11.glPopMatrix();
			}

		}
	}

}
