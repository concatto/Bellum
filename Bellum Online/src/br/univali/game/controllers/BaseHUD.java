package br.univali.game.controllers;

import br.univali.game.GameConstants;
import br.univali.game.graphics.GameFont;
import br.univali.game.graphics.Renderer;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.util.FloatRect;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntVec;
import br.univali.game.util.Utils;
import br.univali.game.window.GameWindow;

public abstract class BaseHUD {
	protected GameObjectCollection collection;
	protected GameWindow window;
	protected Renderer renderer;
	protected CombatObject playerObject;
	protected GameScore score;
		
	public BaseHUD(GameObjectCollection collection, GameWindow window) {
		this.collection = collection;
		this.window = window;
		this.renderer = window.getRenderer();
	}
	
	public void draw() {
		drawPlayerHealthBar();
		drawScore();
		
		for (Enemy enemy : collection.getEnemies()) {
			drawEnemyHealth(enemy);
		}
	}
	
	protected abstract void drawScore();
	
	protected void drawIndicatorBar(int index, float fraction, float red, float green, float blue) {
		drawIndicatorBar(index, fraction, red, green, blue, true);
	}
	
	protected void drawIndicatorBar(int index, float fraction, float red, float green, float blue, boolean drawBackground) {
		float width = GameConstants.HUD_BAR_WIDTH;
		float height = GameConstants.HUD_BAR_HEIGHT;
		
		float y = GameConstants.MARGIN * (index + 1) + height * index;
		FloatRect box = new FloatRect(GameConstants.MARGIN, y, width, height);
		
		drawBar(box, fraction, red, green, blue, drawBackground);
	}
	
	protected void drawLargeBar(float fraction, float red, float green, float blue) {	
		IntVec windowSize = window.getSize();
		
		float width = windowSize.x - GameConstants.MARGIN * 2;
		float height = GameConstants.SPECIAL_BAR_HEIGHT;
		
		float x = GameConstants.MARGIN;
		float y = windowSize.y - GameConstants.MARGIN - height;
		
		drawBar(new FloatRect(x, y, width, height), fraction, red, green, blue);
	}
	
	protected void drawBar(FloatRect box, float fraction, float red, float green, float blue) {
		drawBar(box, fraction, red, green, blue, true);
	}
	
	protected void drawBar(FloatRect box, float fraction, float red, float green, float blue, boolean drawBackground) {
		fraction = (float) Utils.clamp(fraction, 0, 1);
		
		if (drawBackground) {
			renderer.setColor(0, 0, 0, 0.3f);
			renderer.drawRectangle(box.x, box.y, box.width, box.height);
		}
		
		renderer.setColor(red, green, blue, 1);
		renderer.drawRectangle(box.x, box.y, box.width * fraction, box.height);
	}
	
	private void drawEnemyHealth(Enemy enemy) {
		float width = enemy.getWidth() * GameConstants.HEALTH_BAR_WIDTH_COEF;
		float height = GameConstants.HEALTH_BAR_HEIGHT;
		
		FloatVec center = Geometry.centralPoint(enemy.getBoundingBox());
		float x = center.x - (width / 2);
		float y = enemy.getY() - GameConstants.HEALTH_BAR_Y_OFFSET;
		
		float fraction = enemy.getHealth() / (float) enemy.getTotalHealth();
		
		drawBar(new FloatRect(x, y, width, height), fraction, 0.81f, 0, 0); 
	}
	
	public void drawPlayerHealthBar() {		
		float fraction = playerObject.getHealth() / (float) playerObject.getTotalHealth();
		drawIndicatorBar(0, fraction, 0.85f, 0, 0);
	}
	
	public void setCollection(GameObjectCollection collection) {
		this.collection = collection;
	}
	
	public void setPlayerObject(CombatObject playerObject) {
		this.playerObject = playerObject;
	}

	public void setGameScore(GameScore score) {
		this.score = score;
	}
}