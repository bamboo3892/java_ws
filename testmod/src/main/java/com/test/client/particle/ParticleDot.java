package com.test.client.particle;

import net.minecraft.world.World;

/**
 * @author ???
 *	not-moving dot (mainly used by Particle4)
 */
public class ParticleDot extends ParticleBase {

	public float baseScale = 0.3f;

	public ParticleDot(World world, double x, double y, double z, int color, float baseSize) {
		super(world, x, y, z, color);
		this.baseScale = baseSize;
		this.particleMaxAge = 30;
		this.particleTextureIndexX = 4;
		this.particleTextureIndexY = 2;
		this.textureSizeX = 4;
		this.textureSizeY = 4;
	}

	public ParticleDot(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Integer) objects[3], (Float) objects[4]);
	}
	
	/**called on every partial tick*/
	@Override
	protected void updateScale(float ageScaled) {
		this.particleScale = (float) Math.cos(ageScaled * Math.PI / 2d) * baseScale;
	}

}
