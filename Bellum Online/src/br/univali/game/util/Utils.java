package br.univali.game.util;

public abstract class Utils {
	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		}
		return value;
	}
	
	public static float generateRandom(float min, float max) {
		return (float) (Math.random() * (max - min) + min);
	}
	
	
	public static float generateRandom() {
		return generateRandom(0, 1);
	}
	
	public static float lerp(float x, float x0, float y0, float x1, float y1) {
		return y0 + (x - x0) * ((y1 - y0) / (x1 - x0));
	}
}
