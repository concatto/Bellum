package br.univali.game.behaviour;

import java.io.Serializable;

import br.univali.game.util.FloatVec;

public interface MotionBehaviour extends Serializable {
	public static final double MIN_DELTA = 1E-6;
	
	FloatVec computeNextVector(float delta);
}
