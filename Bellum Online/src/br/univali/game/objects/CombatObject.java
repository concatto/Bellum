package br.univali.game.objects;

import br.univali.game.util.Countdown;

public class CombatObject extends DrawableObject {
	private Countdown respawnCountdown;
	private int health = 0;
	private int totalHealth = 0;
	private boolean died = false;
	
	public CombatObject() {
		
	}

	public void setHealth(int health) {
		this.health = health;
		
		if (isDead()) {
			died = true;
		} else {
			died = false;
		}
	}
	
	public int getHealth() {
		return health;
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	public boolean hasDied() {
		boolean r = died;
		died = false;
		return r;
	}

	public int getTotalHealth() {
		return totalHealth;
	}
	
	public void setTotalHealth(int totalHealth) {
		this.totalHealth = totalHealth;
	}
	
	public void prepareRespawn(long ms) {
		respawnCountdown = Countdown.createAndStart(ms);
	}
	
	public boolean isRespawning() {
		return respawnCountdown != null && respawnCountdown.running();
	}
	
	public boolean shouldRespawn() {
		return respawnCountdown != null && respawnCountdown.finished();
	}

	public void respawn() {
		setHealth(getTotalHealth());
		respawnCountdown = null;
	}

	public double getRemainingRespawnTime() {
		if (respawnCountdown == null) {
			return 0;
		}
		
		return respawnCountdown.remaining();
	}
}
