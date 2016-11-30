 package br.univali.game.remote;

import java.io.Serializable;

public interface RemoteCallback extends Serializable {
	void execute();
}
