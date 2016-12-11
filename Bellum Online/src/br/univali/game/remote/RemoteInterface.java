package br.univali.game.remote;
import java.rmi.Remote;
import java.rmi.RemoteException;

import br.univali.game.objects.GameObjectCollection;

public interface RemoteInterface extends Remote {
	GameObjectCollection getGameObjectCollection() throws RemoteException;
	void startGame() throws RemoteException;
	boolean shouldStart() throws RemoteException;
	
	GameConnection connectToServer() throws RemoteException;
	
	void onStart(Runnable runnable) throws RemoteException;
}
