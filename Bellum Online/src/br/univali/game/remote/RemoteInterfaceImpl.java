package br.univali.game.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;

import br.univali.game.objects.GameObjectCollection;

public class RemoteInterfaceImpl implements RemoteInterface {
	private GameObjectCollection collection;
	private boolean startRequested = false;
	private Runnable startAction;
	private Callable<GameConnection> connectionCallable;
	
	public RemoteInterfaceImpl(GameObjectCollection collection, Callable<GameConnection> connectionCallable) {
		this.collection = collection;
		this.connectionCallable = connectionCallable;
	}

	@Override
	public GameObjectCollection getGameObjectCollection() throws RemoteException {
		return collection;
	}

	@Override
	public void startGame() throws RemoteException {
		startRequested = true;
		startAction.run();
	}
	
	@Override
	public boolean shouldStart() {
		return startRequested;
	}

	@Override
	public void onStart(Runnable action) throws RemoteException {
		startAction = action;
	}

	@Override
	public GameConnection connectToServer() throws RemoteException {
		try {
			return (GameConnection) UnicastRemoteObject.exportObject(connectionCallable.call(), 8080);
		} catch (Exception e) {
			throw new RemoteException("Connection callable failed to execute");
		}
	}
}
