package com.okina.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class ParticleGrower extends EntityFX {

	private float portalParticleScale;
	private double portalPosX;
	private double portalPosY;
	private double portalPosZ;

	public ParticleGrower(World world, double endX, double endY, double endZ, double startX, double startY, double startZ) {
		super(world, endX, endY, endZ, startX, startY, startZ);
		motionX = startX;
		motionY = startY;
		motionZ = startZ;
		portalPosX = posX = endX;
		portalPosY = posY = endY;
		portalPosZ = posZ = endZ;
		float f = rand.nextFloat() * 0.6F + 0.4F;
		portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;
		particleRed = particleGreen = particleBlue = 1.0F * f;
		particleGreen *= 0.9F;
		particleRed *= 0.3F;
		particleMaxAge = (int) (Math.random() * 10.0D) + 40;
		noClip = true;
		setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	public ParticleGrower(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5]);
	}

	@Override
	public void renderParticle(net.minecraft.client.renderer.WorldRenderer worldRendererIn, net.minecraft.entity.Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
		float f6 = (particleAge + partialTicks) / particleMaxAge;
		f6 = 1.0F - f6;
		f6 *= f6;
		f6 = 1.0F - f6;
		particleScale = portalParticleScale * f6;
		super.renderParticle(worldRendererIn, entityIn, partialTicks, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
	}

	//	public void renderParticle(Tessellator tessellator, float partialTick, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
	//		float f6 = ((float) this.particleAge + partialTick) / (float) this.particleMaxAge;
	//		f6 = 1.0F - f6;
	//		f6 *= f6;
	//		f6 = 1.0F - f6;
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
		f = -f + f * f * 2.0F;
		f = 1.0F - f;
		posX = portalPosX + motionX * f;
		posY = portalPosY + motionY * f;
		posZ = portalPosZ + motionZ * f;

		if(particleAge++ >= particleMaxAge){
			setDead();
		}
	}

}
