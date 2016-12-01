package br.univali.game.objects;

@SuppressWarnings("serial")
public class PlayerTank extends CombatObject {
	private boolean shielded = false;
	private boolean poweredUp = false;
	private float shieldEnergy = 1;
	private float powerupTime = 0;
	private boolean cannonCharging = false;
	
	public PlayerTank() {
		
	}
	
	public void setShielded(boolean shielded) {
		this.shielded = shielded;
	}
	
	public boolean isShielded() {
		return shielded;
	}

	public float getShieldEnergy() {
		return shieldEnergy;
	}

	public void setShieldEnergy(float shieldEnergy) {
		this.shieldEnergy = shieldEnergy;
	}

	public boolean isPoweredUp() {
		return poweredUp;
	}

	public void setPoweredUp(boolean poweredUp) {
		if (poweredUp) powerupTime = 1;
		this.poweredUp = poweredUp;
	}
	
	public float getPowerupTime() {
		return powerupTime;
	}
	
	public void setPowerupTime(float powerupTime) {
		this.powerupTime = powerupTime;
	}

	public boolean isCannonCharging() {
		return cannonCharging;
	}
	
	public void setCannonCharging(boolean cannonCharging) {
		this.cannonCharging = cannonCharging;
	}
}
