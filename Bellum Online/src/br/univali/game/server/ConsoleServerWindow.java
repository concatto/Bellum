package br.univali.game.server;

public class ConsoleServerWindow implements ServerWindow {

	@Override
	public void setOnClose(Runnable action) {
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				super.run();
				action.run();
			}
		});
	}

	@Override
	public void publishMessage(String message) {
		System.out.println(message);
	}

}
