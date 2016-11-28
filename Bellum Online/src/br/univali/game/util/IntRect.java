package br.univali.game.util;

import java.io.Serializable;

public class IntRect implements Serializable {
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
