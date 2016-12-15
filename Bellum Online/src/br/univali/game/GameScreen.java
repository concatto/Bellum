package br.univali.game;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

import br.univali.game.graphics.GameFont;
import br.univali.game.graphics.Texture;
import br.univali.game.graphics.TextureManager;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.remote.GameConnection;
import br.univali.game.util.Direction;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntRect;
import br.univali.game.window.GameWindow;

public class GameScreen extends BaseScreen {
	private GameConnection connection;
	private Texture backgroundTexture;
	private TextureManager manager;
	private GameObjectCollection collection;
	private CountDownLatch latch = new CountDownLatch(1);
	private CombatObject playerObject;
	private boolean running = false;
	private boolean respawnScreenPrepared = false;

	public GameScreen(GameWindow window, GameConnection connection) {
		super(window);
		this.connection = connection;
		this.backgroundTexture = Texture.load("images/background.png");
		this.manager = new TextureManager("regular");
		
		try {
			manager.loadAllTextures();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		running = true;
		while (running) {
			GameObjectCollection collection = this.collection;
			
			try {
				playerObject = collection.getPlayerObject(connection.getIdentifier());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			drawCentralizedTexture(backgroundTexture);
			drawObjects(collection);
			
			if (playerObject.isRespawning()) {
				drawRespawningScreen();
			} else {
				respawnScreenPrepared = false;
			}
			
			renderer.draw();
		}
	}

	private void drawRespawningScreen() {
		if (!respawnScreenPrepared) {
			respawnScreenPrepared = true;
			setOverlayAlpha(0);
			fadeOverlayTo(0.8f, 1000);
			setOverlayColor(0.5f, 0.5f, 0.5f);
		}
		
		drawOverlay();
		
		renderer.setFont(GameFont.GIGANTIC);
		renderer.setColor(1f, 1f, 1f);
		
		double time = playerObject.getRemainingRespawnTime() / 1000.0;
		centralizeXAndDraw(String.format("Respawning in %.2fs", time), 200);
	}

	private void drawObjects(GameObjectCollection collection) {
		for (DrawableObject object : collection.getDrawableObjects()) {
			if (object.getDirection() == Direction.LEFT) {
				renderer.setScale(new FloatVec(-1, 1));
			}
			
			renderer.setRotation(object.getRotation());
			
			if (object == playerObject) {
				renderer.setColor(0.5f, 0.5f, 0.5f, 0.5f);
				float margin = 10;
				renderer.drawRectangle(object.getX() - margin, object.getY() - margin,
										object.getWidth() + margin * 2, object.getHeight() + margin * 2);
			}
			
			Texture tex = manager.getObjectTexture(object.getType());
			IntRect frame = tex.getFrames().get(object.getCurrentFrame());			
			renderer.drawTextureFrame(tex, object.getX(), object.getY(), frame);
			
			renderer.setRotation(0);
			renderer.setScale(new FloatVec(1, 1));
		}
	}

	public void setCollection(GameObjectCollection collection) {
		this.collection = collection;
		latch.countDown();
	}
}
