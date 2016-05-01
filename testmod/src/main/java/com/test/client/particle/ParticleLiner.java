package com.test.client.particle;

import net.minecraft.world.World;

public class ParticleLiner extends ParticleBase {

	private float baseScale;
	private double startX;
	private double startY;
	private double startZ;
	private double endX;
	private double endY;
	private double endZ;

	/**all coordinate parameter should be relative one*/
	protected ParticleLiner(World world, double startX, double startY, double startZ, double endX, double endY, double endZ) {
		super(world, startX, startY, startZ);
		this.startX = this.posX = startX;
		this.startY = this.posY = startY;
		this.startZ = this.posZ = startZ;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
		float f = this.rand.nextFloat() * 0.6F + 0.4F;
		this.baseScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
		this.particleGreen *= 0.5F;
		this.particleBlue *= 0.1F;
		this.particleMaxAge = (int) (Math.random() * 2.0D) + 8;
		this.setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	@Override
	protected void updateScale(float ageScaled) {
		ageScaled = (float) Math.sin(ageScaled * Math.PI);
		this.particleScale = this.baseScale * ageScaled;
	}

	@Override
	protected void updatePosition(float ageScaled) {
		ageScaled = (float) ((-1) * (4F / 9F) * (ageScaled - 1.5) * (ageScaled - 1.5) + 1);
		this.posX = this.startX + this.endX * (double) ageScaled;
		this.posY = this.startY + this.endY * (double) ageScaled;
		this.posZ = this.startZ + this.endZ * (double) ageScaled;
	}

}
