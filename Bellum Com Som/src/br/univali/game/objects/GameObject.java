package br.univali.game.objects;

import br.univali.game.util.Direction;
import br.univali.game.util.FloatRect;
import br.univali.game.util.FloatVec;

public class GameObject {
	private FloatVec motionVector = new FloatVec(0, 0);
	private FloatRect boundingBox = new FloatRect();
	private FloatRect lastBoundingBox = new FloatRect();
	private boolean affectedByGravity = false;
	private float speed = 0;
	private Direction direction;
	private ObjectType type;

	public GameObject() {
		
	}
	
	public ObjectType getType() {
		return type;
	}

	public void setType(ObjectType type) {
		this.type = type;
	}
	
	public void setAffectedByGravity(boolean affectedByGravity) {
		this.affectedByGravity = affectedByGravity;
	}
	
	public void setPosition(float x, float y) {
		setX(x);
		setY(y);
	}
	
	public void setPosition(FloatVec p) {
		setPosition(p.x, p.y);
	}
	
	public void setX(float x) {
		lastBoundingBox.x = boundingBox.x;
		boundingBox.x = x;
	}
	
	public float getX() {
		return boundingBox.x;
	}
	
	public void setY(float y) {
		lastBoundingBox.y = boundingBox.y;
		boundingBox.y = y;
	}
	
	public float getY() {
		return boundingBox.y;
	}
	
	public float getWidth() {
		return boundingBox.width;
	}
	
	public float getHeight() {
		return boundingBox.height;
	}
	
	public FloatVec getMotionVector() {
		return motionVector;
	}

	public void setMotionVector(FloatVec motionVector) {
		this.motionVector = motionVector;
	}
	
	public void setMotionVector(float x, float y) {
		setMotionVector(new FloatVec(x, y));
	}
	
	public FloatRect getBoundingBox() {
		return boundingBox;
	}
	
	public FloatRect getLastBoundingBox() {
		return lastBoundingBox;
	}
	
	public void setSize(float width, float height) {
		lastBoundingBox.width = boundingBox.width;
		lastBoundingBox.height = boundingBox.height;
		
		boundingBox.width = width;
		boundingBox.height = height;
	}
	
	public boolean isAffectedByGravity() {
		return affectedByGravity;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public FloatVec getPosition() {
		return new FloatVec(boundingBox.x, boundingBox.y);
	}
	
	public FloatVec getSize() {
		return new FloatVec(boundingBox.width, boundingBox.height);
	}
}
