package br.univali.game.remote;
import java.rmi.Remote;
import java.rmi.RemoteException;

import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.objects.GameObjectCollection;

public interface RemoteInterface extends Remote {
	GameObjectCollection getGameObjectCollection() throws RemoteException;
	void startGame() throws RemoteException;
	boolean shouldStart() throws RemoteException;
	
	int connectToServer() throws RemoteException;
	
	void onStart(RemoteCallback callback) throws RemoteException;
	void onKeyboardEvent(RemoteConsumer<KeyboardEvent> consumer) throws RemoteException;
	void onMouseEvent(RemoteConsumer<MouseEvent> consumer) throws RemoteException;
	
	void publishKeyboardEvent(KeyboardEvent event) throws RemoteException;
	void publishMouseEvent(MouseEvent event) throws RemoteException;
}
