package br.univali.game.controllers;

import java.util.ArrayList;
import java.util.List;

import br.univali.game.GameConstants;
import br.univali.game.event.collision.BinaryCollisionEvent;
import br.univali.game.event.collision.CollisionEvent;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObject;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.Projectile;
import br.univali.game.util.FloatRect;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;

public class PhysicsController {
	private GameObjectCollection collection;
	private float groundLevel;

	public PhysicsController(GameObjectCollection collection, float groundLevel) {
		this.collection = collection;
		this.groundLevel = groundLevel;
	}
	
	public void updatePositions(float delta) {
		for (DrawableObject drawable : collection.getDrawableObjects()) {
			updateObjectPosition(drawable, delta);
		}
	}
	
	public List<BinaryCollisionEvent<Projectile, Enemy>> checkEnemyCollisions() {
		List<BinaryCollisionEvent<Projectile, Enemy>> events = new ArrayList<>();
		
		for (Projectile p : collection.getProjectiles()) {
			if (p.isHostile()) continue;
			
			FloatRect aoe = createAreaOfEffect(projectileRadius(p), Geometry.centralPoint(p.getBoundingBox()));
			
			for (Enemy enemy : collection.getEnemies()) {
				if (enemy.isDead()) continue;
				
				if (didCollide(p, enemy)) {
					for (Enemy sec : collection.getEnemies()) {
						 if (sec != enemy && Geometry.intersects(sec.getBoundingBox(), aoe)) {
							 events.add(new BinaryCollisionEvent<>(p, sec));
						 }
					}
					
					events.add(new BinaryCollisionEvent<>(p, enemy));
					break;
				}
			}
		}
		
		return events;
	}
	
	private static float projectileRadius(Projectile p) {
		switch (p.getType()) {
		case CANNONBALL:
			return 80;
		default:
			return p.getWidth() / 2;
		}
	}
	
	private static FloatRect createAreaOfEffect(float radius, FloatVec center) {
		return new FloatRect(center.x - radius, center.y - radius, radius * 2, radius * 2);
	}
	
	private static boolean didCollide(GameObject subject, GameObject context) {
		FloatVec[] boxVertices = Geometry.vertices(subject.getBoundingBox());
		FloatVec[] prevBoxVertices = Geometry.vertices(subject.getLastBoundingBox());
		
		for (int i = 0; i < boxVertices.length; i++) {
			if (Geometry.intersects(prevBoxVertices[i], boxVertices[i], context.getBoundingBox())) {
				
				return true;
			}
		}
		
		return false;
	}

	public List<CollisionEvent<DrawableObject>> checkGroundCollisions() {
		List<CollisionEvent<DrawableObject>> events = new ArrayList<>();
		
		for (DrawableObject obj : collection.getDrawableObjects()) {
			boolean falling = obj.getMotionVector().y > 0;
			if (obj.getY() + obj.getHeight() >= groundLevel && falling) {
				events.add(new CollisionEvent<>(obj));
			}
		}
		
		return events;
	}
	
	public List<CollisionEvent<Projectile>> checkPlayerCollisions() {
		List<CollisionEvent<Projectile>> events = new ArrayList<>();
		
		for (Projectile p : collection.getProjectiles()) {
			if (p.isHostile() && didCollide(p, collection.getTank())) {
				events.add(new CollisionEvent<>(p));
			}
		}
		
		return events;
	}
	
	public List<CollisionEvent<DrawableObject>> checkPickupCollisions() {
		List<CollisionEvent<DrawableObject>> events = new ArrayList<>();
		
		for (DrawableObject obj : collection.getPickups()) {
			if (didCollide(obj, collection.getTank())) {
				events.add(new CollisionEvent<>(obj));
			}
		}
		
		return events;
	}
	
	private void updateObjectPosition(GameObject object, float delta) {
		float groundDelta = object.getY() + object.getHeight() - groundLevel;
		
		FloatVec v = object.getMotionVector();
		float coefficient = delta * object.getSpeed();
		
		object.setX(object.getX() + coefficient * v.x);
		
		if (groundDelta > 0) {
			if (object.isPhysical()) {
				object.setY(groundLevel - object.getHeight());
			}
		} else {
			object.setY(object.getY() + coefficient * v.y);
			
			if (object.isAffectedByGravity()) {
				object.setMotionVector(v.x, v.y + GameConstants.GRAVITY * delta);
			}
		}
	}
}
