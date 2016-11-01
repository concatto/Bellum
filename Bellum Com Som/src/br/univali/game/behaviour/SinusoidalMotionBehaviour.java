package br.univali.game.behaviour;

import br.univali.game.util.FloatVec;

public class SinusoidalMotionBehaviour implements MotionBehaviour {
	private double amplitude;
	private double r = 0;
	private double frequency;
	private MotionBehaviour behaviour;
	private float speed;
	
	public SinusoidalMotionBehaviour(MotionBehaviour behaviour, float amplitude, float frequency, float speed) {
		this.behaviour = behaviour;
		this.amplitude = amplitude / (frequency * speed * 0.1);
		this.frequency = frequency;
		this.speed = speed;
	}

	@Override
	public FloatVec computeNextVector(float delta) {
		r += delta * speed;
		
		if (r > frequency) {
			r -= frequency;
		}
		
		double y = Math.sin((Math.PI * 2 * r) / frequency) * amplitude;
		
		FloatVec motion = behaviour.computeNextVector(delta);
		
		return new FloatVec(motion.x, motion.y + (float) y);
	}

}
