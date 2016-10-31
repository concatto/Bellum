package br.univali.game.objects;

public class DrawableObject extends GameObject {
	private int currentFrame = 0;
	private float rotation = 0;
	private float frameTime = 0;
	private float frameDuration = 0;
	private boolean animationRepeated = false;

	public DrawableObject() {
		
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public float getFrameTime() {
		return frameTime;
	}
	
	public void setFrameTime(float frameTime) {
		this.frameTime = frameTime;
	}
	
	public float getFrameDuration() {
		return frameDuration;
	}
	
	public void setFrameDuration(int frameDuration) {
		this.frameDuration = frameDuration;
	}

	public void advanceFrame() {
		setCurrentFrame(getCurrentFrame() + 1);
	}

	public boolean isAnimationRepeated() {
		return animationRepeated;
	}

	public void setAnimationRepeated(boolean animationRepeated) {
		this.animationRepeated = animationRepeated;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}
}
