package br.univali.game.remote;

import java.io.Serializable;

public class Player implements Serializable {
	private String name;
	private boolean ready;
	
	public Player(String name, boolean ready) {
		this.name = name;
		this.ready = ready;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public String getName() {
		return name;
	}
}
