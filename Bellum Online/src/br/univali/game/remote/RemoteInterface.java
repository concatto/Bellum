package br.univali.game.remote;
import java.rmi.Remote;
import java.rmi.RemoteException;

import br.univali.game.objects.GameObjectCollection;

public interface RemoteInterface extends Remote {
	GameObjectCollection getGameObjectCollection() throws RemoteException;
	GameConnection connectToServer(String identifier) throws RemoteException;
}
