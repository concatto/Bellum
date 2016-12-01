package br.univali.game;

import br.univali.game.controllers.PlayerController;

public class Client {
	private int id;
	private PlayerController controller;
	
	public Client(int id) {
		this.id = id;
	}
	
	public void setController(PlayerController controller) {
		this.controller = controller;
	}
	
	public PlayerController getController() {
		return controller;
	}
}
