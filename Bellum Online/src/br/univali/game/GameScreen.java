package br.univali.game;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

import br.univali.game.controllers.BaseHUD;
import br.univali.game.controllers.GameScore;
import br.univali.game.controllers.HUDScoreBehaviour;
import br.univali.game.controllers.HelicopterHUD;
import br.univali.game.controllers.TankHUD;
import br.univali.game.graphics.GameFont;
import br.univali.game.graphics.Texture;
import br.univali.game.graphics.TextureManager;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.ObjectType;
import br.univali.game.remote.GameConnection;
import br.univali.game.util.Countdown;
import br.univali.game.util.Direction;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntRect;
import br.univali.game.window.GameWindow;

public class GameScreen extends BaseScreen {
	private GameConnection connection;
	private Texture backgroundTexture;
	private TextureManager manager;
	private GameObjectCollection collection;
	private CountDownLatch latch;
	private CombatObject playerObject;
	private BaseHUD hud;
	private boolean running = false;
	private boolean respawnScreenPrepared = false;
	private Countdown endingCountdown;
	private GameScore score;
	private PlayerRole role;
//	private LogicController logic;
//	private PhysicsController physics;
//	private AnimationController animation;
//	private Spawner spawner;

	public GameScreen(GameWindow window, GameConnection connection) {
		super(window);
		this.connection = connection;
		this.backgroundTexture = Texture.load("images/background.png");
		this.manager = new TextureManager();
		
		try {
			manager.loadAllTextures();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		spawner = new Spawner(manager, collection);
//		logic = new LogicController(collection, spawner, window.getSize());
//		physics = new PhysicsController(collection, logic.getGroundLevel());
//		animation = new AnimationController(collection, manager);
	}

	public void start() {
		try {
			latch = new CountDownLatch(1);
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			role = connection.getRole();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		score = null;
		hud = createHUD();
		running = true;
		endingCountdown = new Countdown(10000);
		long lastFrame = System.nanoTime();
		
		while (running) {
			long time = System.nanoTime();
			float delta = (float) ((time - lastFrame) / 1E6);
			
			GameObjectCollection collection = this.collection;
			
			try {
				playerObject = collection.getPlayerObject(connection.getIdentifier());
				score = connection.getGameScore();
				
				if (role == PlayerRole.HELICOPTER) {
					playerObject.setType(ObjectType.PLAYER_HELICOPTER);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			hud.setCollection(collection);
			hud.setPlayerObject(playerObject);
			hud.setGameScore(score);
			
//			logic.update(delta);
//			physics.updatePositions(delta);
//			
//			logic.handleEnemyCollisions(physics.checkEnemyCollisions());
//			logic.handleGroundCollisions(physics.checkGroundCollisions());
//			logic.handlePlayerCollisions(physics.checkPlayerCollisions());
//			logic.handlePickupCollisions(physics.checkPickupCollisions());
//			
//			animation.updateAnimations(delta);
			
			drawCentralizedTexture(backgroundTexture);
			drawObjects(collection);
			hud.draw();
			
			if (playerObject.isRespawning()) {
				drawRespawningScreen();
			} else {
				respawnScreenPrepared = false;
			}
			
			if (collection.getTank().isDead()) {
				drawEndingScreen();
			}
			
			renderer.draw();
			lastFrame = time;
		}
	}

	private void drawEndingScreen() {
		float targetAlpha = 0.6f;
		
		if (!endingCountdown.running()) {
			endingCountdown.start();
			setOverlayAlpha(0);
			setOverlayColor(0, 0, 0);
			fadeOverlayTo(targetAlpha, 1500);
		}
		
		drawOverlay();
		
		float textAlpha = getOverlayAlpha() * (1 / targetAlpha);
		
		boolean tankWon = score.getTankScore() > score.getHelicoptersScore();
		String text = tankWon ? "The Tank won the game!" : "The Helicopters won the game!";
		
		if (score.getHelicoptersScore() == score.getTankScore()) {
			renderer.setColor(1, 1, 1, textAlpha);
			text = "Draw! No one wins!";
		} else if (tankWon && role == PlayerRole.TANK || !tankWon && role == PlayerRole.HELICOPTER) {
			renderer.setColor(0.2f, 1, 0.2f, textAlpha);
		} else {
			renderer.setColor(1, 0.2f, 0.2f, textAlpha);
		}
		
		renderer.setFont(GameFont.GIGANTIC);
		centralizeXAndDraw("GAME ENDED", 150);
		
		renderer.setFont(GameFont.LARGE);
		centralizeXAndDraw(text, 300);
		
		renderer.setFont(GameFont.MEDIUM);
		renderer.setColor(1, 1, 1, textAlpha);
		centralizeXAndDraw(String.format("Resuming in %.2fs", endingCountdown.remainingSeconds()), 450);
		
		if (endingCountdown.finished()) {
			running = false;
		}
	}

	private BaseHUD createHUD() {
		BaseHUD hud;
		
		switch (role) {
		case HELICOPTER:
			hud = new HelicopterHUD(collection, window);
			break;
		case TANK:
			hud = new TankHUD(collection, window);
			break;
		default:
			return null;
		}
		
		hud.setScoreBehaviour(new HUDScoreBehaviour(role));
		return hud;
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
			if (!Direction.isRight(object.getDirection())) {
				renderer.setScale(new FloatVec(-1, 1));
			}
			
			renderer.setRotation(object.getRotation());
			
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
