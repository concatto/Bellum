package br.univali.game.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameObjectCollection implements Serializable {
	private List<Enemy> enemies = new ArrayList<>();
	private List<Projectile> projectiles = new ArrayList<>();
	private List<DrawableObject> effects = new ArrayList<>();
	private List<DrawableObject> pickups = new ArrayList<>();
	private PlayerTank tank;
	private Map<String, CombatObject> playerObjects = new HashMap<>();
	
	public void addPlayerObject(String identifier, CombatObject object) {
		playerObjects.put(identifier, object);
	}
	
	public CombatObject getPlayerObject(String identifier) {
		return playerObjects.get(identifier);
	}
	
	public void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}
	
	public void addProjectile(Projectile projectile) {
		projectiles.add(projectile);
	}
	
	public void setTank(PlayerTank tank) {
		this.tank = tank;
	}
	
	public List<Enemy> getEnemies() {
		return enemies;
	}
	
	public List<Projectile> getProjectiles() {
		return projectiles;
	}
	
	public PlayerTank getTank() {
		return tank;
	}
	
	public List<DrawableObject> getEffects(){
		return effects;
	}
	
	public List<DrawableObject> getPickups() {
		return pickups;
	}

	public List<DrawableObject> getDrawableObjects() {
		List<DrawableObject> drawables = new ArrayList<>(enemies);
		if (tank != null) {
			drawables.add(tank);
		}
		drawables.addAll(pickups);
		drawables.addAll(projectiles);
		drawables.addAll(effects);
		
		return drawables;
	}
	
	public void addEffect(DrawableObject effect) {
		effects.add(effect);
	}
	
	//Complexidade O(|objetos|). Tenta remover objeto de todas as listas.
	public void removeObject(GameObject object) {
		enemies.remove(object);
		projectiles.remove(object);
		effects.remove(object);
		pickups.remove(object);
	}

	public void removeEnemy(Enemy enemy) {
		enemies.remove(enemy);
	}

	public void removeProjectile(Projectile projectile) {
		projectiles.remove(projectile);
	}
	
	public void clear() {
		projectiles.clear();
		effects.clear();
		enemies.clear();
		pickups.clear();
	}

	public void addPickup(DrawableObject pickup) {
		pickups.add(pickup);
	}
	
	public void removePickup(DrawableObject pickup) {
		pickups.remove(pickup);
	}

	public void removeEffect(DrawableObject effect) {
		effects.remove(effect);
		
	}
}

