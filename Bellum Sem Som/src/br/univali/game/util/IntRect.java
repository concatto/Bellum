package br.univali.game.util;

public class IntRect {
	public int x;
	public int y;
	public int width;
	public int height;
	
	public IntRect(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public IntRect() {
		this(0, 0, 0, 0);
	}
}
