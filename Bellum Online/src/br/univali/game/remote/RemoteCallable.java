package br.univali.game.remote;

import java.io.Serializable;

public interface RemoteCallable<T> extends Serializable {
	T execute();
}
