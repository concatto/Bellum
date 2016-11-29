package br.univali.game.controllers;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import br.univali.game.GameConstants;
import br.univali.game.graphics.Renderer;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.util.FloatRect;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntVec;
import br.univali.game.util.Utils;

public class HUDController {
	private GameObjectCollection collection;
	private Renderer renderer;
	private IntVec windowSize;
		
	public HUDController(GameObjectCollection collection, Renderer renderer, IntVec windowSize) {
		this.collection = collection;
		this.renderer = renderer;
		this.windowSize = windowSize;
	}
	
	public void updateHUD() {
		drawPlayerHealthBar();
		
		for (Enemy enemy : collection.getEnemies()) {
			drawEnemyHealth(enemy);

		}
	}
	
	public void drawCannonCharge(float fraction, boolean cooldown, boolean charging) {
		float width = GameConstants.HUD_BAR_WIDTH;
		float height = GameConstants.HUD_BAR_HEIGHT;
		
		FloatRect box = new FloatRect(GameConstants.MARGIN, height + GameConstants.MARGIN * 2, width, height);
		drawBar(box, charging ? 1 : fraction, 0.384f, 0.795f, 0.474f);
		
		if (charging) {
			renderer.setColor(0.615f, 0.964f, 0.69f, 1);
			renderer.drawRectangle(box.x, box.y, box.width * fraction, box.height);
		}
	}
	
	public void drawShieldEnergy(float fraction) {
		float width = GameConstants.HUD_BAR_WIDTH;
		float height = GameConstants.HUD_BAR_HEIGHT;
		
		float y = GameConstants.MARGIN * 3 + height * 2;
		FloatRect box = new FloatRect(GameConstants.MARGIN, y, width, height);
		
		drawBar(box, fraction, 0.82f, 0.455f, 0.81f);
	}
	
	private void drawBar(FloatRect box, float fraction, float red, float green, float blue) {
		fraction = (float) Utils.clamp(fraction, 0, 1);
		
		renderer.setColor(0, 0, 0, 0.3f);
		renderer.drawRectangle(box.x, box.y, box.width, box.height);
		
		renderer.setColor(red, green, blue, 1);
		renderer.drawRectangle(box.x, box.y, box.width * fraction, box.height);
	}
	
	private void drawEnemyHealth(Enemy enemy) {
		float width = enemy.getWidth() * GameConstants.HEALTH_BAR_WIDTH_COEF;
		float height = GameConstants.HEALTH_BAR_HEIGHT;
		
		FloatVec center = Geometry.center(enemy.getBoundingBox());
		float x = center.x - (width / 2);
		float y = enemy.getY() - GameConstants.HEALTH_BAR_Y_OFFSET;
		
		float fraction = enemy.getHealth() / (float) enemy.getTotalHealth();
		
		drawBar(new FloatRect(x, y, width, height), fraction, 0.81f, 0, 0); 
	}
	
	public void drawPlayerHealthBar() {
		float margin = GameConstants.MARGIN;
		FloatRect rect = new FloatRect(margin, margin, GameConstants.HUD_BAR_WIDTH, GameConstants.HUD_BAR_HEIGHT);
		CombatObject tank = collection.getTank();
		
		drawBar(rect, tank.getHealth() / (float) tank.getTotalHealth(), 0.85f, 0, 0);
	}
	
	/*(public void drawPlayerKillBar(float kill) {
		float margin = 105;
		FloatRect rect = new FloatRect(10, 100, GameConstants.HUD_BAR_WIDTH, GameConstants.HUD_BAR_HEIGHT);
		
		drawBar(rect, kill, 0.73f, 0.80f, 0.80f);
	}
	*/

	public void drawPowerupBar(float powerupTime) {	
		float width = windowSize.x - GameConstants.MARGIN * 2;
		float height = GameConstants.SPECIAL_BAR_HEIGHT;
		
		float x = GameConstants.MARGIN;
		float y = windowSize.y - GameConstants.MARGIN - height;
		
		drawBar(new FloatRect(x, y, width, height), powerupTime, 0.73f, 0.91f, 0.95f);
	}
	
	public void setCollection(GameObjectCollection collection) {
		this.collection = collection;
	}
}