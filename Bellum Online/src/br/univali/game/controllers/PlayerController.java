package br.univali.game.controllers;

import java.util.Set;
import java.util.TreeSet;

import br.univali.game.GameConstants;
import br.univali.game.GameObjectCollection;
import br.univali.game.Spawner;
import br.univali.game.event.input.InputEventType;
import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseButton;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.PlayerTank;
import br.univali.game.sound.SoundEffect;
import br.univali.game.util.Direction;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntVec;
import br.univali.game.util.Utils;

public class PlayerController {
	private Set<MouseButton> pressedButtons = new TreeSet<>();
	private Set<Integer> pressedKeys = new TreeSet<>();
	
	private MouseButton bulletButton;
	private MouseButton cannonballButton;
	private int leftKey;
	private int rightKey;
	private int shieldKey;
	
	private Spawner spawner;
	private PlayerTank tank;
	private IntVec windowSize;
	private DrawableObject shield;
	private GameObjectCollection collection;
	
	private IntVec mousePosition;
	private long lastBullet = 0;
	private long lastCannon = 0;
	private boolean cannonCharging = false;
	
	public PlayerController(Spawner spawner, GameObjectCollection collection, IntVec windowSize) {
		this.spawner = spawner;
		this.collection = collection;
		this.tank = collection.getTank();
		this.windowSize = windowSize;
	}
	
	public void setLeftKey(int leftKey) {
		this.leftKey = leftKey;
	}
	
	public void setRightKey(int rightKey) {
		this.rightKey = rightKey;
	}
	
	public void setShieldKey(int shieldKey) {
		this.shieldKey = shieldKey;
	}
	
	public void setBulletButton(MouseButton bulletButton) {
		this.bulletButton = bulletButton;
	}
	
	public void setCannonballButton(MouseButton cannonballButton) {
		this.cannonballButton = cannonballButton;
	}

	public void handleKey(KeyboardEvent e) {
		if (e.getType() == InputEventType.PRESS) {
			pressedKeys.add(e.getKey());
		} else {
			pressedKeys.remove(e.getKey());
		}
	}

	public Direction computeDirection() {
		boolean left = pressedKeys.contains(leftKey);
		boolean right = pressedKeys.contains(rightKey);
		
		//XNOR: Se ambos são true ou ambos são false
		if (!(left ^ right)) {
			return Direction.NONE;
		} else if (left) {
			return Direction.LEFT;
		} else { //Right
			return Direction.RIGHT;
		}
	}

	public void handleMouse(MouseEvent e) {
		if (e.getType() == InputEventType.PRESS) {
			pressedButtons.add(e.getButton());
		} else {
			pressedButtons.remove(e.getButton());
		}
	}
	
	public void setMousePosition(IntVec mousePosition) {
		this.mousePosition = mousePosition;
	}
	
	public IntVec getMousePosition() {
		return mousePosition;
	}
	
	public void updateTank(float delta) {
		//Teclado
		Direction direction = computeDirection();
		
		if (direction == Direction.NONE) {
			tank.setMotionVector(0, 0);
		} else {
			tank.setDirection(direction);
			float y = tank.getMotionVector().y;
			
			if (direction == Direction.LEFT && tank.getX() > 0) {
				tank.setMotionVector(-1, y);
			} else if (direction == Direction.RIGHT && tank.getX() + tank.getWidth() < windowSize.x) {
				tank.setMotionVector(1, tank.getMotionVector().y);	
			} else {
				tank.setMotionVector(0, y);
			}
		}
		
		float newEnergy;
		if (tank.isShielded()) {
			newEnergy = tank.getShieldEnergy() - delta * GameConstants.SHIELD_DEPLETION_RATE;
		} else {
			newEnergy = tank.getShieldEnergy() + delta * GameConstants.SHIELD_RECOVERY_RATE;
		}
		
		newEnergy = (float) Utils.clamp(newEnergy, 0, 1);
		tank.setShieldEnergy(newEnergy);

		if (newEnergy == 0) {
			tank.setShielded(false);
			collection.removeObject(shield);
		} else if (pressedKeys.contains(shieldKey)) {
			if (newEnergy > 0.2 || (tank.getShieldEnergy() > 0 && tank.isShielded())) {
				if (!tank.isShielded()) {
					//SoundEffect.SHIELD.play();
					shield = spawner.spawnShield();
				}
				
				tank.setShielded(true);
				FloatVec center = Geometry.center(tank.getBoundingBox());
				shield.setPosition(Geometry.toTopLeft(shield.getSize(), center));
			}
		} else {
			if (tank.isShielded()) {
				collection.removeObject(shield);
			}
			tank.setShielded(false);
		}
		
		
		if (tank.isPoweredUp()) {
			tank.setPowerupTime(tank.getPowerupTime() - delta * GameConstants.SPECIAL_DEPLETION_RATE);
			
			if (tank.getPowerupTime() < 0) {
				tank.setPowerupTime(0);
				tank.setPoweredUp(false);
			}
		}
		
		
		long time = System.currentTimeMillis();
		
		//Mouse
		if (pressedButtons.contains(bulletButton)) {
			long difference = time - lastBullet;
			if (tank.isPoweredUp() && difference > GameConstants.SPECIAL_BULLET_COOLDOWN) {
				//SoundEffect.SPECIALSHOOT.play();
				spawner.spawnSpecialBullet(tank, mousePosition.toFloat());
				lastBullet = time;
			} else if (!tank.isPoweredUp() && difference > GameConstants.BULLET_COOLDOWN) {
				//SoundEffect.SHOOT.play();
				spawner.spawnBullet(tank, mousePosition.toFloat());
				lastBullet = time;
			}
		}
		
		if (pressedButtons.contains(cannonballButton)) {
			if (canFireCannon() && !cannonCharging) {
				lastCannon = time;
				cannonCharging = true;
			}
		} else {
			if (cannonCharging) {
				//SoundEffect.CANNON.play();
				spawner.spawnCannonball(getCannonCharge() + GameConstants.MIN_CANNONBALL_TIME, tank, mousePosition.toFloat());
				
				lastCannon = time;
			}
			cannonCharging = false;
		}
	}
	
	public boolean isCannonCharging() {
		return cannonCharging;
	}
	
	public float getCannonCharge() {
		float delta = System.currentTimeMillis() - lastCannon;
		delta = Math.min(delta, GameConstants.MAX_CANNONBALL_TIME);
		
		return delta;
	}
	
	public boolean canFireCannon() {
		return getRemainingCannonCooldown() <= 0;
	}

	public boolean isCannonOnCooldown() {
		return !isCannonCharging() && !canFireCannon();
	}
	
	public float getRemainingCannonCooldown() {
		float delta = System.currentTimeMillis() - lastCannon;
		return Math.max(0, GameConstants.CANNON_COOLDOWN - delta);
	}
	
	public Set<MouseButton> getPressedButtons() {
		return pressedButtons;
	}
	
	public Set<Integer> getPressedKeys() {
		return pressedKeys;
	}
	
	public void resetFlags() {
		lastBullet = 0;
		lastCannon = 0;
		cannonCharging = false;
	}
}
