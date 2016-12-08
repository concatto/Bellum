package br.univali.game.remote;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.function.Consumer;

import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.objects.GameObjectCollection;

public interface RemoteInterface extends Remote {
	GameObjectCollection getGameObjectCollection() throws RemoteException;
	void startGame() throws RemoteException;
	boolean shouldStart() throws RemoteException;
	
	int connectToServer() throws RemoteException;
	
	void onStart(Runnable runnable) throws RemoteException;
	void onKeyboardEvent(Consumer<KeyboardEvent> consumer) throws RemoteException;
	void onMouseEvent(Consumer<MouseEvent> consumer) throws RemoteException;
	
	void publishKeyboardEvent(KeyboardEvent event) throws RemoteException;
	void publishMouseEvent(MouseEvent event) throws RemoteException;
}
