package br.univali.game.controllers;

import br.univali.game.GameConstants;
import br.univali.game.Spawner;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.PlayerHelicopter;
import br.univali.game.util.Direction;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntVec;

public class HelicopterController extends PlayerController {

	private static final int upKey = 'W';
	
	private Enemy helicopter;
	
	public HelicopterController(Spawner spawner, GameObjectCollection collection, IntVec windowSize, Enemy helicopter) {
		super(spawner,collection,windowSize);
		this.helicopter = helicopter;
//		helicopter = collection.getHelicopter();
	}
	
	@Override
	public void update(float delta) {
		//Talvez devesse estar mais acima no call stack
		if (!helicopter.isDead()) {
			handleMovement();
			handleWeapons();
		}
	}

	
	@Override
	public Direction computeDirection() {
		boolean up = pressedKeys.contains(upKey);

		switch (super.computeDirection())
		{
		case NONE:
			if ( up )
				return Direction.UP;
			break;
		case LEFT:
			return ( up ? Direction.UP_LEFT : Direction.LEFT );
		case RIGHT:
			return ( up ? Direction.UP_RIGHT : Direction.RIGHT );
		default:
			break;
		}
		return Direction.DOWN;
	}

	private void handleWeapons() {
		long time = System.currentTimeMillis();
		
		if (pressedButtons.contains(bulletButton)) {
			long difference = time - lastBullet;
			if (difference > GameConstants.BULLET_COOLDOWN_HELICOPTER) {
				spawner.spawnBullet(helicopter, mousePosition.toFloat());
				lastBullet = time;
			}
		}
	}
	@Override
	protected void handleMovement() {
		Direction direction = computeDirection();
		
		
		float y = 1;
		if (direction == Direction.UP_LEFT && helicopter.getY() > 0 ){
			y = -1;
			direction = Direction.LEFT;
		} else if (direction == Direction.UP_RIGHT && helicopter.getY() + helicopter.getWidth() < windowSize.x){
			y = -1;
			direction = Direction.RIGHT;
		} else {
			if ( direction != Direction.DOWN )
				y = 0;
		}
		
		y *= 0.01;
		
		helicopter.setDirection(direction);
		
		if (direction == Direction.LEFT && helicopter.getX() > 0) {
			helicopter.setMotionVector(-1, y);
		} else if (direction == Direction.RIGHT && helicopter.getX() + helicopter.getWidth() < windowSize.x) {
			helicopter.setMotionVector(1, y);
		} else {
			helicopter.setMotionVector(0, y);
		}
	}
}
