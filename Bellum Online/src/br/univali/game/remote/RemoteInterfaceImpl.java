package br.univali.game.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import br.univali.game.objects.GameObjectCollection;
import br.univali.game.server.GameServer;
import br.univali.game.server.ServerFullException;

public class RemoteInterfaceImpl implements RemoteInterface {
	private GameServer server;
	
	public RemoteInterfaceImpl(GameServer server) {
		this.server = server;
	}

	@Override
	public GameObjectCollection getGameObjectCollection() throws RemoteException {
		return server.getGameObjectCollection();
	}

	@Override
	public GameConnection connectToServer() throws RemoteException, ServerFullException {
		GameConnection conn = server.createConnection();
		return (GameConnection) UnicastRemoteObject.exportObject(conn, 8080);
	}
}
