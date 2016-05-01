package com.test.client.renderer;

import org.lwjgl.opengl.GL11;

import com.test.client.model.MultiBlockModel;
import com.test.main.TestCore;
import com.test.multiblock.MultiBlockCoreTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileMultiBlockRenderer extends TileEntitySpecialRenderer {

	public static MultiBlockModel model1 = new MultiBlockModel();
	public static MultiBlockModel model2 = new MultiBlockModel();
	public static ResourceLocation textures = new ResourceLocation(TestCore.MODID + ":textures/blocks/multi_block.png");
	public static ResourceLocation background = new ResourceLocation(TestCore.MODID + ":textures/blocks/multi_block_background.png");

	/**coordinates is calculated from view point*/
	@Override
	public void renderTileEntityAt(TileEntity tile, double tileX, double tileY, double tileZ, float partialTicks) {
		//this.renderEndPortal(tile, tileX, tileY, tileZ, partialTicks);

		//		int n;
		//		n = GL11.GL_LINES;//vertex should not be odd number
		//		n = GL11.GL_LINE_LOOP;//draw a line loop
		//		n = GL11.GL_LINE_STRIP;//draw not looped lines
		//		n = GL11.GL_TRIANGLES;//draw triangle
		//		n = GL11.GL_TRIANGLE_STRIP;//draw triangle (render both side)
		//		n = GL11.GL_TRIANGLE_FAN;//draw triangle connected with next one
		//		n = GL11.GL_QUADS;//normal quad
		//		n = GL11.GL_QUAD_STRIP;//strip triangle?
		//		n = GL11.GL_POLYGON;
		//		//and so on

		if(tile instanceof MultiBlockCoreTileEntity){
			MultiBlockCoreTileEntity core = (MultiBlockCoreTileEntity) tile;

			Minecraft.getMinecraft().renderEngine.bindTexture(background);
			Tessellator tessellator = Tessellator.instance;
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDepthMask(false);

			double d;
			double d2;
			tessellator.startDrawing(7);
			//tessellator.startDrawingQuads();
			tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 0.5F);
			if(core.connected){
				d = 2.8D;
				d2 = 2.8D;
				tileX -= 0.9D;
				tileY -= 0.9D;
				tileZ -= 0.9D;
			}else{
				d = 1.0D - 0.2D / 3.0D;
				d2 = 1.0D - 0.2D / 3.0D;
				tileX += 0.1D / 3.0D;
				tileY += 0.1D / 3.0D;
				tileZ += 0.1D / 3.0D;
			}
			//y neg
			tessellator.addVertexWithUV(tileX, tileY, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX, tileY, tileZ + d, 0, d2);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ, d2, 0);
			//y pos
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ, 0, d2);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ + d, d2, 0);
			//z neg
			tessellator.addVertexWithUV(tileX, tileY, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ, d2, 0);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX, tileY, tileZ + d, 0, d2);
			//z pos
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ + d, d2, 0);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ, 0, d2);
			//x neg
			tessellator.addVertexWithUV(tileX, tileY, tileZ, 0, 0);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ, 0, d2);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ, d2, d2);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ, d2, 0);
			///x pos
			tessellator.addVertexWithUV(tileX, tileY, tileZ + d, 0, 0);
			tessellator.addVertexWithUV(tileX, tileY + d, tileZ + d, 0, d2);
			tessellator.addVertexWithUV(tileX + d, tileY + d, tileZ + d, d2, d2);
			tessellator.addVertexWithUV(tileX + d, tileY, tileZ + d, d2, 0);
			tessellator.draw();

			if(core.connected){
				tileX += 0.9D;
				tileY += 0.9D;
				tileZ += 0.9D;
			}else{
				tileX -= 0.1D / 3.0D;
				tileY -= 0.1D / 3.0D;
				tileZ -= 0.1D / 3.0D;
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_LIGHTING);

			int maxLength = Math.max(core.xSize, Math.max(core.ySize, core.zSize));
			double ticks = tile.getWorldObj().getTotalWorldTime() + partialTicks;
			GL11.glTranslatef((float) tileX + 0.5f, (float) tileY + 0.5f, (float) tileZ + 0.5f);
			Minecraft.getMinecraft().renderEngine.bindTexture(textures);
			if(core.connected){
				model1.render(null, 0, 0, 0, 0, 0, 3f / 16f);
				GL11.glScalef(2f / (float) maxLength, 2f / (float) maxLength, 2f / (float) maxLength);
			}else{
				model2.render(null, 0, 0, 0, 0, 0, 1f / 16f);
				GL11.glScalef(0.5f / (float) maxLength, 0.5f / (float) maxLength, 0.5f / (float) maxLength);
			}
			if(core.renderDetail){
				GL11.glRotatef(tile.getBlockMetadata() * -90, 0, 1, 0);
				GL11.glTranslatef(-(float) core.xSize / 2f + 0.5f, -(float) core.ySize / 2f + 0.5f, -(float) core.zSize / 2f + 0.5f);
				for (int i = 0; i < core.xSize; i++){
					for (int j = 0; j < core.ySize; j++){
						for (int k = 0; k < core.zSize; k++){
							if(core.parts[i][j][k] != null){
								GL11.glTranslatef(i, j, k);
								core.parts[i][j][k].renderPart(ticks);
								GL11.glTranslatef(-i, -j, -k);
							}
						}
					}
				}
			}
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
	}

	//	private static final ResourceLocation textureEndSky = new ResourceLocation("textures/environment/end_sky.png");
	//	private static final ResourceLocation textureEndPortal = new ResourceLocation("textures/entity/end_portal.png");
	//	private static final Random random = new Random(31100L);
	//	private FloatBuffer floatBuf = GLAllocation.createDirectFloatBuffer(16);
	//
	//	public void renderEndPortal(TileEntity tile, double tileX, double tileY, double tileZ, float partialTicks) {
	//		EntityItem entity = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, new ItemStack(Blocks.cactus, 1));
	//		entity.hoverStart = 0.0F;
	//
	//		float playerX = (float) this.field_147501_a.field_147560_j;
	//		float playerY = (float) this.field_147501_a.field_147561_k;
	//		float playerZ = (float) this.field_147501_a.field_147558_l;
	//		GL11.glDisable(GL11.GL_LIGHTING);
	//		random.setSeed(31100L);
	//		float portalHeight = 0.75F;
	//
	//		for (int layer = 0; layer < 16; ++layer){
	//			GL11.glPushMatrix();
	//			float depth = (float) (16 - layer);
	//			float scale = 0.0625F;
	//			float clearness = 1.0F / (depth + 1.0F);
	//
	//			if(layer == 0){
	//				this.bindTexture(textureEndSky);
	//				clearness = 0.1F;
	//				depth = 65.0F;
	//				scale = 0.125F;
	//				GL11.glEnable(GL11.GL_BLEND);
	//				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	//			}
	//
	//			if(layer == 1){
	//				this.bindTexture(textureEndPortal);
	//				GL11.glEnable(GL11.GL_BLEND);
	//				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
	//				scale = 0.5F;
	//			}
	//
	//			float portalY = (float) (-(tileY + (double) portalHeight));
	//			float portalAbsY = portalY + ActiveRenderInfo.objectY;
	//			float layerY = portalY + depth + ActiveRenderInfo.objectY;
	//			float f11 = portalAbsY / layerY;
	//			f11 += (float) (tileY + (double) portalHeight);
	//
	//			//where?
	//			GL11.glTranslatef(playerX, f11, playerZ);
	//			GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
	//			GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
	//			GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
	//			GL11.glTexGeni(GL11.GL_Q, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
	//			GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, this.setFloatBuf(1.0F, 0.0F, 0.0F, 0.0F));
	//			GL11.glTexGen(GL11.GL_T, GL11.GL_OBJECT_PLANE, this.setFloatBuf(0.0F, 0.0F, 1.0F, 0.0F));
	//			GL11.glTexGen(GL11.GL_R, GL11.GL_OBJECT_PLANE, this.setFloatBuf(0.0F, 0.0F, 0.0F, 1.0F));
	//			GL11.glTexGen(GL11.GL_Q, GL11.GL_EYE_PLANE, this.setFloatBuf(0.0F, 1.0F, 0.0F, 0.0F));
	//			GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
	//			GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
	//			GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
	//			GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
	//			//			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
	//			GL11.glPopMatrix();
	//
	//			GL11.glMatrixMode(GL11.GL_TEXTURE);
	//
	//			GL11.glPushMatrix();
	//			GL11.glLoadIdentity();
	//			GL11.glTranslatef(0.0F, (float) (Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
	//			GL11.glScalef(scale, scale, scale);
	//			GL11.glTranslatef(0.5F, 0.5F, 0.0F);
	//			GL11.glRotatef((float) (layer * layer * 4321 + layer * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
	//			GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
	//			GL11.glTranslatef(-playerX, -playerZ, -playerY);
	//			portalAbsY = portalY + ActiveRenderInfo.objectY;
	//			GL11.glTranslatef(ActiveRenderInfo.objectX * depth / portalAbsY, ActiveRenderInfo.objectZ * depth / portalAbsY, -playerY);
	//
	//			//just view point cursol
	//			Tessellator tessellator = Tessellator.instance;
	//			tessellator.startDrawingQuads();
	//			float red = random.nextFloat() * 0.5F + 0.1F;
	//			float green = random.nextFloat() * 0.5F + 0.4F;
	//			float blue = random.nextFloat() * 0.5F + 0.5F;
	//
	//			if(layer == 0){
	//				blue = 1.0F;
	//				green = 1.0F;
	//				red = 1.0F;
	//			}
	//
	//			tessellator.setColorRGBA_F(red * clearness, green * clearness, blue * clearness, 1.0F);
	//			tessellator.addVertex(tileX, tileY + (double) portalHeight, tileZ);
	//			tessellator.addVertex(tileX, tileY + (double) portalHeight, tileZ + 1.0D);
	//			tessellator.addVertex(tileX + 1.0D, tileY + (double) portalHeight, tileZ + 1.0D);
	//			tessellator.addVertex(tileX + 1.0D, tileY + (double) portalHeight, tileZ);
	//			tessellator.draw();
	//			GL11.glPopMatrix();
	//			GL11.glMatrixMode(GL11.GL_MODELVIEW);
	//		}
	//
	//		GL11.glDisable(GL11.GL_BLEND);
	//		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
	//		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
	//		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
	//		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
	//		GL11.glEnable(GL11.GL_LIGHTING);
	//	}
	//
	//	private FloatBuffer setFloatBuf(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_) {
	//		this.floatBuf.clear();
	//		this.floatBuf.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
	//		this.floatBuf.flip();
	//		return this.floatBuf;
	//	}


}
