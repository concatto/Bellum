package br.univali.game.controllers;

import br.univali.game.MiscType;
import br.univali.game.Texture;
import br.univali.game.TextureManager;
import br.univali.game.event.input.MouseButton;
import br.univali.game.graphics.Renderer;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntVec;

public class MenuController {
	private PlayerController input;
	private FloatVec windowSize;
	private Texture startButton;
	private Texture youDiedTexture;
	private boolean ready = false;
	private float alpha = 0;
	private long lastUpdate = 0;
	private boolean canContinue;
	
	public MenuController(PlayerController input, TextureManager textureManager, IntVec windowSize) {
		this.input = input;
		this.windowSize = windowSize.toFloat();
		
		startButton = textureManager.getMiscTexture(MiscType.START_BUTTON);
		youDiedTexture = textureManager.getMiscTexture(MiscType.YOU_DIED);
	}
	
	public void drawGameMenu(Renderer renderer) {
		FloatVec pos = Geometry.centerVector(startButton.getSize().toFloat(), windowSize);
		
		renderer.setColor(0, 0, 0, 0.65f);
		renderer.drawRectangle(0, 0, windowSize.x, windowSize.y);
		
		renderer.drawImage(startButton.getId(), pos.x, pos.y);
		canContinue = true;
	}
	
	public void prepareToDie() {
		alpha = 0;
		canContinue = false;
	}
	
	public void drawYouDied(Renderer renderer) {
		FloatVec pos = Geometry.centerVector(youDiedTexture.getSize().toFloat(), windowSize);
		
		renderer.setColor(0, 0, 0, alpha / 2f);
		renderer.drawRectangle(0, 0, windowSize.x, windowSize.y);
		
		renderer.drawImage(youDiedTexture.getId(), pos.x, pos.y, alpha);
		
		if (alpha < 1) {
			if (System.currentTimeMillis() - lastUpdate > 10) {
				alpha += 0.01;
				if (alpha > 1) alpha = 1;
				lastUpdate = System.currentTimeMillis();
			}
		} else {
			canContinue = true;
		}
	}
	
	public boolean didClick() {
		boolean leftPressed = input.getPressedButtons().contains(MouseButton.LEFT);
		
		if (leftPressed && canContinue){
			ready = true;
		}
		
		if (!leftPressed && ready) {
			ready = false;
			return true;
		}
		
		return false;
	}
}
