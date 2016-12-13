package br.univali.game;

import java.rmi.ConnectException;
import java.util.List;
import java.util.function.Consumer;

import br.univali.game.graphics.GameFont;
import br.univali.game.graphics.Texture;
import br.univali.game.remote.GameConnection;
import br.univali.game.remote.Player;
import br.univali.game.util.Countdown;
import br.univali.game.util.IntVec;
import br.univali.game.window.GameWindow;

public class WaitingRoom extends GameScreen {
	private Texture background;
	private Consumer<Boolean> readyConsumer;
	private boolean previousEnter = true;
	private boolean ready = false;
	private GameConnection connection;
	private boolean allReady = false;
	private Countdown countdown = new Countdown(5000);
	
	public WaitingRoom(GameWindow window, GameConnection connection) {
		super(window);
		
		this.connection = connection;
		this.background = Texture.load("images/waiting_background.jpg");
	}

	public void display() throws ConnectException {
		boolean running = true;
		setOverlayAlpha(0.6f);
		
		while (running) {
			if (countdown.running() && countdown.finished()) {
				running = false;
				break;
			}
			
			renderer.setColor(0, 0, 0);
			renderer.clear();
			drawCentralizedTexture(background);
			drawOverlay();
			
			drawHeader();
			drawPlayers();
			
			renderer.draw();
			
			boolean enter = window.isKeyPressed(Keyboard.ENTER);
			if (enter && !previousEnter && !allReady) {
				ready = !ready;
				readyConsumer.accept(ready);
			}
			
			previousEnter = enter;
		}
	}

	private void drawHeader() {
		renderer.setFont(GameFont.LARGE);
		renderer.setColor(1, 1, 1);
		centralizeXAndDraw("Waiting Room", 50);
		
		renderer.setFont(GameFont.MEDIUM);
		if (allReady) {
			renderer.setColor(1, 1, 1);
			
			double remaining = countdown.remainingSeconds();
			centralizeXAndDraw(String.format("Starting game in %.2fs", remaining), 120);
		} else {
			if (ready) {
				renderer.setColor(0.9f, 0.9f, 1f);
			} else {
				renderer.setColor(1f, 0.9f, 0.9f);
			}
			centralizeXAndDraw(ready ? "Waiting for others players" : "Press ENTER when ready", 110);
		}
	}

	private void drawPlayers() throws ConnectException {
		boolean readyFlag = true;
		
		renderer.setFont(GameFont.MEDIUM);
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
					readyFlag = false;
				}
				
				String username = (i + 1) + ". " + p.getName();
				renderer.drawText(username, 50, 200 + (i * 50));
				if (self) {
					IntVec tSize = renderer.computeTextSize(username);
					renderer.drawRectangle(50, 200 + (i * 50) + tSize.y, tSize.x, 0.5f);
				}
			}
		} catch (ConnectException ce) {
			throw ce;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (readyFlag) {
			if (!allReady) {
				countdown.start();
				fadeOverlayTo(0.9f, 1000);
			}
			
			allReady = true;
		}
	}

	public void onReady(Consumer<Boolean> readyConsumer) {
		this.readyConsumer = readyConsumer;
	}

}
