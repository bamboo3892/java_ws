package com.test.utils;

public class Position {

	public int x;
	public int y;
	public int z;

	public Position(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Position sum(Position a, Position b){
		return new Position(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof Position){
			Position p = (Position)o;
			return p.x == x && p.y == y && p.z == z;
		}
		return false;
	}

	@Override
	public String toString(){
		return "(x, y, z) = (" + x + ", " + y + ", " + z + ")";
	}

}
