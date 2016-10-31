package br.univali.game;

public interface GameConstants {
	public static final int HELICOPTER_HEALTH = 50;
	public static final int PLAYER_HEALTH = 100;
	
	public static final float ENEMY_BULLET_SPEED = 0.7f;
	public static final float CANNONBALL_SPEED = 0.0016f;
	public static final float BULLET_SPEED = 1.2f;
	public static final float PLAYER_SPEED = 0.3f;
	public static final float HELICOPTER_SPEED = 0.35f;
	public static final float SPECIAL_BULLET_SPEED = 0.6f;
	
	public static final float GRAVITY = 0.0019f;
	
	public static final float CANNONBALL_DAMAGE_COEF = 22.8f;
	public static final int BULLET_DAMAGE = 5;
	public static final int ENEMY_BULLET_DAMAGE = 10;
	public static final int SPECIAL_BULLET_DAMAGE = 15;
	
	public static final float MAX_CANNONBALL_TIME = 1200;
	public static final float MIN_CANNONBALL_TIME = 150;
	
	public static final int EXPLOSION_FRAME_TIME = 60;
	public static final int SPARK_FRAME_TIME = 80;
	public static final int SHIELD_FRAME_TIME = 30;
	public static final int SPECIAL_EXPLOSION_FRAME_TIME = 14;
	
	public static final long BULLET_COOLDOWN = 150;
	public static final long CANNON_COOLDOWN = 3000;
	public static final long SPECIAL_BULLET_COOLDOWN = 200;
	
	public static final float HEALTH_BAR_HEIGHT = 10;
	public static final float HEALTH_BAR_Y_OFFSET = 26;
	public static final float HEALTH_BAR_WIDTH_COEF = 1.35f;
	
	public static final float CHARGE_BAR_HEIGHT = 12;
	public static final float CHARGE_BAR_Y_OFFSET = 24;
	public static final float CHARGE_BAR_WIDTH_COEF = 2.15f;

	public static final float MARGIN = 10;
	public static final float HUD_BAR_WIDTH = 170;
	public static final float HUD_BAR_HEIGHT = 20;
	
	public static final int GROUND_Y_OFFSET = 70;
	
	public static final float SHIELD_DAMAGE_REDUCTION = 0.9f;
	public static final float SHIELD_RECOVERY_RATE = 0.0004f;
	public static final float SHIELD_DEPLETION_RATE = 0.001f;
	
	public static final long MIN_PICKUP_INTERVAL = 10000;
	public static final long MAX_PICKUP_INTERVAL = 30000;
	
	public static final int HEALTH_PICKUP_VALUE = 50;
	public static final float HEALTH_PICKUP_CHANCE = 0.004f;
	public static final float SPECIAL_PICKUP_CHANCE = 0.001f;
	
	public static final float SPECIAL_DEPLETION_RATE = 0.0001f;
	public static final float SPECIAL_BAR_HEIGHT = 16;
	public static final float PICKUP_FALL_SPEED = 0.2f;
	
}
