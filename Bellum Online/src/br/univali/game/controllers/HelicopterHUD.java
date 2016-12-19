package br.univali.game.controllers;

import br.univali.game.graphics.GameFont;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.PlayerTank;
import br.univali.game.window.GameWindow;

public class HelicopterHUD extends BaseHUD {
	private CombatObject helicopter;
	
	public HelicopterHUD(GameObjectCollection collection, GameWindow window) {
		super(collection, window);
	}
	
	@Override
	public void draw() {
		super.draw();
		
		drawTankHealth();
	}
	
	private void drawTankHealth() {
		PlayerTank tank = collection.getTank();
		drawLargeBar(tank.getHealth() / (float) tank.getTotalHealth(), 0.85f, 0, 0);
	}

	@Override
	public void setPlayerObject(CombatObject playerObject) {
		super.setPlayerObject(playerObject);
		this.helicopter = playerObject;
	}

	@Override
	protected void drawScore() {
		renderer.setFont(GameFont.MEDIUM_BOLD);
		renderer.setColor(0, 0.5f, 0);
		renderer.drawText("Helicopters: " + score.getHelicoptersScore(), 400, 10);
		renderer.setColor(0.5f, 0f, 0);
		renderer.drawText("Tank: " + score.getTankScore(), 400, 40);
	}
}
