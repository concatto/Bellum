package br.univali.game.remote;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.util.IntVec;

public class GameConnectionImpl implements GameConnection {
	private RemoteConsumer<KeyboardEvent> keyboardConsumer;
	private RemoteConsumer<MouseEvent> mouseConsumer;
	private RemoteConsumer<IntVec> positionConsumer;
	private RemoteConsumer<Boolean> readyConsumer;
	private long lastHeartbeat = Long.MAX_VALUE;
	
	private Callable<GameInformation> gameInformationCallable;
	private String identifier;
	
	public GameConnectionImpl(String identifier) {
		this.identifier = identifier;
	}
	
	public void setKeyboardConsumer(RemoteConsumer<KeyboardEvent> keyboardConsumer) {
		this.keyboardConsumer = keyboardConsumer;
	}
	
	public void setMouseConsumer(RemoteConsumer<MouseEvent> mouseConsumer) {
		this.mouseConsumer = mouseConsumer;
	}
	
	public void setPositionConsumer(RemoteConsumer<IntVec> positionConsumer) {
		this.positionConsumer = positionConsumer;
	}
	
	public void setReadyConsumer(RemoteConsumer<Boolean> readyConsumer) {
		this.readyConsumer = readyConsumer;
	}

	public void setGameInformationCallable(Callable<GameInformation> callable) {
		gameInformationCallable = callable;
	}
	
	public long getLastHeartbeat() {
		return lastHeartbeat;
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
	public void publishMousePosition(IntVec position) throws RemoteException {
		if (positionConsumer != null) {
			positionConsumer.accept(position);
		}
	}
	
	@Override
	public void publishReady(boolean ready) throws RemoteException {
		if (readyConsumer != null) {
			readyConsumer.accept(ready);
		}
	}

	@Override
	public GameInformation getGameInformation() throws RemoteException {
		try {
			return gameInformationCallable.call();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String getIdentifier() throws RemoteException {
		return identifier;
	}

	@Override
	public void heartbeat() throws RemoteException {
		lastHeartbeat = System.currentTimeMillis();
	}
}
