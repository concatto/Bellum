package br.univali.game.server;

import br.univali.game.controllers.PlayerController;
import br.univali.game.remote.GameConnectionImpl;

public class Client {
	private PlayerController controller;
	private GameConnectionImpl connection;

	public Client(GameConnectionImpl connection) {
		this.connection = connection;
	}
	
	public void setController(PlayerController controller) {
		this.controller = controller;
		installHandlers();
	}
	
	private void installHandlers() {
		connection.setKeyboardConsumer(controller::handleKey);
		connection.setMouseConsumer(controller::handleMouse);
		connection.setPositionConsumer(controller::setMousePosition);
	}

	public PlayerController getController() {
		return controller;
	}
	
	public GameConnectionImpl getConnection() {
		return connection;
	}
}
