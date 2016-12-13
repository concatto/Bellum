package br.univali.game.util;

public class Countdown {
	private long duration;
	private long startTime;
	
	public Countdown(long duration) {
		this.duration = duration;
	}

	public static Countdown createAndStart(long duration) {
		Countdown c = new Countdown(duration);
		c.start();
		return c;
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public long elapsed() {
		return System.currentTimeMillis() - startTime;
	}
	
	public double elapsedSeconds() {
		return elapsed() / 1000.0;
	}
	
	public boolean finished() {
		return elapsed() > duration;
	}
	
	public long remaining() {
		return Math.max(0, duration - elapsed());
	}
	
	public double remainingSeconds() {
		return remaining() / 1000.0;
	}
	
	public boolean running() {
		return startTime != 0;
	}
}
