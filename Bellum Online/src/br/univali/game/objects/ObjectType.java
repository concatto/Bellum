package br.univali.game.objects;

import java.util.EnumSet;

public enum ObjectType {
	BULLET, CANNONBALL, HELICOPTER, TANK, PLAYER_TANK,
	EXPLOSION, SPARK, SHIELD, SPECIAL_BULLET,
	SPECIAL_EXPLOSION, HEALTH_PICKUP, SPECIAL_PICKUP, KILLS, FIRE, PLAYER_HELICOPTER, BIG_EXPLOSION;
	
	private static final EnumSet<ObjectType> bullets = EnumSet.of(BULLET, CANNONBALL, SPECIAL_BULLET);
	
	public int getTextureId() {
		return ordinal();
	}
	
	public static boolean isProjectile(ObjectType type) {
		return bullets.contains(type);
	}
}
