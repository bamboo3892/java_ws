package com.test.client.particle;

import com.test.main.TestCore;
import com.test.utils.Bezier;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author ???
 *	Bezier curve( not-moving dots)
 */
public class ParticleBezierDots extends ParticleBase {

	private Bezier bezier;

	public ParticleBezierDots(World world, double startX, double startY, double startZ, double endX, double endY, double endZ, int startSide, int endSide, int color) {
		super(world, startX, startY, startZ, color);
		ForgeDirection startDir = ForgeDirection.getOrientation(startSide);
		ForgeDirection endDir = ForgeDirection.getOrientation(endSide);
		this.bezier = new Bezier(startX, startY, startZ, endX, endY, endZ, startDir.offsetX * 2, startDir.offsetY * 2, startDir.offsetZ * 2, -endDir.offsetX * 2, -endDir.offsetY * 2, -endDir.offsetZ * 2);
		this.particleScale = 0.3f;
		this.particleTextureIndexX = 4;
		this.particleTextureIndexY = 2;
		this.textureSizeX = 4;
		this.textureSizeY = 4;
		this.particleMaxAge = (int) (this.bezier.getLength() * 100);
	}

	public ParticleBezierDots(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5], (Integer) objects[6], (Integer) objects[7], (Integer) objects[8]);
	}

	protected void updatePosition(float ageScaled) {
		double[] pos = this.bezier.getPosition(ageScaled);
		this.posX = pos[0];
		this.posY = pos[1];
		this.posZ = pos[2];

		if(this.worldObj.getTotalWorldTime() % 8 == 0){
			TestCore.spawnParticle(worldObj, TestCore.PARTICLE_DOT, this.posX, this.posY, this.posZ, this.color.getRGB(), 0.3f);
		}
	}

}
