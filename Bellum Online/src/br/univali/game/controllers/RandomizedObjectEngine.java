package br.univali.game.controllers;

import java.util.function.Function;

import br.univali.game.behaviour.LinearMotionBehaviour;
import br.univali.game.behaviour.MotionBehaviour;
import br.univali.game.behaviour.SinusoidalMotionBehaviour;
import br.univali.game.behaviour.TriangularMotionBehaviour;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.util.Countdown;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntVec;
import br.univali.game.util.Utils;

public class RandomizedObjectEngine {
	private ProbabilityGenerator healthGenerator;
	private ProbabilityGenerator specialGenerator;
	private GameObjectCollection collection;
	private IntVec windowSize;
	private Countdown countdown;
	private long startTime = System.currentTimeMillis();
	
	public RandomizedObjectEngine(GameObjectCollection collection, IntVec windowSize) {
		this.collection = collection;
		this.windowSize = windowSize;
		this.healthGenerator = new ProbabilityGenerator(0, 0, 45000, 1);
		this.specialGenerator = new ProbabilityGenerator(0, 0, 60000, 1);
	}

	public boolean shouldGenerateEnemy() {
		if (countdown == null) {
			countdown = Countdown.createAndStart(ProbabilityGenerator.CHECK_INTERVAL);
		}
		
		int enemies = collection.getEnemies().size();
		long delta = System.currentTimeMillis() - startTime;
		if (countdown.finished()) {
			countdown = Countdown.createAndStart(ProbabilityGenerator.CHECK_INTERVAL);
			
			//Interpolação (segundos, multiplicador) entre (0, 1) e (90, 3)
			float multiplier = Utils.lerp(delta, 0, 1, 90000, 3);
			float chance = (1 - Utils.growthCurve(enemies, 0.3f)) * multiplier;
			if (Utils.generateRandom() < chance) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean shouldGenerateSpecial() {
		return specialGenerator.calculate();
	}
	
	public boolean shouldGenerateHealth() {
		return healthGenerator.calculate();
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
	
	private class ProbabilityGenerator {
		private static final long CHECK_INTERVAL = 100;
		private Countdown countdown;
		private long last;
		private Function<Long, Float> probabilityFunction;
		
		public ProbabilityGenerator(float x0, float y0, float x1, float y1) {
			probabilityFunction = delta -> {
				return Utils.lerp(delta / (float) CHECK_INTERVAL, x0, y0, x1, y1);
			};
			
			last = System.currentTimeMillis();
		}		
		
		public boolean calculate() {
			if (countdown == null) {
				countdown = Countdown.createAndStart(CHECK_INTERVAL);
			}
			
			long delta = System.currentTimeMillis() - last;
			if (countdown.finished()) {
				countdown = Countdown.createAndStart(CHECK_INTERVAL);
				
				float chance = probabilityFunction.apply(delta);
				if (Utils.generateRandom() < chance) {
					last = System.currentTimeMillis();
					return true;
				}
			}
			
			return false;
		}
	}
}
