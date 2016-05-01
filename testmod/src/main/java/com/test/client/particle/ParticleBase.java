package com.test.client.particle;

import java.awt.Color;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class ParticleBase extends EntityFX {

	protected int textureSizeX = 1;
	protected int textureSizeY = 1;
	protected Color color;

	protected ParticleBase(World world, double x, double y, double z) {
		this(world, x, y, z, 0xFFFFFF);
	}

	protected ParticleBase(World world, double x, double y, double z, int color) {
		super(world, x, y, z);
		this.color = new Color(color);
		this.particleRed = this.color.getRed() / 256f;
		this.particleGreen = this.color.getGreen() / 256f;
		this.particleBlue = this.color.getBlue() / 256f;
		this.particleMaxAge = 20;
		this.noClip = true;
	}

	public void renderParticle(Tessellator tessellator, float partialTicks, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		float ageScaled = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge;

		this.updateColor(ageScaled);
		this.updateTexture(ageScaled);
		this.updateScale(ageScaled);

		float f6 = (float) this.particleTextureIndexX / 16.0F;
		float f7 = f6 + this.textureSizeX / 16F;
		float f8 = (float) this.particleTextureIndexY / 16.0F;
		float f9 = f8 + this.textureSizeY / 16F;
		float f10 = 0.1F * this.particleScale;

		float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
		tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
		tessellator.addVertexWithUV((double) (f11 - p_70539_3_ * f10 - p_70539_6_ * f10), (double) (f12 - p_70539_4_ * f10), (double) (f13 - p_70539_5_ * f10 - p_70539_7_ * f10), (double) f7, (double) f9);
		tessellator.addVertexWithUV((double) (f11 - p_70539_3_ * f10 + p_70539_6_ * f10), (double) (f12 + p_70539_4_ * f10), (double) (f13 - p_70539_5_ * f10 + p_70539_7_ * f10), (double) f7, (double) f8);
		tessellator.addVertexWithUV((double) (f11 + p_70539_3_ * f10 + p_70539_6_ * f10), (double) (f12 + p_70539_4_ * f10), (double) (f13 + p_70539_5_ * f10 + p_70539_7_ * f10), (double) f6, (double) f8);
		tessellator.addVertexWithUV((double) (f11 + p_70539_3_ * f10 - p_70539_6_ * f10), (double) (f12 - p_70539_4_ * f10), (double) (f13 + p_70539_5_ * f10 - p_70539_7_ * f10), (double) f6, (double) f9);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.particleAge++ >= this.particleMaxAge){
			this.setDead();
		}
		this.updatePosition(((float) this.particleAge) / (float) this.particleMaxAge);
	}

	/**called on every partial tick*/
	protected void updateColor(float ageScaled) {

	}

	/**called on every partial tick*/
	protected void updateTexture(float ageScaled) {

	}

	/**called on every partial tick*/
	protected void updateScale(float ageScaled) {

	}

	/**called on every tick*/
	protected void updatePosition(float ageScaled) {

	}

	/**return color multiply?*/
	public int getBrightnessForRender(float partialTick) {
		int i = super.getBrightnessForRender(partialTick);
		float ageScaled = (float) this.particleAge / (float) this.particleMaxAge;
		ageScaled *= ageScaled;
		ageScaled *= ageScaled;
		int j = i & 255;
		int alpha = i >> 16 & 255;
		alpha += (int) (ageScaled * 15.0F * 16.0F);

		if(alpha > 240){
			alpha = 240;
		}

		return j | alpha << 16;
	}

	/**
	 * Gets how bright this entity is.
	 */
	public float getBrightness(float partialTicks) {
		float brightness = super.getBrightness(partialTicks);
		float ageScaled = (float) this.particleAge / (float) this.particleMaxAge;
		ageScaled = ageScaled * ageScaled * ageScaled * ageScaled;
		return brightness * (1.0F - ageScaled) + ageScaled;
	}

}
