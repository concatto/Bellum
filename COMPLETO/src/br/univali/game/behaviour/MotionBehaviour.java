package br.univali.game.behaviour;

import br.univali.game.util.FloatVec;

public interface MotionBehaviour {
	public static final double MIN_DELTA = 1E-6;
	
	FloatVec computeNextVector(float delta);
}
