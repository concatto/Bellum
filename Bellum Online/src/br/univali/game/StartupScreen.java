package br.univali.game;

import br.univali.game.graphics.GameFont;
import br.univali.game.graphics.Texture;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.window.GameWindow;

public class StartupScreen extends BaseScreen {
	private Texture background;
	
	public StartupScreen(GameWindow window) {
		super(window);
		this.background = Texture.load("images/menu_background.jpg");
	}
	
	public String displayWelcome() {
		boolean running = true;
		
		beginInputCapture();
		
		String input = "";
		while (running) {
			input = getCapturedInput();
			
			renderer.setColor(0, 0, 0);
			renderer.clear();
			
			drawCentralizedTexture(background);
			drawOverlay(0.6f);
			
			if (window.isKeyPressed(Keyboard.ENTER)) {
				running = false;
				break;
			}
			
			renderer.setColor(1, 1, 1);
			renderer.setFont(GameFont.GIGANTIC);
			centralizeXAndDraw("BELLUM", 50);
			
			renderer.setFont(GameFont.MEDIUM);
			centralizeXAndDraw("Enter the address of the server", 350);
			
			centralizeXAndDraw(input, 400);
			
			renderer.setFont(GameFont.SMALL);
			centralizeXAndDraw("Leave blank to connect to localhost.", 530);
			
			renderer.draw();
		}
		
		stopCapturingInput();
		
		return input;
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
