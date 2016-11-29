package br.univali.game.remote;

import java.rmi.RemoteException;

import br.univali.game.GameServer;
import br.univali.game.objects.GameObjectCollection;

public class Server implements RemoteInterface {
	private GameObjectCollection collection;
	private GameServer game;
	private boolean startRequested = false;
	
	public Server(GameObjectCollection collection, GameServer game) {
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
