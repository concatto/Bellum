package br.univali.game.remote;

import java.io.Serializable;

public interface RemoteConsumer<T> extends Serializable {
	void accept(T t);
}
