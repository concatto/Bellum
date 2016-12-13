package br.univali.game.remote;

import java.io.Serializable;

import br.univali.game.PlayerRole;
import br.univali.game.objects.CombatObject;

public class Player implements Serializable {
	private String name;
	private PlayerRole role;
	private boolean ready;
	private CombatObject object;
	
	public Player(String name, PlayerRole role, boolean ready, CombatObject object) {
		this.name = name;
		this.role = role;
		this.ready = ready;
		this.object = object;
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
	
	public CombatObject getObject() {
		return object;
	}
}
