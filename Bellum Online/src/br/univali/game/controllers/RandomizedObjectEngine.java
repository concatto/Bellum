package br.univali.game.controllers;

import br.univali.game.GameConstants;
import br.univali.game.behaviour.LinearMotionBehaviour;
import br.univali.game.behaviour.MotionBehaviour;
import br.univali.game.behaviour.SinusoidalMotionBehaviour;
import br.univali.game.behaviour.TriangularMotionBehaviour;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntVec;
import br.univali.game.util.Utils;

public class RandomizedObjectEngine {
	private long lastSpecial;
	private long lastHealth;
	private long healthPerSecond = 0;
	private long specialPerSecond = 0;
	private long enemyInterval = 0;
	private long lastSpawn = 0;
	private float maxEnemies = 6;
	private GameObjectCollection collection;
	private IntVec windowSize;
	
	public RandomizedObjectEngine(GameObjectCollection collection, IntVec windowSize) {
		this.collection = collection;
		this.windowSize = windowSize;
		
		lastSpecial = System.currentTimeMillis();
		lastHealth = System.currentTimeMillis();
		specialPerSecond = 0;
		healthPerSecond = 0;
	}

	public boolean shouldGenerateEnemy() {
		//Revisar este método
		long delta = System.currentTimeMillis() - lastSpawn;
		int enemyCount = collection.getEnemies().size();
		
		if (delta > enemyInterval && enemyCount < maxEnemies) {
			enemyInterval = (long) (Utils.generateRandom(800, 2000));
			lastSpawn = System.currentTimeMillis();
			return true;
		}
		
		return false;
	}
	
	public boolean shouldGenerateSpecial() {
		//Revisar este método também
		long delta = System.currentTimeMillis() - lastSpecial;
		if (delta - specialPerSecond > 1000) {
			specialPerSecond = delta;
		
			if (delta > GameConstants.MIN_PICKUP_INTERVAL) {
				if (Math.random() < GameConstants.SPECIAL_PICKUP_CHANCE * specialPerSecond
						|| delta > GameConstants.MAX_PICKUP_INTERVAL) {
					
					lastSpecial = System.currentTimeMillis();
					specialPerSecond = 0;
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean shouldGenerateHealth() {
		//Idem
		long delta = System.currentTimeMillis() - lastHealth;
		if (delta - healthPerSecond > 1000) {
			healthPerSecond = delta;
			
			if (delta > GameConstants.MIN_PICKUP_INTERVAL) {
				if (Math.random() < GameConstants.HEALTH_PICKUP_CHANCE * healthPerSecond
						|| delta > GameConstants.MAX_PICKUP_INTERVAL) {
					
					lastHealth = System.currentTimeMillis();
					healthPerSecond = 0;
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public FloatVec generatePickupPosition() {
		float x = Utils.generateRandom(windowSize.x * 0.1f, windowSize.x * 0.9f);
		return new FloatVec(x, -200);
	}
	
	public void randomizeEnemy(Enemy enemy) {
		//Pelo amor dos céus alguém revisa e simplifica esse método
		float offScreen = 100;
		
		boolean val = Math.random() > 0.5;
		float startX = val ? -offScreen : (windowSize.x + offScreen); 
		float startY = val ? Utils.generateRandom(0.5f, 1f) * ((windowSize.y / 2)) : ((windowSize.y / 4) * Utils.generateRandom(0.5f, 1f));
		float endX = startX < 0 ? windowSize.x : -50;
		float endY = startY;
		FloatVec startVec = new FloatVec(startX, endY);	
		FloatVec endVec = new FloatVec(endX, endY);
		
		enemy.setPosition(startVec);
		MotionBehaviour behaviour;
		
		behaviour = new LinearMotionBehaviour(startVec, endVec, enemy.getSpeed(), true);
		
		if(Math.random() < 0.4) {
			behaviour = new SinusoidalMotionBehaviour(behaviour, Utils.generateRandom(4, 15), Utils.generateRandom(500, 1100), enemy.getSpeed());
		}
		
		if (Math.random() < 0.3) {
			behaviour = new TriangularMotionBehaviour(behaviour, Utils.generateRandom(3, 13), Utils.generateRandom(300, 1200), enemy.getSpeed());
		}
		
		enemy.setBehaviour(behaviour);
	}
	
	public void randomizePlayerHelicopterPosition(CombatObject helicopter) {
		float xMax = windowSize.x - helicopter.getWidth();
		float yMax = (windowSize.y / 3f) - helicopter.getHeight();
	
		helicopter.setPosition(new FloatVec(Utils.generateRandom(0, xMax), Utils.generateRandom(0, yMax)));
	}
}
