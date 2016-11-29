package br.univali.game;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import br.univali.game.controllers.AnimationController;
import br.univali.game.controllers.LogicController;
import br.univali.game.controllers.PhysicsController;
import br.univali.game.controllers.PlayerController;
import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseButton;
import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.TextureManager;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.remote.RemoteInterface;
import br.univali.game.remote.Server;
import br.univali.game.window.GameWindow;
import br.univali.game.window.RenderMode;
import br.univali.game.window.WindowFactory;

public class GameServer {
	private ServerWindow serverWindow;
	private Renderer renderer;
	private TextureManager textureManager;
	private GameObjectCollection collection;
	private LogicController logic;
	private PhysicsController physics;
	private AnimationController animation;
	private boolean running;
	private long lastFrame;
	private PlayerController player;
	private Spawner spawner;
	
	private Registry registry = null;
	private RemoteInterface server;

	public GameServer(RenderMode renderMode, String textureFolder) {
		serverWindow = new ServerWindow();
		serverWindow.setOnClose(() -> System.exit(0));
		serverWindow.setVisible(true);
		
		GameWindow window = WindowFactory.createWindow(renderMode, "", 800, 600);
		renderer = window.getRenderer();
		textureManager = new TextureManager(renderer, textureFolder);
		
		serverWindow.publishMessage("Loading textures...");
		try {
			textureManager.loadAllTextures();
			serverWindow.publishMessage("Texture loading completed.");
		} catch (IOException e) {
			serverWindow.publishMessage("Failed to load textures.");
			e.printStackTrace();
		}
		
		serverWindow.publishMessage("Creating object managers...");
		collection = new GameObjectCollection();
		spawner = new Spawner(textureManager, collection);
		serverWindow.publishMessage("Managers created.");
		
		
		try {
			registry = LocateRegistry.createRegistry(8080);
			server = new Server(collection, this);
			
			registry.bind("server", UnicastRemoteObject.exportObject(server, 8080));
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
		
		
		spawner.spawnTank();
		
		serverWindow.publishMessage("Creating controllers...");
		player = new PlayerController(spawner, collection, window.getSize());
		player.setBulletButton(MouseButton.RIGHT);
		player.setCannonballButton(MouseButton.LEFT);
		player.setLeftKey('A');
		player.setRightKey('D');
		player.setShieldKey(KeyboardEvent.SPACE);
		
		logic = new LogicController(collection, spawner, window.getSize());
		physics = new PhysicsController(collection, logic.getGroundLevel());
		animation = new AnimationController(collection, textureManager);	
		serverWindow.publishMessage("Controllers created.");
		
		running = true;
		
		while (running) {
			logic.prepareGame();
	
			serverWindow.publishMessage("Displaying menu...");
			beginMenu();
			serverWindow.publishMessage("Done.");
			
			serverWindow.publishMessage("Beginning main loop...");
			boolean dead = beginMainLoop();
			
			if (dead) {
				displayDeathScreen();
			}
			
			try {
				collection.clear();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private void displayDeathScreen() {
//		menu.prepareToDie();
//		
//		do {
//			drawGame();
//			menu.drawYouDied(renderer);
//			renderer.draw();
//			
//		} while (!menu.didClick());
	}
	
	private void beginMenu() {
//		window.display();
		
		try {
			boolean notReady = true;
			do {
//				drawGame();
//				menu.drawGameMenu(renderer);
//				renderer.draw();
				notReady = !server.shouldStart();
				Thread.sleep(100);
			} while (notReady);
		} catch (RemoteException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inicia o loop principal do jogo.
	 * @return true se o jogador morreu.
	 */
	public boolean beginMainLoop() {
		lastFrame = System.nanoTime();
		player.resetFlags();
		
		while (running) {		
			long time = System.nanoTime();
			float delta = (float) ((time - lastFrame) / 1E6);
			
			//serverWindow.publishMessage("FPS: " + (1000f / delta) + " (Rendering took " + delta + " ms).");
			
			logic.cleanupBullets();
			
			logic.tryGenerateEnemy();
			logic.tryGenerateHealth();
			logic.tryGenerateSpecial();
			
			player.updateTank(delta);
			logic.updateEnemies(delta);
			physics.updatePositions(delta);
			
			logic.handleEnemyCollisions(physics.checkEnemyCollisions());
			logic.handleGroundCollisions(physics.checkGroundCollisions());
			logic.handlePlayerCollisions(physics.checkPlayerCollisions());
			logic.handlePickupCollisions(physics.checkPickupCollisions());
		
			if (collection.getTank().getHealth() <= 0) {
				//return true;
			}
			
			animation.updateAnimations(delta);	
			lastFrame = time;
			
			//exportCollection();
		}
		
		return false;
	}
}
