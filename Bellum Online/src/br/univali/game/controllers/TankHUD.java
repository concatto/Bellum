package br.univali.game.controllers;

import br.univali.game.graphics.GameFont;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.PlayerTank;
import br.univali.game.window.GameWindow;

public class TankHUD extends BaseHUD {
	private PlayerTank tank;
	
	public TankHUD(GameObjectCollection collection, GameWindow window) {
		super(collection, window);
		setCollection(collection);
	}
	
	@Override
	public void draw() {
		super.draw();
		
		drawCannonCharge();
		drawShieldEnergy();
		
		if (tank.isPoweredUp()) {
			drawPowerupBar();
		}
	}
	
	@Override
	protected void drawScore() {
		renderer.setFont(GameFont.MEDIUM_BOLD);
		renderer.setColor(0, 0.5f, 0);
		renderer.drawText("Tank: " + score.getTankScore(), 400, 10);
		renderer.setColor(0.5f, 0, 0);
		renderer.drawText("Helicopters: " + score.getHelicoptersScore(), 400, 40);
	}
	
	public void drawCannonCharge() {
		drawIndicatorBar(1, 1 - tank.getCannonRecovery(), 0.384f, 0.795f, 0.474f);
		
		drawIndicatorBar(1, tank.getCannonCharge(), 0.615f, 0.964f, 0.69f, false);
	}
	
	public void drawShieldEnergy() {
		drawIndicatorBar(2, tank.getShieldEnergy(), 0.82f, 0.455f, 0.81f);
	}
		
	public void drawPowerupBar() {	
		drawLargeBar(tank.getPowerupTime(), 0.73f, 0.91f, 0.95f);
	}
	
	@Override
	public void setCollection(GameObjectCollection collection) {
		super.setCollection(collection);
		this.tank = collection.getTank();
		setPlayerObject(tank);
	}
}
