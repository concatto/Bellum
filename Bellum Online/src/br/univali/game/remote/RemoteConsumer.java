package br.univali.game.remote;

public interface RemoteConsumer<T> {
	void accept(T t);
}
