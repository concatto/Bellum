package br.univali.game.util;

public class IntVec {
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
