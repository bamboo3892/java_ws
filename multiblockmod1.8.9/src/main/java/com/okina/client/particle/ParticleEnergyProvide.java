package com.okina.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleEnergyProvide extends EntityFX {

	private float portalParticleScale;
	private double portalPosX;
	private double portalPosY;
	private double portalPosZ;

	public ParticleEnergyProvide(World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		super(world, startX, startY, startZ, endX, endY, endZ);
		motionX = endX;
		motionY = endY;
		motionZ = endZ;
		portalPosX = posX = startX;
		portalPosY = posY = startY;
		portalPosZ = posZ = startZ;
		float f = rand.nextFloat() * 0.6F + 0.4F;
		portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;
		particleRed = particleGreen = particleBlue = 1.0F * f;
		particleGreen *= 0.5F;
		particleBlue *= 0.1F;
		particleMaxAge = (int) (Math.random() * 2.0D) + 8;
		noClip = true;
		setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	public ParticleEnergyProvide(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5]);
	}

	public ParticleEnergyProvide set1() {
		float f = rand.nextFloat() * 0.6F + 0.4F;
		portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;
		particleRed = particleGreen = particleBlue = 1.0F * f;
		particleGreen *= 0.3F;
		particleRed *= 0.9F;
		particleMaxAge = (int) (Math.random() * 10.0D) + 40;
		return this;
	}

	@Override
	public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
		float f6 = (particleAge + partialTicks) / particleMaxAge;
		f6 = 1 - f6;
		particleScale = portalParticleScale * f6;
		super.renderParticle(worldRendererIn, entityIn, partialTicks, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
	}

	//	public void renderParticle(Tessellator tessellator, float partialTick, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
	//		float f6 = ((float) this.particleAge + partialTick) / (float) this.particleMaxAge;
	//		f6 = 1 - f6;
	//		this.particleScale = this.portalParticleScale * f6;
	//		super.renderParticle(tessellator, partialTick, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
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

	/**
	 * Gets how bright this entity is.
	 */
	@Override
	public float getBrightness(float partialTicks) {
		float f1 = super.getBrightness(partialTicks);
		float f2 = (float) particleAge / (float) particleMaxAge;
		f2 = f2 * f2 * f2 * f2;
		return f1 * (1.0F - f2) + f2;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		float f = (float) particleAge / (float) particleMaxAge;
		f = (float) ((-1) * (4F / 9F) * (f - 1.5) * (f - 1.5) + 1);
		posX = portalPosX + motionX * f;
		posY = portalPosY + motionY * f;
		posZ = portalPosZ + motionZ * f;

		if(particleAge++ >= particleMaxAge){
			setDead();
		}
	}

}
