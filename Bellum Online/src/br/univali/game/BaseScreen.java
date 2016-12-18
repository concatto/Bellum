package br.univali.game;

import br.univali.game.event.input.InputEventType;
import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntVec;
import br.univali.game.util.Utils;
import br.univali.game.window.GameWindow;

public abstract class BaseScreen {
	protected GameWindow window;
	protected Renderer renderer;
	private boolean capturing = false;
	private String capturedInput = "";
	private float overlayAlpha = 1;
	private float startingAlpha;
	private float endingAlpha;
	private long fadeStart = 0;
	private long fadeDuration;
	private float overlayRed = 0;
	private float overlayGreen = 0;
	private float overlayBlue = 0;
	
	public BaseScreen(GameWindow window) {
		this.window = window;
		this.renderer = window.getRenderer();
		
		window.addKeyboardEventConsumer(this::processCapture);
	}

	private void processCapture(KeyboardEvent event) {
		if (!capturing) {
			return;
		}
		
		int key = event.getKey();
		
		if (event.getType() == InputEventType.PRESS && key != Keyboard.UNKNOWN) {
			if (key == Keyboard.BACKSPACE) {
				if (!capturedInput.isEmpty()) {
					capturedInput = capturedInput.substring(0, capturedInput.length() - 1);
				}
			} else if (key >= '.' && key <= 'Z') {
				capturedInput += (char) key;
			}
		}
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
		if (isOverlayFading()) {
			long delta = System.currentTimeMillis() - fadeStart;
			
			if (delta > fadeDuration) {
				fadeStart = 0;
			} else {
				overlayAlpha = Utils.lerp(delta, 0, startingAlpha, fadeDuration, endingAlpha);
			}
		}
		
		renderer.setColor(overlayRed, overlayGreen, overlayBlue, overlayAlpha);
		renderer.drawRectangle(0, 0, window.getWidth(), window.getHeight());
	}
	
	protected void setOverlayAlpha(float alpha) {
		overlayAlpha  = alpha;
	}
	
	protected void setOverlayColor(float red, float green, float blue) {
		overlayRed = red;
		overlayGreen = green;
		overlayBlue = blue;
	}
	
	protected void fadeOverlayTo(float alpha, long ms) {
		fadeStart = System.currentTimeMillis();
		fadeDuration = ms;
		startingAlpha = overlayAlpha;
		endingAlpha = alpha;
	}
	
	protected boolean isOverlayFading() {
		return fadeStart > 0;
	}
	
	protected void drawCentralizedTexture(Texture texture) {
		FloatVec center = Geometry.centerVector(texture.getSize().toFloat(), window.getSize().toFloat());
		renderer.drawTexture(texture, center.x, center.y);
	}
	
	protected void beginInputCapture() {
		clearCapturedInput();
		capturing = true;
	}
	
	protected void stopCapturingInput() {
		capturing = false;
	}
	
	protected void clearCapturedInput() {
		capturedInput = "";
	}
	
	protected String getCapturedInput() {
		return capturedInput;
	}
	
	public float getOverlayAlpha() {
		return overlayAlpha;
	}
}
