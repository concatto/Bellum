package br.univali.game;

import java.awt.Font;

import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
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
	
	public void displayAndWait() {
		boolean running = true;
		
		renderer.setFont(new Font("Arial", Font.PLAIN, 40));
		
		while (running) {
			FloatVec pos = Geometry.centerVector(background.getSize().toFloat(), window.getSize().toFloat());
			
			renderer.setColor(0, 0, 0);
			renderer.clear();
			renderer.drawTexture(background, pos.x, pos.y);
			
			if (window.isKeyPressed(Keyboard.ENTER) || time != 0) {
				if (time == 0) {
					time = System.currentTimeMillis();
				} else {
					long delta = System.currentTimeMillis() - time;
					double remaining = 3 - (delta / 1000.0);
					if (remaining < 0) {
						running = false;
					} else {
						renderer.setColor(0.9f, 0.9f, 0.9f);
						renderer.drawText(String.format("Starting in %.2f", remaining), 50, 50);
					}
				}
			} else {
				renderer.setColor(0.9f, 0.9f, 0.9f);
				renderer.drawText("Press ENTER to start", 50, 50);
			}
			
			renderer.draw();
		}
	}
}
