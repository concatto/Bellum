package br.univali.game.server;

import java.rmi.RemoteException;

import br.univali.game.PlayerRole;
import br.univali.game.controllers.PlayerController;
import br.univali.game.remote.GameConnectionImpl;
import br.univali.game.remote.Player;

public class Client {
	private PlayerController controller;
	private GameConnectionImpl connection;
	private boolean ready = false;

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
	
	public String getIdentifier() {
		try {
			return connection.getIdentifier();
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PlayerRole getRole() {
		try {
			return connection.getRole();
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public Player convertToPlayer() {
		return new Player(getIdentifier(), getRole(), isReady());
	}
}
