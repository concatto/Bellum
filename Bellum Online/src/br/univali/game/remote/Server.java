package br.univali.game.remote;

import java.rmi.RemoteException;

import br.univali.game.Game;
import br.univali.game.GameObjectCollection;

public class Server implements RemoteInterface {
	private GameObjectCollection collection;
	private Game game;
	private boolean startRequested = false;
	
	public Server(GameObjectCollection collection, Game game) {
		this.collection = collection;
		this.game = game;
	}

	@Override
	public GameObjectCollection getGameObjectCollection() throws RemoteException {
		return collection;
	}

	@Override
	public void startGame() throws RemoteException {
		startRequested = true;
	}
	
	@Override
	public boolean shouldStart() {
		return startRequested;
	}
}
