package br.univali.game.remote;

import java.io.Serializable;
import java.util.List;

public class GameInformation implements Serializable {
	private List<Player> players;

	public GameInformation(List<Player> players) {
		this.players = players;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
}
