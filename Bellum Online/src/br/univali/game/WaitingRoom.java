package br.univali.game;

import java.util.List;
import java.util.function.Consumer;

import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.remote.GameConnection;
import br.univali.game.remote.Player;
import br.univali.game.window.GameWindow;

public class WaitingRoom extends GameScreen {
	private Texture background;
	private Consumer<Boolean> readyConsumer;
	private boolean previousEnter = false;
	private boolean ready = false;
	private GameConnection connection;
	
	public WaitingRoom(GameWindow window, GameConnection connection) {
		super(window);
		
		this.connection = connection;
		this.background = Texture.load("images/waiting_background.jpg");
	}

	public void display() {
		boolean running = true;
		
		renderer.setFont(Renderer.MEDIUM_FONT);
		
		while (running) {
			renderer.clear();
			drawCentralizedTexture(background);
			drawOverlay(0.7f);
			
			boolean enter = window.isKeyPressed(Keyboard.ENTER);
			if (enter && !previousEnter) {
				ready = !ready;
				readyConsumer.accept(ready);
			}
			
			try {
				List<Player> players = connection.getGameInformation().getPlayers();
				
				for (int i = 0; i < players.size(); i++) {
					Player p = players.get(i);
					boolean self = p.getName().equals(connection.getIdentifier());
					
					float c = self ? 0.6f : 0.2f;
					
					if (p.isReady()) {
						renderer.setColor(c, 0.95f, c);
					} else {
						renderer.setColor(0.95f, c, c);
					}
					
					renderer.drawText((i + 1) + ". " + p.getName(), 50, 200 + (i * 50));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			previousEnter = enter;
			
			renderer.draw();
		}
	}

	public void onReady(Consumer<Boolean> readyConsumer) {
		this.readyConsumer = readyConsumer;
	}

}
