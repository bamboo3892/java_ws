package com.test.client.particle;

import java.util.Random;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @author ???
 *	cruck icon
 */
public class ParticleCruck extends EntityFX {

	private double startX;
	private double startY;
	private double startZ;
	private double endX;
	private double endY;
	private double endZ;
	private IIcon icon;

	public ParticleCruck(World world, double x, double y, double z, double vecX, double vecY, double vecZ, IIcon icon) {
		super(world, x, y, z, vecX, vecY, vecZ);
		Random rand = world.rand;
		this.startX = x + rand.nextDouble() * 0.2;
		this.startY = y + rand.nextDouble() * 0.2;
		this.startZ = z + rand.nextDouble() * 0.2;
		this.endX = vecX;
		this.endY = vecY;
		this.endZ = vecZ;
		this.setParticleIcon(icon);
		this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
		this.particleScale /= 4.0F;
		this.particleMaxAge = 8;
		this.noClip = true;
	}

	public ParticleCruck(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5], (IIcon) objects[6]);
	}

	public int getFXLayer() {
		return 1;
	}

	public void renderParticle(Tessellator tesselator, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		float f6 = ((float) this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
		float f7 = f6 + 0.015609375F;
		float f8 = ((float) this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
		float f9 = f8 + 0.015609375F;
		float f10 = 0.1F * this.particleScale;

		if(this.particleIcon != null){
			f6 = this.particleIcon.getInterpolatedU((double) (this.particleTextureJitterX / 4.0F * 16.0F));
			f7 = this.particleIcon.getInterpolatedU((double) ((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
			f8 = this.particleIcon.getInterpolatedV((double) (this.particleTextureJitterY / 4.0F * 16.0F));
			f9 = this.particleIcon.getInterpolatedV((double) ((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
		}

		float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) p_70539_2_ - interpPosX);
		float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) p_70539_2_ - interpPosY);
		float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) p_70539_2_ - interpPosZ);
		tesselator.setColorOpaque_F(this.particleRed, this.particleGreen, this.particleBlue);
		tesselator.addVertexWithUV((double) (f11 - p_70539_3_ * f10 - p_70539_6_ * f10), (double) (f12 - p_70539_4_ * f10), (double) (f13 - p_70539_5_ * f10 - p_70539_7_ * f10), (double) f6, (double) f9);
		tesselator.addVertexWithUV((double) (f11 - p_70539_3_ * f10 + p_70539_6_ * f10), (double) (f12 + p_70539_4_ * f10), (double) (f13 - p_70539_5_ * f10 + p_70539_7_ * f10), (double) f6, (double) f8);
		tesselator.addVertexWithUV((double) (f11 + p_70539_3_ * f10 + p_70539_6_ * f10), (double) (f12 + p_70539_4_ * f10), (double) (f13 + p_70539_5_ * f10 + p_70539_7_ * f10), (double) f7, (double) f8);
		tesselator.addVertexWithUV((double) (f11 + p_70539_3_ * f10 - p_70539_6_ * f10), (double) (f12 - p_70539_4_ * f10), (double) (f13 + p_70539_5_ * f10 - p_70539_7_ * f10), (double) f7, (double) f9);
	}

	public int getBrightnessForRender(float partialTick) {
		int i = super.getBrightnessForRender(partialTick);
		float f1 = (float) this.particleAge / (float) this.particleMaxAge;
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

	public float getBrightness(float partialTicks) {
		float f1 = super.getBrightness(partialTicks);
		float f2 = (float) this.particleAge / (float) this.particleMaxAge;
		f2 = f2 * f2 * f2 * f2;
		return f1 * (1.0F - f2) + f2;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		double f = this.particleAge / (float) this.particleMaxAge;
		this.posX = (endX - startX) * f + startX;
		this.posY = (endY - startY) * f + startY;
		this.posZ = (endZ - startZ) * f + startZ;

		if(this.particleAge++ >= this.particleMaxAge){
			this.setDead();
		}
	}
}
