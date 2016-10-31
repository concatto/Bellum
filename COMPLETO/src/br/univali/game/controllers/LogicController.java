package br.univali.game.controllers;

import java.util.List;

import br.univali.game.GameConstants;
import br.univali.game.GameObjectCollection;
import br.univali.game.Spawner;
import br.univali.game.behaviour.LinearMotionBehaviour;
import br.univali.game.behaviour.MotionBehaviour;
import br.univali.game.behaviour.SinusoidalMotionBehaviour;
import br.univali.game.behaviour.TriangularMotionBehaviour;
import br.univali.game.event.collision.BinaryCollisionEvent;
import br.univali.game.event.collision.CollisionEvent;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.ObjectType;
import br.univali.game.objects.PlayerTank;
import br.univali.game.objects.Projectile;
import br.univali.game.sound.SoundEffect;
import br.univali.game.util.Direction;
import br.univali.game.util.FloatRect;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntVec;
import br.univali.game.util.Utils;

public class LogicController {
	private GameObjectCollection collection;
	private Spawner spawner;
	private IntVec windowSize;
	private long tempExe = 0;
	private long lastSpawn = 0;
	private float enemyCount = Float.MAX_VALUE;
	private float groundLevel;
	private long lastSpecial;
	private long lastHealth;
	private int kills = 0;
	
	public LogicController(GameObjectCollection collection, Spawner spawner, IntVec windowSize) {
		this.collection = collection;
		this.spawner = spawner;
		this.windowSize = windowSize;
		this.groundLevel = windowSize.y - GameConstants.GROUND_Y_OFFSET;
	}

	public void cleanupBullets() {
		collection.getProjectiles().removeIf(p -> {
			FloatRect itemBox = p.getBoundingBox();
			
			FloatRect windowBox = new FloatRect(0, 0, windowSize.x, windowSize.y);
			
			if ((p.getType() == ObjectType.BULLET && !Geometry.intersects(windowBox, itemBox)) ||
				(p.getType() == ObjectType.CANNONBALL && !isWithinLateralBounds(itemBox, windowBox))) {
				
				return true;
			}
			
			return false;
		});
	}
	
	private static boolean isWithinLateralBounds(FloatRect itemBox, FloatRect windowBox) {
		return itemBox.x + itemBox.width > 0 && itemBox.x < windowBox.width;
	}

	public void handleEnemyCollisions(List<BinaryCollisionEvent<Projectile, Enemy>> collisions) {
		for (BinaryCollisionEvent<Projectile, Enemy> event : collisions) {
			Projectile origin = event.getOrigin();
			Enemy target = event.getTarget();
			
			int damage = damageForProjectile(origin);
			
			target.setHealth(target.getHealth() - damage);
			
			if (target.getHealth() <= 0) {
				spawner.spawnExplosion(Geometry.center(target.getBoundingBox()));
				collection.removeEnemy(target);	
				SoundEffect.ENEMYDEAD.play();			
				kills ++;
			}
			
			terminateProjectile(origin);
		}
	}
	
	private void terminateProjectile(Projectile p) {
		FloatVec center = Geometry.center(p.getBoundingBox());
		
		if (p.getType() == ObjectType.CANNONBALL) {
			spawner.spawnExplosion(center);
		} else if (p.getType() == ObjectType.BULLET) {
			spawner.spawnSpark(center);
		} else if (p.getType() == ObjectType.SPECIAL_BULLET) {
			spawner.spawnSpecialExplosion(center);
		}
		
		collection.removeProjectile(p);
	}
	
	public void handleGroundCollisions(List<CollisionEvent<DrawableObject>> collisions) {
		for (CollisionEvent<DrawableObject> evt : collisions) {
			DrawableObject obj = evt.getOrigin();
			
			if (obj.getType() == ObjectType.CANNONBALL || obj.getType() == ObjectType.BULLET) {
				terminateProjectile((Projectile) obj);
			} else if (obj.getType() == ObjectType.HEALTH_PICKUP || obj.getType() == ObjectType.SPECIAL_PICKUP) {
				obj.setMotionVector(0, 0);
				obj.setY(groundLevel - obj.getHeight());
			}
		}
	}
	
	public void handlePickupCollisions(List<CollisionEvent<DrawableObject>> events) {
		for (CollisionEvent<DrawableObject> evt : events) {
			DrawableObject obj = evt.getOrigin();
			PlayerTank tank = collection.getTank();
			
			if (obj.getType() == ObjectType.HEALTH_PICKUP) {
				SoundEffect.LIFEUP.play();
				tank.setHealth(tank.getHealth() + GameConstants.HEALTH_PICKUP_VALUE);
				
				if (tank.getHealth() > tank.getTotalHealth()) {
					tank.setHealth(tank.getTotalHealth());
				}
			} else if (obj.getType() == ObjectType.SPECIAL_PICKUP) {
				SoundEffect.GOTSPECIAL.play();
				tank.setPoweredUp(true);
			}
			
			collection.removePickup(obj);
		}
	}
	
	public void handlePlayerCollisions(List<CollisionEvent<Projectile>> collisions) {
		for (CollisionEvent<Projectile> c : collisions) {
			Projectile p = c.getOrigin();
			PlayerTank tank = collection.getTank();
			
			int damage = damageForProjectile(p);
			if (tank.isShielded()) {
				damage -= damage * GameConstants.SHIELD_DAMAGE_REDUCTION;
			}
			tank.setHealth(tank.getHealth() - damage);
			
			terminateProjectile(p);
		}
	}
	
	private static int damageForProjectile(Projectile p) {
		switch (p.getType()) {
		case BULLET:
			return p.isHostile() ? GameConstants.ENEMY_BULLET_DAMAGE : GameConstants.BULLET_DAMAGE;
		case CANNONBALL:
			return Math.round(GameConstants.CANNONBALL_DAMAGE_COEF * p.getSpeed());
		case SPECIAL_BULLET:
			return GameConstants.SPECIAL_BULLET_DAMAGE;
		default:
			return 0;
		}
	}
	
	public void updateEnemies(float delta) {
		for (Enemy enemy : collection.getEnemies()) {
			updateEnemyMotionVector(enemy, delta);
			updateEnemyShot(enemy);
		}
	}
	
	private void updateEnemyShot(Enemy enemy) {
		long time = System.currentTimeMillis();
		if (time - enemy.getLastShot() > enemy.getShotInterval()) {
			SoundEffect.ENEMYSHOT.play();
			spawner.spawnBullet(enemy, Geometry.center(collection.getTank().getBoundingBox()));
			
			enemy.setLastShot(time);
			enemy.setShotInterval(Math.round(Utils.generateRandom(300, 2000)));
		}
	}
	
	private void updateEnemyMotionVector(Enemy enemy, float delta) {
		FloatVec next = enemy.getBehaviour().computeNextVector(delta);
		enemy.setMotionVector(next);
		
		if (next.x < 0) {
			enemy.setDirection(Direction.LEFT);
		} else if (next.x > 0) {
			enemy.setDirection(Direction.RIGHT);
		}
	}
	
	public void generateAndSpawnEnemy() {
		float offScreen = 100;
		
		boolean val = Math.random() > 0.5;
		float startPositionXSetter = val ? -offScreen : (windowSize.x + offScreen); 
		float startPositionYSetter = val ? Utils.generateRandom(0.5f,1f)*((windowSize.y/2)) : ((windowSize.y/4)*Utils.generateRandom(0.5f, 1f));
		float endPositionXSetter = startPositionXSetter < 0 ? windowSize.x : -50;
		float endPositionYSetter = startPositionYSetter;
		FloatVec startVec = new FloatVec(startPositionXSetter, endPositionYSetter);	
		FloatVec endVec = new FloatVec(endPositionXSetter, endPositionYSetter);
		
		Enemy enemy = spawner.spawnHelicopter(startVec);
		MotionBehaviour behavior;
		
		behavior = new LinearMotionBehaviour(startVec,endVec,enemy.getSpeed(), true);
		
		if(Math.random() < 0.4) {
			behavior = new SinusoidalMotionBehaviour(behavior, Utils.generateRandom(4, 15), Utils.generateRandom(500, 1100), enemy.getSpeed());
		}
		
		if (Math.random() < 0.3) {
			behavior = new TriangularMotionBehaviour(behavior, Utils.generateRandom(3, 13), Utils.generateRandom(300, 1200), enemy.getSpeed());
		}
		
		enemy.setBehaviour(behavior);
	}
	
	public void prepareGame() {
		CombatObject tank = collection.getTank();
		FloatRect box = tank.getBoundingBox();
		box.y = groundLevel - box.height;
		box.x = (windowSize.x / 2) - (box.width / 2);
		tank.setDirection(Direction.RIGHT);
		tank.setHealth(tank.getTotalHealth());
		
		lastSpecial = System.currentTimeMillis();
		lastHealth = System.currentTimeMillis();
	}
	
	public void tryGenerateEnemy() {
		long delta = System.currentTimeMillis() - lastSpawn;
		
		if (collection.getEnemies().size() >= enemyCount) {
			lastSpawn = System.currentTimeMillis();
		} else if (delta > tempExe) {
			tempExe = (long)(Utils.generateRandom(1f, 5f)*800);
			enemyCount = Utils.generateRandom(3, 6);
			lastSpawn = System.currentTimeMillis();
			
			generateAndSpawnEnemy();
		}
	}
	
	public void tryGenerateSpecial() {
		long delta = System.currentTimeMillis() - lastSpecial;
		
		if (delta > GameConstants.MIN_PICKUP_INTERVAL) {
			if (Math.random() < GameConstants.SPECIAL_PICKUP_CHANCE || delta > GameConstants.MAX_PICKUP_INTERVAL) {
				float x = Utils.generateRandom(windowSize.x * 0.1f, windowSize.x * 0.9f);
				spawner.spawnSpecialPickup(new FloatVec(x, -200));
				
				lastSpecial = System.currentTimeMillis();
			}
		}
	}
	
	public void tryGenerateHealth() {
		long delta = System.currentTimeMillis() - lastHealth;
		
		if (delta > GameConstants.MIN_PICKUP_INTERVAL) {
			if (Math.random() < GameConstants.HEALTH_PICKUP_CHANCE || delta > GameConstants.MAX_PICKUP_INTERVAL) {
				float x = Utils.generateRandom(windowSize.x * 0.1f, windowSize.x * 0.9f);
				spawner.spawnHealthPickup(new FloatVec(x, -200));
				
				lastHealth = System.currentTimeMillis();
			}
		}
	}
	
	public float getGroundLevel() {
		return groundLevel;
	}
	
	public int getKills(){
		return kills;
	}
}
