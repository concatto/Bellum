package br.univali.game;

import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntVec;
import br.univali.game.window.GameWindow;

public abstract class GameScreen {
	protected GameWindow window;
	protected Renderer renderer;
	
	public GameScreen(GameWindow window) {
		this.window = window;
		this.renderer = window.getRenderer();
	}

	protected void centralizeAndDraw(String text) {
		FloatVec center = centralize(text);
		renderer.drawText(text, center.x, center.y);
	}
	
	protected void centralizeXAndDraw(String text, int y) {
		renderer.drawText(text, centralize(text).x, y);
	}
	
	protected void centralizeYAndDraw(String text, int x) {
		renderer.drawText(text, x, centralize(text).x);
	}
	
	protected FloatVec centralize(String text) {
		IntVec size = renderer.computeTextSize(text);
		return Geometry.centerVector(size.toFloat(), window.getSize().toFloat());
	}
	
	protected void drawOverlay(float alpha) {
		renderer.setColor(0, 0, 0, alpha);
		renderer.drawRectangle(0, 0, window.getWidth(), window.getHeight());
	}
	
	protected void drawCentralizedTexture(Texture texture) {
		FloatVec center = Geometry.centerVector(texture.getSize().toFloat(), window.getSize().toFloat());
		renderer.drawTexture(texture, center.x, center.y);
	}
}
