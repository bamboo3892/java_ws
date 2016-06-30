package com.okina.client.particle;

import com.okina.utils.Bezier;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ParticleBezierCurve extends ParticleBase {

	private Bezier bezier;

	public ParticleBezierCurve(World world, double startX, double startY, double startZ, double endX, double endY, double endZ, int startSide, int endSide, int color) {
		super(world, startX, startY, startZ, color);
		EnumFacing startDir = EnumFacing.getFront(startSide);
		EnumFacing endDir = EnumFacing.getFront(endSide);
		bezier = new Bezier(startX, startY, startZ, endX, endY, endZ, startDir.getFrontOffsetX() * 2, startDir.getFrontOffsetY() * 2, startDir.getFrontOffsetZ() * 2, -endDir.getFrontOffsetX() * 2, -endDir.getFrontOffsetY() * 2, -endDir.getFrontOffsetZ() * 2);
		particleMaxAge = (int) (bezier.getLength() * 20);
		setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	public ParticleBezierCurve(World world, Bezier bezier, int color) {
		super(world, bezier.start[0], bezier.start[1], bezier.start[2], color);
		this.bezier = bezier;
		particleMaxAge = (int) (this.bezier.getLength() * 20);
		setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	//	public ParticleBezierCurve(World world, Object... objects) {
	//		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5], (Integer) objects[6], (Integer) objects[7], (Integer) objects[8]);
	//	}

	public ParticleBezierCurve(World world, Object... objects) {
		this(world, (Bezier) objects[0], (Integer) objects[1]);
	}

	@Override
	protected void updateScale(float ageScaled) {
		particleScale = (float) Math.sin(ageScaled * Math.PI) * 0.5f;
	}

	@Override
	protected void updatePosition(float ageScaled) {
		double[] pos = bezier.getPosition(ageScaled);
		posX = pos[0];
		posY = pos[1];
		posZ = pos[2];
	}

}
