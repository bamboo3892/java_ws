package com.test.client.particle;

import com.test.utils.Bezier;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ParticleBezierCurve extends ParticleBase {

	private Bezier bezier;

	public ParticleBezierCurve(World world, double startX, double startY, double startZ, double endX, double endY, double endZ, int startSide, int endSide, int color) {
		super(world, startX, startY, startZ, color);
		ForgeDirection startDir = ForgeDirection.getOrientation(startSide);
		ForgeDirection endDir = ForgeDirection.getOrientation(endSide);
		this.bezier = new Bezier(startX, startY, startZ, endX, endY, endZ, startDir.offsetX * 2, startDir.offsetY * 2, startDir.offsetZ * 2, -endDir.offsetX * 2, -endDir.offsetY * 2, -endDir.offsetZ * 2);
		this.particleMaxAge = (int) (this.bezier.getLength() * 20);
		this.setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	public ParticleBezierCurve(World world, Bezier bezier, int color) {
		super(world, bezier.start[0], bezier.start[1], bezier.start[2], color);
		this.bezier = bezier;
		this.particleMaxAge = (int) (this.bezier.getLength() * 20);
		this.setParticleTextureIndex((int) (Math.random() * 8.0D));
	}

	//	public ParticleBezierCurve(World world, Object... objects) {
	//		this(world, (Double) objects[0], (Double) objects[1], (Double) objects[2], (Double) objects[3], (Double) objects[4], (Double) objects[5], (Integer) objects[6], (Integer) objects[7], (Integer) objects[8]);
	//	}

	public ParticleBezierCurve(World world, Object... objects) {
		this(world, (Bezier) objects[0], (Integer) objects[1]);
	}

	protected void updateScale(float ageScaled) {
		this.particleScale = (float) Math.sin(ageScaled * Math.PI) * 0.5f;
	}

	protected void updatePosition(float ageScaled) {
		double[] pos = this.bezier.getPosition(ageScaled);
		this.posX = pos[0];
		this.posY = pos[1];
		this.posZ = pos[2];
	}

}
