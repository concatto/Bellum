package br.univali.game.remote;

import java.io.Serializable;

import br.univali.game.PlayerRole;

public class Player implements Serializable {
	private String name;
	private PlayerRole role;
	private boolean ready;
	
	public Player(String name, PlayerRole role, boolean ready) {
		this.name = name;
		this.role = role;
		this.ready = ready;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public String getName() {
		return name;
	}
	
	public PlayerRole getRole() {
		return role;
	}
}
