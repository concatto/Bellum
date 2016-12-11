package br.univali.game;

import java.awt.Font;

import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntVec;
import br.univali.game.window.GameWindow;

public class MainMenu {
	private GameWindow window;
	private Renderer renderer;
	private Texture background;
	private long time = 0;
	
	public MainMenu(GameWindow window) {
		this.window = window;
		this.renderer = window.getRenderer();
		this.background = Texture.load("images/menu_background.jpg");
	}
	
	public void displayWelcome() {
		boolean running = true;
		time = 0;
		
		renderer.setFont(new Font("Arial", Font.PLAIN, 40));
		
		while (running) {
			FloatVec center = Geometry.centerVector(background.getSize().toFloat(), window.getSize().toFloat());
			
			renderer.setColor(0, 0, 0);
			renderer.clear();
			
			renderer.drawTexture(background, center.x, center.y);
			drawOverlay(0.4f);
			
			if (window.isKeyPressed(Keyboard.ENTER) || time != 0) {
				if (time == 0) {
					time = System.currentTimeMillis();
				} else {
					long delta = System.currentTimeMillis() - time;
					double remaining = 3 - (delta / 1000.0);
					if (remaining < 0) {
						running = false;
					} else {
						String text = String.format("Starting in %.2f", remaining);
						
						renderer.setColor(0.48f, 0.72f, 0.85f);
						centralizeXAndDraw(text, 50);
					}
				}
			} else {
				renderer.setColor(0.48f, 0.72f, 0.85f);
				centralizeXAndDraw("Press ENTER to start", 50);
			}
			
			renderer.draw();
		}
	}

	private void centralizeAndDraw(String text) {
		FloatVec center = centralize(text);
		renderer.drawText(text, center.x, center.y);
	}
	
	private void centralizeXAndDraw(String text, int y) {
		renderer.drawText(text, centralize(text).x, y);
	}
	
	private void centralizeYAndDraw(String text, int x) {
		renderer.drawText(text, x, centralize(text).x);
	}
	
	private FloatVec centralize(String text) {
		IntVec size = renderer.computeTextSize(text);
		return Geometry.centerVector(size.toFloat(), window.getSize().toFloat());
	}

	public void displayConnectionFailure() {
		long time = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - time < 3000) {
			FloatVec center = Geometry.centerVector(background.getSize().toFloat(), window.getSize().toFloat());
			renderer.drawTexture(background, center.x, center.y);
			
			drawOverlay(0.8f);
			
			renderer.setColor(0.9f, 0, 0, 1);
			centralizeAndDraw("Failed to connect to server.");
			
			renderer.draw();
		}
	}
	
	private void drawOverlay(float alpha) {
		renderer.setColor(0, 0, 0, alpha);
		renderer.drawRectangle(0, 0, window.getWidth(), window.getHeight());
	}
}
