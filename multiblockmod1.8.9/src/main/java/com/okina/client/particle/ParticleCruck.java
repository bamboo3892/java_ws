package com.okina.client.particle;

import java.util.Random;

import net.minecraft.world.World;

/**
 * @author ???
 *	cruck icon
 */
public class ParticleCruck extends ParticleBase {

	private double startX;
	private double startY;
	private double startZ;
	private double endX;
	private double endY;
	private double endZ;
	//	private IIcon icon;

	public ParticleCruck(World world, double x, double y, double z, double vecX, double vecY, double vecZ) {
		super(world, x, y, z);
		Random rand = world.rand;
		startX = x + rand.nextDouble() * 0.2;
		startY = y + rand.nextDouble() * 0.2;
		startZ = z + rand.nextDouble() * 0.2;
		endX = vecX;
		endY = vecY;
		endZ = vecZ;
		//		this.setParticleIcon(icon);
		particleRed = particleGreen = particleBlue = 0.6F;
		particleScale /= 4.0F;
		particleMaxAge = 8;
		noClip = true;
	}

	public ParticleCruck(World world, Object[] objects) {
		//		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5], (IIcon) objects[6]);
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5]);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	//	public void renderParticle(Tessellator tesselator, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
	//		float f6 = ((float) this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
	//		float f7 = f6 + 0.015609375F;
	//		float f8 = ((float) this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
	//		float f9 = f8 + 0.015609375F;
	//		float f10 = 0.1F * this.particleScale;
	//
	//		if(this.particleIcon != null){
	//			f6 = this.particleIcon.getInterpolatedU((double) (this.particleTextureJitterX / 4.0F * 16.0F));
	//			f7 = this.particleIcon.getInterpolatedU((double) ((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
	//			f8 = this.particleIcon.getInterpolatedV((double) (this.particleTextureJitterY / 4.0F * 16.0F));
	//			f9 = this.particleIcon.getInterpolatedV((double) ((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
	//		}
	//
	//		float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) p_70539_2_ - interpPosX);
	//		float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) p_70539_2_ - interpPosY);
	//		float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) p_70539_2_ - interpPosZ);
	//		tesselator.setColorOpaque_F(this.particleRed, this.particleGreen, this.particleBlue);
	//		tesselator.addVertexWithUV((double) (f11 - p_70539_3_ * f10 - p_70539_6_ * f10), (double) (f12 - p_70539_4_ * f10), (double) (f13 - p_70539_5_ * f10 - p_70539_7_ * f10), (double) f6, (double) f9);
	//		tesselator.addVertexWithUV((double) (f11 - p_70539_3_ * f10 + p_70539_6_ * f10), (double) (f12 + p_70539_4_ * f10), (double) (f13 - p_70539_5_ * f10 + p_70539_7_ * f10), (double) f6, (double) f8);
	//		tesselator.addVertexWithUV((double) (f11 + p_70539_3_ * f10 + p_70539_6_ * f10), (double) (f12 + p_70539_4_ * f10), (double) (f13 + p_70539_5_ * f10 + p_70539_7_ * f10), (double) f7, (double) f8);
	//		tesselator.addVertexWithUV((double) (f11 + p_70539_3_ * f10 - p_70539_6_ * f10), (double) (f12 - p_70539_4_ * f10), (double) (f13 + p_70539_5_ * f10 - p_70539_7_ * f10), (double) f7, (double) f9);
	//	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		int i = super.getBrightnessForRender(partialTick);
		float f1 = (float) particleAge / (float) particleMaxAge;
		f1 *= f1;
		f1 *= f1;
		int j = i & 255;
		int k = i >> 16 & 255;
		k += (int) (f1 * 15.0F * 16.0F);

		if(k > 240){
			k = 240;
		}

		return j | k << 16;
	}

	@Override
	public float getBrightness(float partialTicks) {
		float f1 = super.getBrightness(partialTicks);
		float f2 = (float) particleAge / (float) particleMaxAge;
		f2 = f2 * f2 * f2 * f2;
		return f1 * (1.0F - f2) + f2;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		double f = particleAge / (float) particleMaxAge;
		posX = (endX - startX) * f + startX;
		posY = (endY - startY) * f + startY;
		posZ = (endZ - startZ) * f + startZ;
	}
}
