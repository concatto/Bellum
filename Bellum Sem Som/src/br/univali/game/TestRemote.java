package br.univali.game;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TestRemote extends Remote {
	public void test() throws RemoteException;
}
