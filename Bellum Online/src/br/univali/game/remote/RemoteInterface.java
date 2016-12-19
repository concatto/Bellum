package br.univali.game.remote;
import java.rmi.Remote;
import java.rmi.RemoteException;

import br.univali.game.objects.GameObjectCollection;
import br.univali.game.server.ServerFullException;

public interface RemoteInterface extends Remote {
	GameObjectCollection getGameObjectCollection() throws RemoteException;
	GameConnection connectToServer() throws RemoteException, ServerFullException;
}
