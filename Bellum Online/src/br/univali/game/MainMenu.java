package br.univali.game;

import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.window.GameWindow;

public class MainMenu extends GameScreen {
	private Texture background;
	private long time = 0;
	
	public MainMenu(GameWindow window) {
		super(window);
		this.background = Texture.load("images/menu_background.jpg");
	}
	
	public void displayWelcome() {
		boolean running = true;
		time = 0;
		
		renderer.setFont(Renderer.LARGE_FONT);
		
		while (running) {			
			renderer.setColor(0, 0, 0);
			renderer.clear();
			
			drawCentralizedTexture(background);
			drawOverlay(0.6f);
			
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
						
						renderer.setColor(0.58f, 0.82f, 0.95f);
						centralizeXAndDraw(text, 50);
					}
				}
			} else {
				renderer.setColor(0.58f, 0.82f, 0.95f);
				centralizeXAndDraw("Press ENTER to start", 50);
			}
			
			renderer.draw();
		}
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
}
