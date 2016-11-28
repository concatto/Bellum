package br.univali.game.util;

import java.io.Serializable;

public class IntVec implements Serializable {
	public final int x;
	public final int y;
	
	public IntVec(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public FloatVec toFloat() {
		return new FloatVec(x, y);
	}
}
