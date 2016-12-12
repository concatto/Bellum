package br.univali.game.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.util.IntVec;

public interface GameConnection extends Remote {
	void publishKeyboardEvent(KeyboardEvent event) throws RemoteException;
	void publishMouseEvent(MouseEvent event) throws RemoteException;
	void publishMousePosition(IntVec position) throws RemoteException;
	void publishReady(boolean ready) throws RemoteException;
	GameInformation getGameInformation() throws RemoteException;
	void heartbeat() throws RemoteException;
}
