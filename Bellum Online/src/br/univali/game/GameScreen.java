package br.univali.game;

import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntVec;
import br.univali.game.util.Utils;
import br.univali.game.window.GameWindow;

public abstract class GameScreen {
	protected GameWindow window;
	protected Renderer renderer;
	private float overlayAlpha = 1;
	private float startingAlpha;
	private float endingAlpha;
	private long fadeStart = 0;
	private long fadeDuration;
	
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
		setOverlayAlpha(alpha);
		drawOverlay();
	}
	
	protected void drawOverlay() {
		if (fadeStart > 0) {
			long delta = System.currentTimeMillis() - fadeStart;
			
			if (delta > fadeDuration) {
				fadeStart = 0;
			} else {
				overlayAlpha = Utils.lerp(delta, 0, startingAlpha, fadeDuration, endingAlpha);
			}
		}
		
		renderer.setColor(0, 0, 0, overlayAlpha);
		renderer.drawRectangle(0, 0, window.getWidth(), window.getHeight());
	}
	
	protected void setOverlayAlpha(float alpha) {
		overlayAlpha  = alpha;
	}
	
	protected void fadeOverlayTo(float alpha, long ms) {
		fadeStart = System.currentTimeMillis();
		fadeDuration = ms;
		startingAlpha = overlayAlpha;
		endingAlpha = alpha;
	}
	
	protected void drawCentralizedTexture(Texture texture) {
		FloatVec center = Geometry.centerVector(texture.getSize().toFloat(), window.getSize().toFloat());
		renderer.drawTexture(texture, center.x, center.y);
	}
}
