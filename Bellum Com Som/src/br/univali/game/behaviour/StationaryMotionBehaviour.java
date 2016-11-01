package br.univali.game.behaviour;

import br.univali.game.util.FloatVec;

public class StationaryMotionBehaviour implements MotionBehaviour {

	@Override
	public FloatVec computeNextVector(float delta) {
		return new FloatVec(0, 0);
	}

}
