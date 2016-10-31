package br.univali.game.util;

public class FloatRect {
	public float x;
	public float y;
	public float width;
	public float height;
	
	public FloatRect(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public FloatRect() {
		this(0, 0, 0, 0);
	}
}
