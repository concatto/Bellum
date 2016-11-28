package br.univali.game;

import java.rmi.RemoteException;

public class TestRemoteImpl implements TestRemote {

	@Override
	public void test() throws RemoteException {
		System.out.println("hi");
	}

}
