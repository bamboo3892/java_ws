package com.okina.utils;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public final class RenderingHelper {

	private static FaceBakery faceBakery = new FaceBakery();

	/**
	 * render full cube
	 */
	public static BakedQuad renderCube(float startX, float startY, float startZ, float endX, float endY, float endZ, EnumFacing face, TextureAtlasSprite texture) {
		Vector3f from = new Vector3f(startX, startY, startZ);
		Vector3f to = new Vector3f(endX, endY, endZ);
		BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0F, 0.0F, 16.0F, 16.0F }, 0);
		BlockPartFace partFace = new BlockPartFace(BlockPartFace.FACING_DEFAULT, 0, new ResourceLocation("blocks/stone").toString(), uv);
		BakedQuad bakedQuad = faceBakery.makeBakedQuad(from, to, partFace, texture, face, ModelRotation.X0_Y0, null, true, true);
		return bakedQuad;
	}

	public static List<BakedQuad> renderFrame(float startX, float startY, float startZ, float endX, float endY, float endZ, float thickness, EnumFacing face, TextureAtlasSprite texture) {
		List<BakedQuad> list = new ArrayList<BakedQuad>();
		float sizeX = endX - startX;
		float sizeY = endY - startY;
		float sizeZ = endZ - startZ;
		list.add(renderCube(startX, startY, startZ, startX + thickness, startY + sizeY, startZ + thickness, face, texture));
		list.add(renderCube(startX + sizeX - thickness, startY, startZ, startX + sizeX, startY + sizeY, startZ + thickness, face, texture));
		list.add(renderCube(startX, startY, startZ + sizeZ - thickness, startX + thickness, startY + sizeY, startZ + sizeZ, face, texture));
		list.add(renderCube(startX + sizeX - thickness, startY, startZ + sizeZ - thickness, startX + sizeX, startY + sizeY, startZ + sizeZ, face, texture));
		list.add(renderCube(startX + thickness, startY, startZ, startX + sizeX - thickness, startY + thickness, startZ + thickness, face, texture));
		list.add(renderCube(startX, startY, startZ + thickness, startX + thickness, startY + thickness, startZ + sizeZ - thickness, face, texture));
		list.add(renderCube(startX + sizeX - thickness, startY, startZ + thickness, startX + sizeX, startY + thickness, startZ + sizeZ - thickness, face, texture));
		list.add(renderCube(startX + thickness, startY, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + thickness, startZ + sizeZ, face, texture));
		list.add(renderCube(startX + thickness, startY + sizeY - thickness, startZ, startX + sizeX - thickness, startY + sizeY, startZ + thickness, face, texture));
		list.add(renderCube(startX, startY + sizeY - thickness, startZ + thickness, startX + thickness, startY + sizeY, startZ + sizeZ - thickness, face, texture));
		list.add(renderCube(startX + sizeX - thickness, startY + sizeY - thickness, startZ + thickness, startX + sizeX, startY + sizeY, startZ + sizeZ - thickness, face, texture));
		list.add(renderCube(startX + thickness, startY + sizeY - thickness, startZ + sizeZ - thickness, startX + sizeX - thickness, startY + sizeY, startZ + sizeZ, face, texture));
		return list;
	}

	/**
	 * render full size connected cube frame
	 * @param startX
	 * @param startY
	 * @param startZ
	 * @param thickness
	 * @param connection
	 * @param face
	 * @param texture
	 * @return
	 */
	public static List<BakedQuad> renderConnectedFrame(float thickness, boolean[] connection, EnumFacing face, TextureAtlasSprite texture) {
		List<BakedQuad> list = new ArrayList<BakedQuad>();
		if(connection == null || connection.length != 6) return list;

		for (int j1 = 0; j1 < 2; j1++){
			for (int j2 = 0; j2 < 2; j2++){
				for (int j3 = 0; j3 < 2; j3++){
					float x1 = j1 == 0 ? 0 : 16 - thickness;
					float x2 = j1 == 0 ? thickness : 16;
					float y1 = j2 == 0 ? 0 : 16 - thickness;
					float y2 = j2 == 0 ? thickness : 16;
					float z1 = j3 == 0 ? 0 : 16 - thickness;
					float z2 = j3 == 0 ? thickness : 16;
					list.add(renderCube(x1, y1, z1, x2, y2, z2, face, texture));
				}
			}
		}

		float[] c1 = new float[3];
		float[] c2 = new float[3];
		int[] i = new int[3];

		for (i[0] = -1; i[0] < 2; i[0]++){
			for (i[1] = -1; i[1] < 2; i[1]++){
				if(i[0] == 0 && i[1] == 0) continue;
				for (i[2] = -1; i[2] < 2; i[2]++){
					if(i[2] == 0 && (i[0] == 0 || i[1] == 0)) continue;
					for (int j = 0; j < 3; j++){
						if(i[j] == -1){
							c1[j] = 0;
							c2[j] = thickness;
						}else if(i[j] == 0){
							c1[j] = thickness;
							c2[j] = 16 - thickness;
						}else{
							c1[j] = 16 - thickness;
							c2[j] = 16;
						}
					}
					int side1 = -1;
					int side2 = -1;
					if(i[0] != 0){
						side1 = EnumFacing.getFacingFromVector(i[0], 0, 0).getIndex();
					}
					if(i[1] != 0){
						if(side1 == -1){
							side1 = EnumFacing.getFacingFromVector(0, i[1], 0).getIndex();
						}else{
							side2 = EnumFacing.getFacingFromVector(0, i[1], 0).getIndex();
						}
					}
					if(i[2] != 0){
						side2 = EnumFacing.getFacingFromVector(0, 0, i[2]).getIndex();
					}
					if(!(connection[side1] || connection[side2])){
						list.add(renderCube(c1[0], c1[1], c1[2], c2[0], c2[1], c2[2], face, texture));
					}
				}
			}
		}
		return list;
	}

	/**
	 * for tile entity special renderer
	 */
	public static void renderCube(double startX, double startY, double startZ, double endX, double endY, double endZ) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		double d2 = 1;

		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

		//y neg
		worldrenderer.pos(startX, startY, startZ).tex(0, 0).endVertex();
		worldrenderer.pos(endX, startY, startZ).tex(d2, 0).endVertex();
		worldrenderer.pos(endX, startY, endZ).tex(d2, d2).endVertex();
		worldrenderer.pos(startX, startY, endZ).tex(0, d2).endVertex();
		//y pos
		worldrenderer.pos(startX, endY, startZ).tex(0, 0).endVertex();
		worldrenderer.pos(startX, endY, endZ).tex(d2, 0).endVertex();
		worldrenderer.pos(endX, endY, endZ).tex(d2, d2).endVertex();
		worldrenderer.pos(endX, endY, startZ).tex(0, d2).endVertex();
		//z neg
		worldrenderer.pos(startX, startY, startZ).tex(0, 0).endVertex();
		worldrenderer.pos(startX, startY, endZ).tex(0, d2).endVertex();
		worldrenderer.pos(startX, endY, endZ).tex(d2, d2).endVertex();
		worldrenderer.pos(startX, endY, startZ).tex(d2, 0).endVertex();
		//z pos
		worldrenderer.pos(endX, startY, startZ).tex(0, 0).endVertex();
		worldrenderer.pos(endX, endY, startZ).tex(0, d2).endVertex();
		worldrenderer.pos(endX, endY, endZ).tex(d2, d2).endVertex();
		worldrenderer.pos(endX, startY, endZ).tex(d2, 0).endVertex();
		//x neg
		worldrenderer.pos(startX, startY, startZ).tex(0, 0).endVertex();
		worldrenderer.pos(startX, endY, startZ).tex(d2, 0).endVertex();
		worldrenderer.pos(endX, endY, startZ).tex(d2, d2).endVertex();
		worldrenderer.pos(endX, startY, startZ).tex(0, d2).endVertex();
		///x pos
		worldrenderer.pos(startX, startY, endZ).tex(0, 0).endVertex();
		worldrenderer.pos(endX, startY, endZ).tex(d2, 0).endVertex();
		worldrenderer.pos(endX, endY, endZ).tex(d2, d2).endVertex();
		worldrenderer.pos(startX, endY, endZ).tex(0, d2).endVertex();

		tessellator.draw();
	}



	//	public static void renderLaser(TileEntity host, double x, double y, double z, int length, ForgeDirection orientation, int red, int green, int blue, int alpha, float partialTicks) {
	//		Tessellator tessellator = Tessellator.instance;
	//		GL11.glPushMatrix();
	//
	//		double xStart = x;
	//		double yStart = y;
	//		double zStart = z;
	//
	//		if(orientation == ForgeDirection.DOWN){
	//			yStart -= length;
	//		}
	//		if(orientation == ForgeDirection.NORTH){
	//			GL11.glRotatef(270, 1, 0, 0);
	//			yStart = -z - 1;
	//			zStart = y;
	//		}else if(orientation == ForgeDirection.SOUTH){
	//			GL11.glRotatef(90, 1, 0, 0);
	//			yStart = z;
	//			zStart = -y - 1;
	//		}else if(orientation == ForgeDirection.EAST){
	//			GL11.glRotatef(270, 0, 0, 1);
	//			yStart = x;
	//			xStart = -y - 1;
	//		}else if(orientation == ForgeDirection.WEST){
	//			GL11.glRotatef(90, 0, 0, 1);
	//			yStart = -x - 1;
	//			xStart = y;
	//		}
	//		yStart = yStart + 7.0 / 16.0D;
	//
	//		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
	//		RenderHelper.disableStandardItemLighting();
	//		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
	//		GL11.glDisable(GL11.GL_TEXTURE_2D);
	//		GL11.glDisable(GL11.GL_CULL_FACE);
	//		double height = length + 2.0 / 16.0D;
	//		GL11.glEnable(GL11.GL_BLEND);
	//		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	//		GL11.glDepthMask(false);
	//		tessellator.startDrawingQuads();
	//		if(alpha < 26) alpha = 26;
	//		tessellator.setColorRGBA(red, green, blue, alpha);
	//		double d18 = 7.0 / 16.0D;
	//		double d19 = 7.0 / 16.0D;
	//		double d20 = 9.0 / 16.0D;
	//		double d21 = 7.0 / 16.0D;
	//		double d22 = 7.0 / 16.0D;
	//		double d23 = 9.0 / 16.0D;
	//		double d24 = 9.0 / 16.0D;
	//		double d25 = 9.0 / 16.0D;
	//
	//		tessellator.addVertex(xStart + d18, yStart + height, zStart + d19);
	//		tessellator.addVertex(xStart + d18, yStart, zStart + d19);
	//		tessellator.addVertex(xStart + d20, yStart, zStart + d21);
	//		tessellator.addVertex(xStart + d20, yStart + height, zStart + d21);
	//		tessellator.addVertex(xStart + d24, yStart + height, zStart + d25);
	//		tessellator.addVertex(xStart + d24, yStart, zStart + d25);
	//		tessellator.addVertex(xStart + d22, yStart, zStart + d23);
	//		tessellator.addVertex(xStart + d22, yStart + height, zStart + d23);
	//		tessellator.addVertex(xStart + d20, yStart + height, zStart + d21);
	//		tessellator.addVertex(xStart + d20, yStart, zStart + d21);
	//		tessellator.addVertex(xStart + d24, yStart, zStart + d25);
	//		tessellator.addVertex(xStart + d24, yStart + height, zStart + d25);
	//		tessellator.addVertex(xStart + d22, yStart + height, zStart + d23);
	//		tessellator.addVertex(xStart + d22, yStart, zStart + d23);
	//		tessellator.addVertex(xStart + d18, yStart, zStart + d19);
	//		tessellator.addVertex(xStart + d18, yStart + height, zStart + d19);
	//
	//		tessellator.draw();
	//		GL11.glEnable(GL11.GL_LIGHTING);
	//		GL11.glEnable(GL11.GL_TEXTURE_2D);
	//		GL11.glDepthMask(true);
	//		GL11.glPopAttrib();
	//		GL11.glPopMatrix();
	//	}
	//
}
