package br.univali.game.remote;

import java.io.Serializable;
import java.rmi.RemoteException;

import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.util.IntVec;

public class GameConnectionImpl implements GameConnection, Serializable {
	private RemoteConsumer<KeyboardEvent> keyboardConsumer;
	private RemoteConsumer<MouseEvent> mouseConsumer;
	private RemoteConsumer<IntVec> positionConsumer;
	
	public GameConnectionImpl() {
		
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
	
	@Override
	public void publishKeyboardEvent(KeyboardEvent event) throws RemoteException {
		keyboardConsumer.accept(event);
	}

	@Override
	public void publishMouseEvent(MouseEvent event) throws RemoteException {
		mouseConsumer.accept(event);
	}

	@Override
	public void publishMousePosition(IntVec position) throws RemoteException {
		positionConsumer.accept(position);
	}

}
