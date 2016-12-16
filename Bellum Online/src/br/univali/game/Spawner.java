package br.univali.game;

import br.univali.game.graphics.TextureManager;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObject;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.ObjectType;
import br.univali.game.objects.PlayerTank;
import br.univali.game.objects.Projectile;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntRect;

public class Spawner {
	private TextureManager textureManager;
	private GameObjectCollection collection;
	
	public Spawner(TextureManager textureManager, GameObjectCollection collection) {
		this.textureManager = textureManager;
		this.collection = collection;
	}
	
	public void spawnHealthPickup(FloatVec position) {
		DrawableObject health = new DrawableObject();
		prepareObject(health, ObjectType.HEALTH_PICKUP);
		
		health.setPosition(Geometry.toTopLeft(health.getSize(), position));
		health.setMotionVector(0, 1);
		health.setSpeed(GameConstants.PICKUP_FALL_SPEED);
		
		collection.addPickup(health);
	}
	
	public void spawnSpecialPickup(FloatVec position) {
		DrawableObject special = new DrawableObject();
		prepareObject(special, ObjectType.SPECIAL_PICKUP);
		
		special.setPosition(Geometry.toTopLeft(special.getSize(), position));
		special.setMotionVector(0, 1);
		special.setSpeed(GameConstants.PICKUP_FALL_SPEED);
		
		collection.addPickup(special);
	}

	public void spawnExplosion(FloatVec position) {
		//SoundEffect.EXPLODE.play();
		DrawableObject explosion = new DrawableObject();
		prepareObject(explosion, ObjectType.EXPLOSION);
		
		explosion.setFrameDuration(GameConstants.EXPLOSION_FRAME_TIME);
		explosion.setPosition(Geometry.toTopLeft(explosion.getSize(), position));
		
		collection.addEffect(explosion);
	}
	
	public void spawnSpark(FloatVec position) {
		DrawableObject spark = new DrawableObject();
		prepareObject(spark, ObjectType.SPARK);
		
		spark.setFrameDuration(GameConstants.SPARK_FRAME_TIME);
		spark.setPosition(Geometry.toTopLeft(spark.getSize(), position));
		
		collection.addEffect(spark);
	}
	
	public void spawnSpecialExplosion(FloatVec position) {
		DrawableObject o = new DrawableObject();
		prepareObject(o, ObjectType.SPECIAL_EXPLOSION);
		
		o.setFrameDuration(GameConstants.SPECIAL_EXPLOSION_FRAME_TIME);
		o.setPosition(Geometry.toTopLeft(o.getSize(), position));
		
		collection.addEffect(o);
	}
	
	public void spawnCannonball(float force, GameObject origin, FloatVec destination) {
		System.out.println("Cannon force = " + force);
		Projectile ball = makeProjectile(origin, destination, ObjectType.CANNONBALL);
		ball.setSpeed(ball.getSpeed() * force);
		
		collection.addProjectile(ball);
	}
	
	public void spawnBullet(GameObject source, FloatVec destination) {
		Projectile bullet = makeProjectile(source, destination, ObjectType.BULLET);
		
		collection.addProjectile(bullet);
	}
	
	private Projectile makeProjectile(GameObject source, FloatVec destination, ObjectType type) {		
		FloatVec sourceCenter = Geometry.center(source.getBoundingBox());
		
		float theta = Geometry.angle(sourceCenter, destination);
		
		CombatObject tank = collection.getTank();
		boolean hostile = source != tank;
//		if (!hostile) {
//			if (theta > 0 && theta < (Math.PI / 2)) {
//				//Se o ângulo está no quadrante inferior direito
//				theta = 0;
//			} else if (theta > (Math.PI / 2) || theta < -Math.PI) {
//				//Se o ângulo está no quadrante inferior esquerdo
//				theta = (float) -Math.PI;
//			}
//		}
		
		Projectile p = new Projectile();
		prepareObject(p, type);
		
		switch (type) {
		case BULLET:
			if (hostile) {
				p.setSpeed(GameConstants.ENEMY_BULLET_SPEED);
			} else {
				p.setSpeed(GameConstants.BULLET_SPEED);
			}
			break;
		case CANNONBALL:
			p.setSpeed(GameConstants.CANNONBALL_SPEED);
			p.setAffectedByGravity(true);
			break;
		case SPECIAL_BULLET:
			p.setSpeed(GameConstants.SPECIAL_BULLET_SPEED);
			p.setAnimationRepeated(true);
			p.setFrameDuration(60);
			break;
		default:
			return null;
		}
		
		p.setMotionVector((float) Math.cos(theta), (float) Math.sin(theta));
		p.setRotation(theta);
		p.setHostile(hostile);
		p.setPosition(Geometry.toTopLeft(p.getSize(), sourceCenter));
		return p;
	}
	
	public Enemy spawnHelicopter(FloatVec position) {
		Enemy enemy = new Enemy();
		prepareObject(enemy, ObjectType.HELICOPTER);
		
		enemy.setPosition(position);
		enemy.setHealth(GameConstants.HELICOPTER_HEALTH);
		enemy.setTotalHealth(GameConstants.HELICOPTER_HEALTH);
		enemy.setSpeed(GameConstants.HELICOPTER_SPEED);
		collection.addEnemy(enemy);
		return enemy;
	}
	
	public PlayerTank spawnTank(float center, float ground) {
		PlayerTank tank = new PlayerTank();
		prepareObject(tank, ObjectType.PLAYER_TANK);
		
		tank.setHealth(GameConstants.PLAYER_HEALTH);
		tank.setTotalHealth(GameConstants.PLAYER_HEALTH);
		tank.setSpeed(GameConstants.PLAYER_SPEED);
		tank.setPosition(center - (tank.getWidth() / 2f), ground - tank.getHeight());
		
		collection.setTank(tank);
		return tank;
	}

	public DrawableObject spawnShield() {
		DrawableObject shield = new DrawableObject();
		prepareObject(shield, ObjectType.SHIELD);
		
		shield.setFrameDuration(GameConstants.SHIELD_FRAME_TIME);
		shield.setAnimationRepeated(true);
		collection.addEffect(shield);
		return shield;
	}
	
	public void spawnSpecialBullet(GameObject source, FloatVec destination) {
		Projectile p = makeProjectile(source, destination, ObjectType.SPECIAL_BULLET);
		
		collection.addProjectile(p);
	}
	
	private void prepareObject(DrawableObject object, ObjectType type) {
		object.setType(type);
		
		IntRect frame = textureManager.getObjectTexture(type).getFrames().get(0);
		object.setSize(frame.width, frame.height);
	}
}
