package com.test.utils;

public class Bezier {

	public double[] start = new double[3];
	public double[] end = new double[3];
	public double[] startVec = new double[3];
	public double[] endVec = new double[3];

	public Bezier(double startX, double startY, double startZ, double endX, double endY, double endZ, double startVecX, double startvecY, double startVecZ, double endVecX, double endVecY, double endVecZ) {
		this(new double[] { startX, startY, startZ }, new double[] { endX, endY, endZ }, new double[] { startVecX, startvecY, startVecZ }, new double[] { endVecX, endVecY, endVecZ });
	}

	public Bezier(double[] start, double[] end, double[] startVec, double[] endVec) {
		for (int i = 0; i < 3; i++){
			this.start[i] = start[i];
			this.end[i] = end[i];
			this.startVec[i] = startVec[i];
			this.endVec[i] = endVec[i];
		}
	}

	public double[] getPosition(double t) {
		if(t > 1){
			t = 1;
		}else if(t < 0){
			t = 0;
		}
		double[] pos = new double[3];
		double t2 = 1 - t;
		for (int i = 0; i < 3; i++){
			pos[i] = end[i] * t * t * t + 3 * (end[i] - endVec[i] / 3) * t2 * t * t + 3 * (start[i] + startVec[i] / 3) * t2 * t2 * t + start[i] * t2 * t2 * t2;
		}
		return pos;
	}

	public double[] getVelocity(double t) {
		if(t > 1){
			t = 1;
		}else if(t < 0){
			t = 0;
		}
		double[] vec = new double[3];
		double t2 = 1 - t;
		for (int i = 0; i < 3; i++){
			vec[i] = 3 * (t * t * endVec[i] / 3 + 2 * t * t2 * (end[i] - start[i] - endVec[i] / 3 - startVec[i] / 3) - t2 * t2 * startVec[i] / 3);
		}
		return vec;
	}

	public double getLength() {
		double length = 0;
		double t = 0;
		double[] vec;
		for (int i = 0; i < 32; i++){
			t = i / 32d;
			vec = this.getVelocity(t);
			length += Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]) / 32d;
		}
		return length;
	}

}
