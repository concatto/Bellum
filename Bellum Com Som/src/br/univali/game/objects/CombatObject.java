package br.univali.game.objects;

public class CombatObject extends DrawableObject {
	private int health = 0;
	private int totalHealth = 0;
	
	public CombatObject() {
		
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public int getHealth() {
		return health;
	}

	public int getTotalHealth() {
		return totalHealth;
	}
	
	public void setTotalHealth(int totalHealth) {
		this.totalHealth = totalHealth;
	}
}
