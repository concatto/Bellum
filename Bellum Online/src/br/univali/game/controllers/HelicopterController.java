package br.univali.game.controllers;

import br.univali.game.GameConstants;
import br.univali.game.Spawner;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.PlayerHelicopter;
import br.univali.game.util.Direction;
import br.univali.game.util.IntVec;

public class HelicopterController extends PlayerController {

	private int upKey = 'W';
	
	private PlayerHelicopter helicopter;
	
	public HelicopterController(Spawner spawner, GameObjectCollection collection, IntVec windowSize) {
		super(spawner,collection,windowSize);
//		helicopter = collection.getHelicopter();
	}
	
	@Override
	public void update(float delta) {
		handleMovement();
		handleWeapons();	
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

	private void handleMovement() {
		Direction direction = computeDirection();
		
		if (direction == Direction.NONE) {
			helicopter.setMotionVector(0, 1);
		} else {
			helicopter.setDirection(direction);
			float y = helicopter.getMotionVector().y;
			
			if (direction == Direction.LEFT && helicopter.getX() > 0) {
				helicopter.setMotionVector(-1, y);
			} else if (direction == Direction.RIGHT && helicopter.getX() + helicopter.getWidth() < windowSize.x) {
				helicopter.setMotionVector(1, helicopter.getMotionVector().y);	
			} else {
				helicopter.setMotionVector(0, y);
			}
		}
	}
}
