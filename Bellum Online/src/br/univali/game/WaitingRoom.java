package br.univali.game;

import java.rmi.ConnectException;
import java.util.List;
import java.util.function.Consumer;

import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.remote.GameConnection;
import br.univali.game.remote.Player;
import br.univali.game.util.IntVec;
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

	public void display() throws ConnectException {
		boolean running = true;
		
		while (running) {
			renderer.clear();
			drawCentralizedTexture(background);
			drawOverlay(0.7f);
			
			renderer.setFont(Renderer.LARGE_FONT);
			renderer.setColor(0.2f, 0.8f, 0.2f);
			centralizeXAndDraw("Waiting Room",50);
			
			renderer.setFont(Renderer.MEDIUM_FONT);
			if ( ready ){
				renderer.setColor(0.2f, 0.2f, 0.7f);
			} else {
				renderer.setColor(0.7f, 0.2f, 0.2f);
			}
			centralizeXAndDraw( ready ? "Waiting for the others players":"Press ENTER when ready", 110 );
			
			boolean enter = window.isKeyPressed(Keyboard.ENTER);
			if (enter && !previousEnter) {
				ready = !ready;
				readyConsumer.accept(ready);
			}
			
			renderer.setFont(Renderer.MEDIUM_FONT);
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
					
					String username = (i + 1) + ". " + p.getName();
					renderer.drawText(username, 50, 200 + (i * 50));
					if (self){
						IntVec tSize = renderer.computeTextSize(username);
						renderer.drawRectangle(
							50, 200 + (i*50) + tSize.y,
							tSize.x, 0.5f
						);
					}
				}
			} catch (ConnectException ce) {
				throw ce;
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
