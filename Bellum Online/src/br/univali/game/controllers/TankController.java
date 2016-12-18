package br.univali.game.controllers;

import java.util.Set;

import br.univali.game.GameConstants;
import br.univali.game.Keyboard;
import br.univali.game.Spawner;
import br.univali.game.event.input.InputEventType;
import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseButton;
import br.univali.game.objects.PlayerTank;
import br.univali.game.util.Direction;
import br.univali.game.util.IntVec;
import br.univali.game.util.Utils;

public class TankController extends PlayerController {	
	private MouseButton cannonballButton = MouseButton.RIGHT;
	private int leftKey = 'A';
	private int rightKey = 'D';
	private int shieldKey = Keyboard.SPACE;
	
	private PlayerTank tank;

	public TankController(Spawner spawner, IntVec windowSize, PlayerTank tank) {
		super(spawner, windowSize);
		this.tank = tank;
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
	public void setMousePosition(IntVec mousePosition) {
		this.mousePosition = mousePosition;
	}
	
	public IntVec getMousePosition() {
		return mousePosition;
	}
	
	public void updateTank(float delta) {
		handleMovement();
		handleWeapons(delta);
		handleShield(delta);
		
		if (tank.isPoweredUp()) {
			tank.setPowerupTime(tank.getPowerupTime() - delta * GameConstants.SPECIAL_DEPLETION_RATE);
			
			if (tank.getPowerupTime() < 0) {
				tank.setPowerupTime(0);
				tank.setPoweredUp(false);
			}
		}
	}

	private void handleWeapons(float delta) {
		long time = System.currentTimeMillis();
		
		//System.out.println(pressedButtons.contains(bulletButton));
		if (pressedButtons.contains(bulletButton)) {
			long difference = time - lastBullet;
			if (tank.isPoweredUp() && difference > GameConstants.SPECIAL_BULLET_COOLDOWN) {
				spawner.spawnSpecialBullet(tank, mousePosition.toFloat());
				lastBullet = time;
			} else if (!tank.isPoweredUp() && difference > GameConstants.BULLET_COOLDOWN) {
				spawner.spawnBullet(tank, mousePosition.toFloat());
				lastBullet = time;
			}
		}
		
		float recovery = tank.getCannonRecovery();
		
		if (pressedButtons.contains(cannonballButton) && recovery == 0) {
			float increment = delta / GameConstants.MAX_CANNONBALL_TIME;
			tank.setCannonCharge(Math.min(1, tank.getCannonCharge() + increment));
		} else {
			if (recovery > 0) {
				tank.setCannonRecovery(Math.max(0, recovery - (delta / GameConstants.CANNON_COOLDOWN)));
			}
			
			float charge = tank.getCannonCharge();
			if (charge > 0) {
				spawner.spawnCannonball(charge, tank, mousePosition.toFloat());
				tank.setCannonCharge(0);
				tank.setCannonRecovery(1);
			}
		}
	}

	private void handleShield(float delta) {
		float newEnergy;
		if (tank.isShielded()) {
			newEnergy = tank.getShieldEnergy() - delta * GameConstants.SHIELD_DEPLETION_RATE;
		} else {
			newEnergy = tank.getShieldEnergy() + delta * GameConstants.SHIELD_RECOVERY_RATE;
		}
		
		newEnergy = (float) Utils.clamp(newEnergy, 0, 1);
		tank.setShieldEnergy(newEnergy);

		if (newEnergy == 0 || !pressedKeys.contains(shieldKey)) {
			tank.setShielded(false);
		} else if (newEnergy > 0.1){
			tank.setShielded(true);
		}
	}

	@Override
	protected void handleMovement() {
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
	}
	
	public Set<MouseButton> getPressedButtons() {
		return pressedButtons;
	}
	
	public Set<Integer> getPressedKeys() {
		return pressedKeys;
	}

	@Override
	public void update(float delta) {
		updateTank(delta);		
	}
}
