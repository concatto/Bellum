package br.univali.game.graphics;

import java.awt.Font;

public enum GameFont {
	GIGANTIC(new Font("Helvetica", Font.PLAIN, 62)),
	LARGE(new Font("Helvetica", Font.PLAIN, 40)),
	MEDIUM(new Font("Helvetica", Font.PLAIN, 28));
	
	private Font font;
	private GameFont(Font font) {
		this.font = font;
	}
	
	public Font getFont() {
		return font;
	}
}
