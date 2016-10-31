package br.univali.game.util;

public class FloatVec {
	public float x;
	public float y;
	
	public FloatVec(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public FloatVec(FloatVec vec) {
		this.x = vec.x;
		this.y = vec.y;
	}

	@Override
	public String toString() {
		return "Vector(" + x + ", " + y + ")";
	}
	
	public IntVec toInt() {
		return new IntVec((int) x, (int) y);
	}
	
	public FloatVec multiply(float coefficient) {
		return new FloatVec(x * coefficient, y * coefficient);
	}
}
