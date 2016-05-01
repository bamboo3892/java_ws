package com.test.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class ParticleTest2 extends EntityFX {

	private float portalParticleScale;
	private double portalPosX;
	private double portalPosY;
	private double portalPosZ;

	public ParticleTest2(World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		super(world, startX, startY, startZ, endX, endY, endZ);
		this.motionX = endX;
		this.motionY = endY;
		this.motionZ = endZ;
		this.portalPosX = this.posX = startX;
		this.portalPosY = this.posY = startY;
		this.portalPosZ = this.posZ = startZ;
		float f = this.rand.nextFloat() * 0.6F + 0.4F;
		this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
		this.particleGreen *= 0.5F;
		this.particleBlue *= 0.1F;
		this.particleMaxAge = (int) (Math.random() * 2.0D) + 8;
		this.noClip = true;
		this.setParticleTextureIndex((int) (Math.random() * 8.0D));
	}
	
	public void set1(){
		float f = this.rand.nextFloat() * 0.6F + 0.4F;
        this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
        this.particleGreen *= 0.3F;
        this.particleRed *= 0.9F;
        this.particleMaxAge = (int)(Math.random() * 10.0D) + 40;
	}

	public void renderParticle(Tessellator tessellator, float partialTick, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		float f6 = ((float) this.particleAge + partialTick) / (float) this.particleMaxAge;
		f6 = 1 -  f6;
		this.particleScale = this.portalParticleScale * f6;
		super.renderParticle(tessellator, partialTick, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
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

	/**
	 * Gets how bright this entity is.
	 */
	public float getBrightness(float partialTicks) {
		float f1 = super.getBrightness(partialTicks);
		float f2 = (float) this.particleAge / (float) this.particleMaxAge;
		f2 = f2 * f2 * f2 * f2;
		return f1 * (1.0F - f2) + f2;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		float f = (float) this.particleAge / (float) this.particleMaxAge;
		f = (float) ((-1) * (4F / 9F) * (f - 1.5) * (f - 1.5) + 1);
		this.posX = this.portalPosX + this.motionX * (double) f;
		this.posY = this.portalPosY + this.motionY * (double) f;
		this.posZ = this.portalPosZ + this.motionZ * (double) f;

		if(this.particleAge++ >= this.particleMaxAge){
			this.setDead();
		}
	}

}
