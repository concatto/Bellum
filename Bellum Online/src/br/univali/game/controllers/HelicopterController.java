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

	private static final int upKey   = 'W';
	private static final int downKey = 'S';
	
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
		boolean up   = pressedKeys.contains(upKey);
		boolean down = pressedKeys.contains(downKey);

		Direction computed = super.computeDirection();
		if ( up && down ){
			return computed;
		}
		
		switch ( computed )
		{
			case NONE:
				if ( up ) 	return Direction.UP;
				if ( down ) return Direction.DOWN;
			case LEFT:
				if ( up )   return Direction.UP_LEFT;
				if ( down ) return Direction.DOWN_LEFT;
			case RIGHT:
				if ( up )   return Direction.UP_RIGHT;
				if ( down ) return Direction.DOWN_RIGHT;
		}
		return computed;
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
		
		
		float y = 0.1f;
		if (direction == Direction.UP_LEFT ){
			y = -1f;
			direction = Direction.LEFT;
		} else if (direction == Direction.UP_RIGHT ){
			y = -1f;
			direction = Direction.RIGHT;
		} else if ( direction == Direction.UP ){
			y = -1f;
			direction = Direction.NONE;
		} else if ( direction == Direction.DOWN ){
			y = 1f;
			direction = Direction.NONE;
		} else if ( direction == Direction.DOWN_LEFT ){
			y = 1f;
			direction = Direction.LEFT;
		} else if ( direction == Direction.DOWN_RIGHT ){
			y = 1f;
			direction = Direction.RIGHT;
		}
		if ( helicopter.getY() <= 0 && y < 0 ){
			y = 0f;
		}
		
		
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
