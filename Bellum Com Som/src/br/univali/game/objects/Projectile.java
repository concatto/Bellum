package br.univali.game.objects;

public class Projectile extends DrawableObject {
	private boolean hostile = false;
	
	public Projectile() {

	}

	public void setHostile(boolean hostile) {
		this.hostile = hostile;
	}
	
	public boolean isHostile() {
		return hostile;
	}
}
