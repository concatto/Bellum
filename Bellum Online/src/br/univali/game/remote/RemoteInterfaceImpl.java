package br.univali.game.remote;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.objects.GameObjectCollection;

public class RemoteInterfaceImpl implements RemoteInterface {
	private GameObjectCollection collection;
	private boolean startRequested = false;
	private Runnable startAction;
	private Consumer<KeyboardEvent> keyboardConsumer;
	private Consumer<MouseEvent> mouseConsumer;
	private Callable<Integer> connectionCallable;
	
	public RemoteInterfaceImpl(GameObjectCollection collection, Callable<Integer> connectionCallable) {
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
	public void onKeyboardEvent(Consumer<KeyboardEvent> consumer) throws RemoteException {
		keyboardConsumer = consumer;
	}

	@Override
	public void onMouseEvent(Consumer<MouseEvent> consumer) throws RemoteException {
		mouseConsumer = consumer;
	}

	@Override
	public void publishKeyboardEvent(KeyboardEvent event) throws RemoteException {
		if (keyboardConsumer != null) {
			keyboardConsumer.accept(event);
		}
	}

	@Override
	public void publishMouseEvent(MouseEvent event) throws RemoteException {
		if (mouseConsumer != null) {
			mouseConsumer.accept(event);
		}
	}

	@Override
	public int connectToServer() throws RemoteException {
		try {
			return connectionCallable.call();
		} catch (Exception e) {
			throw new RemoteException("Connection callable failed to execute");
		}
	}
}
