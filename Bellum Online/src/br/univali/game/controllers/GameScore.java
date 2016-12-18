package br.univali.game.controllers;

import java.io.Serializable;

public class GameScore implements Serializable {
	private long tankScore = 0;
	private long helicoptersScore = 0;
	
	public GameScore() {
		
	}
	
	public void incrementTankScore(long value) {
		tankScore += value;
	}
	
	public void incrementHelicoptersScore(long value) {
		helicoptersScore += value;
	}
	
	public void reset() {
		tankScore = 0;
		helicoptersScore = 0;
	}
	
	public long getTankScore() {
		return tankScore;
	}
	
	public long getHelicoptersScore() {
		return helicoptersScore;
	}
}
