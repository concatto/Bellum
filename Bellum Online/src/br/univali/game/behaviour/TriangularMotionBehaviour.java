package br.univali.game.behaviour;

import br.univali.game.util.FloatVec;

public class TriangularMotionBehaviour implements MotionBehaviour {
	private MotionBehaviour behaviour;
	private float amplitude;
	private float frequency;
	private float speed;
	private float r = 0;
	private float y = 0;

	public TriangularMotionBehaviour(MotionBehaviour behaviour, float amplitude, float frequency, float speed) {
		this.behaviour = behaviour;
		this.amplitude = amplitude / (frequency * speed * 0.1f);
		this.frequency = frequency;
		this.speed = speed;
	}
	
	@Override
	public FloatVec computeNextVector(float delta) {
		r += delta * speed;
		if (r > frequency) {
			r -= frequency;
		}
		
		if (r < (frequency * 0.25) || r > (frequency * 0.75)) {
			y = -frequency;
		} else {
			y = frequency;
		}
		
		FloatVec vec = behaviour.computeNextVector(delta);
		return new FloatVec(vec.x, vec.y + (y * (1 / frequency) * amplitude));
	}

}
