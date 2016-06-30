package com.okina.client.particle;

import com.okina.main.TestCore;
import com.okina.utils.Bezier;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @author ???
 *	Bezier curve( not-moving dots)
 */
public class ParticleBezierDots extends ParticleBase {

	private Bezier bezier;

	public ParticleBezierDots(World world, double startX, double startY, double startZ, double endX, double endY, double endZ, int startSide, int endSide, int color) {
		super(world, startX, startY, startZ, color);
		EnumFacing startDir = EnumFacing.getFront(startSide);
		EnumFacing endDir = EnumFacing.getFront(endSide);
		bezier = new Bezier(startX, startY, startZ, endX, endY, endZ, startDir.getFrontOffsetX() * 2, startDir.getFrontOffsetY() * 2, startDir.getFrontOffsetZ() * 2, -endDir.getFrontOffsetX() * 2, -endDir.getFrontOffsetY() * 2, -endDir.getFrontOffsetZ() * 2);
		particleScale = 0.3f;
		particleTextureIndexX = 4;
		particleTextureIndexY = 2;
		textureSizeX = 4;
		textureSizeY = 4;
		particleMaxAge = (int) (bezier.getLength() * 100);
	}

	public ParticleBezierDots(World world, Object[] objects) {
		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5], (Integer) objects[6], (Integer) objects[7], (Integer) objects[8]);
	}

	@Override
	protected void updatePosition(float ageScaled) {
		double[] pos = bezier.getPosition(ageScaled);
		posX = pos[0];
		posY = pos[1];
		posZ = pos[2];

		if(worldObj.getTotalWorldTime() % 8 == 0){
			TestCore.spawnParticle(worldObj, TestCore.PARTICLE_DOT, posX, posY, posZ, color.getRGB(), 0.3f);
		}
	}

}
