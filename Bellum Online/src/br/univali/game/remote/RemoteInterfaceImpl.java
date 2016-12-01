package br.univali.game.remote;

import java.rmi.RemoteException;

import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.objects.GameObjectCollection;

public class RemoteInterfaceImpl implements RemoteInterface {
	private GameObjectCollection collection;
	private boolean startRequested = false;
	private RemoteCallback startCallback;
	private RemoteConsumer<KeyboardEvent> keyboardConsumer;
	private RemoteConsumer<MouseEvent> mouseConsumer;
	private RemoteCallable<Integer> connectionCallback;
	
	public RemoteInterfaceImpl(GameObjectCollection collection, RemoteCallable<Integer> connectionCallback) {
		this.collection = collection;
		this.connectionCallback = connectionCallback;
	}

	@Override
	public GameObjectCollection getGameObjectCollection() throws RemoteException {
		return collection;
	}

	@Override
	public void startGame() throws RemoteException {
		startRequested = true;
		startCallback.execute();
	}
	
	@Override
	public boolean shouldStart() {
		return startRequested;
	}

	@Override
	public void onStart(RemoteCallback callback) throws RemoteException {
		startCallback = callback;
	}

	@Override
	public void onKeyboardEvent(RemoteConsumer<KeyboardEvent> consumer) throws RemoteException {
		keyboardConsumer = consumer;
	}

	@Override
	public void onMouseEvent(RemoteConsumer<MouseEvent> consumer) throws RemoteException {
		mouseConsumer = consumer;
	}

	@Override
	public void publishKeyboardEvent(KeyboardEvent event) throws RemoteException {
		if (keyboardConsumer != null) {
			keyboardConsumer.execute(event);
		}
	}

	@Override
	public void publishMouseEvent(MouseEvent event) throws RemoteException {
		if (mouseConsumer != null) {
			mouseConsumer.execute(event);
		}
	}

	@Override
	public int connectToServer() throws RemoteException {
		return connectionCallback.execute();
	}
}
