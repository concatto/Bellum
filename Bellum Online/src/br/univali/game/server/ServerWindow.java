package br.univali.game.server;

public interface ServerWindow {
	
	public void setOnClose(Runnable action);
	
	public void publishMessage(String message);
}
