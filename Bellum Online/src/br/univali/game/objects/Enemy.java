package br.univali.game.objects;

import br.univali.game.behaviour.MotionBehaviour;

public class Enemy extends CombatObject {
	private MotionBehaviour behaviour;
	private long shotInterval = 0;
	private long lastShot = 0;
	
	public Enemy() {

	}
	
	public void setBehaviour(MotionBehaviour behaviour) {
		this.behaviour = behaviour;
	}
	
	public MotionBehaviour getBehaviour() {
		return behaviour;
	}
	
	public boolean isBot() {
		return behaviour != null;
	}
	
	public long getLastShot() {
		return lastShot;
	}
	
	public void setLastShot(long lastShot) {
		this.lastShot = lastShot;
	}

	public long getShotInterval() {
		return shotInterval;
	}

	public void setShotInterval(long shotInterval) {
		this.shotInterval = shotInterval;
	}
}
