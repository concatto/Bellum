package br.univali.game.behaviour;

import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;

public class LinearMotionBehaviour implements MotionBehaviour {
	private FloatVec start;
	private FloatVec end;
	private boolean cycle;
	private float speed;
	private float theta;
	private FloatVec motion;
	private double totalDistance;
	private double currentDistance = 0;
	
	public LinearMotionBehaviour(FloatVec start, FloatVec end, float speed, boolean cycle) {
		this.start = start;
		this.end = end;
		this.speed = speed;
		this.cycle = cycle;
		this.totalDistance = Geometry.euclideanDistance(start, end);
		
		calculateMotion();
	}
	
	private void calculateMotion() {
		this.theta = Geometry.angle(start, end);
		this.motion = new FloatVec((float) Math.cos(theta), (float) Math.sin(theta));
	}

	public LinearMotionBehaviour(FloatVec start, FloatVec end, float speed) {
		this(start, end, speed, false);
	}

	@Override
	public FloatVec computeNextVector(float delta) {
		double step = Geometry.norm(motion.multiply(speed * delta));
		currentDistance += step;
		
		if (currentDistance < totalDistance) {
			return new FloatVec(motion.x, motion.y);
		} else {
			if (cycle) {
				FloatVec t = new FloatVec(start);
				start = new FloatVec(end);
				end = t;
				
				currentDistance = 0;
				calculateMotion();
			}
			
			return new FloatVec(0, 0);
		}
	}

	public FloatVec getStart() {
		return start;
	}
	
	public FloatVec getEnd() {
		return end;
	}
	
	public boolean isCycle() {
		return cycle;
	}
	
	public float getSpeed() {
		return speed;
	}
}
