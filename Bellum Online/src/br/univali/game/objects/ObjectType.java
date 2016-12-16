package br.univali.game.objects;

public enum ObjectType {
	BULLET, CANNONBALL, HELICOPTER, TANK, PLAYER_TANK,
	EXPLOSION, SPARK, SHIELD, SPECIAL_BULLET,
	SPECIAL_EXPLOSION, HEALTH_PICKUP, SPECIAL_PICKUP, KILLS, FIRE;
	
	public int getTextureId() {
		return ordinal();
	}
}
