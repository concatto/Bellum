package br.univali.game.controllers;

import java.util.List;
import java.util.ListIterator;

import br.univali.game.GameConstants;
import br.univali.game.Spawner;
import br.univali.game.event.collision.BinaryCollisionEvent;
import br.univali.game.event.collision.CollisionEvent;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.ObjectType;
import br.univali.game.objects.PlayerTank;
import br.univali.game.objects.Projectile;
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
	private RandomizedObjectEngine randomizedEngine;
	private float groundLevel;

	public LogicController(GameObjectCollection collection, Spawner spawner, IntVec windowSize) {
		this.collection = collection;
		this.spawner = spawner;
		this.windowSize = windowSize;
		this.groundLevel = windowSize.y - GameConstants.GROUND_Y_OFFSET;
		this.randomizedEngine = new RandomizedObjectEngine(collection, windowSize);
	}

	public void update(float delta) {
		cleanupBullets();
		
		if (randomizedEngine.shouldGenerateEnemy()) {
			randomizedEngine.randomizeEnemy(spawner.spawnHelicopter());
		}
		
		if (randomizedEngine.shouldGenerateHealth()) {
			spawner.spawnHealthPickup(randomizedEngine.generatePickupPosition());
		}
		
		if (randomizedEngine.shouldGenerateSpecial()) {
			spawner.spawnSpecialPickup(randomizedEngine.generatePickupPosition());
		}
		
		updateEnemies(delta);
		handleDeaths();
		handleRespawns();
	}
	
	private void handleRespawns() {
		collection.getEnemies().stream()
				.filter(e -> !e.isBot())
				.filter(e -> e.shouldRespawn())
				.forEach(this::respawnHelicopter);
	}

	private void handleDeaths() {
		for (ListIterator<Enemy> it = collection.getEnemies().listIterator(); it.hasNext(); ) {
			Enemy e = it.next();
			
			if (e.hasDied()) {
				spawner.spawnExplosion(Geometry.center(e.getBoundingBox()));
				if (e.isBot()) {
					it.remove();
				} else {
					e.prepareRespawn(3000);
					e.setAffectedByGravity(true);
				}
			}
		}
	}

	public void cleanupBullets() {
		collection.getProjectiles().removeIf(p -> {
			FloatRect itemBox = p.getBoundingBox();
			
			FloatRect windowBox = new FloatRect(0, 0, windowSize.x, windowSize.y);
			
			if ((p.getType() == ObjectType.BULLET && !Geometry.intersects(windowBox, itemBox)) ||
				(p.getType() == ObjectType.CANNONBALL && !Geometry.isWithinLateralBounds(itemBox, windowBox))) {
				
				return true;
			}
			
			return false;
		});
	}

	public void handleEnemyCollisions(List<BinaryCollisionEvent<Projectile, Enemy>> collisions) {
		for (BinaryCollisionEvent<Projectile, Enemy> event : collisions) {
			Projectile origin = event.getOrigin();
			Enemy target = event.getTarget();
			
			target.setHealth(target.getHealth() - damageForProjectile(origin));
			
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
			
			if (ObjectType.isProjectile(obj.getType())) {
				obj.setPosition(obj.getX(), groundLevel - (obj.getHeight() / 2));
				
				terminateProjectile((Projectile) obj);
			} else if (obj.getType() == ObjectType.HELICOPTER) {
				CombatObject c = (CombatObject) obj;
				
				if (!c.isDead()) {
					c.setHealth(0);
				}
			}
		}
	}
	
	public void handlePickupCollisions(List<CollisionEvent<DrawableObject>> events) {
		for (CollisionEvent<DrawableObject> evt : events) {
			DrawableObject obj = evt.getOrigin();
			PlayerTank tank = collection.getTank();
			
			if (obj.getType() == ObjectType.HEALTH_PICKUP) {
				int health = tank.getHealth() + GameConstants.HEALTH_PICKUP_VALUE;
				
				tank.setHealth(Math.min(health, tank.getTotalHealth()));
			} else if (obj.getType() == ObjectType.SPECIAL_PICKUP) {
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
		collection.getEnemies().stream().filter(e -> e.getBehaviour() != null).forEach(enemy -> {
			updateEnemyMotionVector(enemy, delta);
			updateEnemyShot(enemy);
		});
	}
	
	private void updateEnemyShot(Enemy enemy) {
		long time = System.currentTimeMillis();
		if (time - enemy.getLastShot() > enemy.getShotInterval()) {
			//SoundEffect.ENEMYSHOT.play();
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
	
	public void prepareGame() {
		CombatObject tank = collection.getTank();
		FloatRect box = tank.getBoundingBox();
		box.y = groundLevel - box.height;
		box.x = (windowSize.x / 2) - (box.width / 2);
		tank.setDirection(Direction.RIGHT);
		tank.setHealth(tank.getTotalHealth());
	}
	
	public float getGroundLevel() {
		return groundLevel;
	}

	public void respawnHelicopter(CombatObject helicopter) {
		helicopter.respawn();
		helicopter.setAffectedByGravity(false);
		randomizedEngine.randomizePlayerHelicopterPosition(helicopter);
	}
}
