package br.univali.game;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import br.univali.game.controllers.AnimationController;
import br.univali.game.controllers.DrawingController;
import br.univali.game.controllers.HUDController;
import br.univali.game.controllers.LogicController;
import br.univali.game.controllers.MenuController;
import br.univali.game.controllers.PhysicsController;
import br.univali.game.controllers.PlayerController;
import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseButton;
import br.univali.game.graphics.Renderer;
import br.univali.game.remote.RemoteInterface;
import br.univali.game.remote.Server;
import br.univali.game.sound.*;
import br.univali.game.window.GameWindow;
import br.univali.game.window.RenderMode;
import br.univali.game.window.WindowFactory;

public class Game {
	private GameWindow window;
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

	private MenuController menu;
	
	private Registry registry = null;
	private RemoteInterface server;

	public Game(RenderMode renderMode, String textureFolder) {		
		window = WindowFactory.createWindow(renderMode, "Bellum", 800, 600);
		renderer = window.getRenderer();
		textureManager = new TextureManager(renderer, textureFolder);
		
		System.out.println("Loading textures...");
		try {
			textureManager.loadAllTextures();
			System.out.println("Texture loading completed.");
		} catch (IOException e) {
			System.out.println("Failed to load textures.");
			e.printStackTrace();
		}
		
		System.out.println("Creating object managers...");
		collection = new GameObjectCollection();
		spawner = new Spawner(textureManager, collection);
		System.out.println("Managers created.");
		
		
		try {
			registry = LocateRegistry.createRegistry(8080);
			server = new Server(collection, this);
			
			registry.bind("server", UnicastRemoteObject.exportObject(server, 8080));
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
		
		
		spawner.spawnTank();
		
		System.out.println("Creating controllers...");
		player = new PlayerController(spawner, collection, window.getSize());
		player.setBulletButton(MouseButton.RIGHT);
		player.setCannonballButton(MouseButton.LEFT);
		player.setLeftKey('A');
		player.setRightKey('D');
		player.setShieldKey(KeyboardEvent.SPACE);
		
		logic = new LogicController(collection, spawner, window.getSize());
		physics = new PhysicsController(collection, logic.getGroundLevel());
		animation = new AnimationController(collection, textureManager);
		menu = new MenuController(player, textureManager, window.getSize());		
		System.out.println("Controllers created.");
		
		System.out.println("Installing listeners...");
		window.onCloseRequested(() -> {
			window.destroy();
			System.exit(0);
		});
		
		window.onMouseEvent(player::handleMouse);
		window.onKeyboardEvent(player::handleKey);
		System.out.println("Listeners installed.");
		
		running = true;
		
		while (running) {
			logic.prepareGame();
	
			System.out.println("Displaying menu...");
			beginMenu();
			System.out.println("Done.");
			
			System.out.println("Beginning main loop...");
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
			do {
//				drawGame();
//				menu.drawGameMenu(renderer);
//				renderer.draw();
				
				System.out.println(server.shouldStart());
			} while (!server.shouldStart());
		} catch (RemoteException e) {
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
			
			System.out.println("FPS: " + (1000f / delta) + " (Rendering took " + delta + " ms).");
			//SoundEffect.BACKGROUND.restart();
			
			
			//player.setMousePosition(window.getMousePosition());
			
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
				return true;
			}
			
			animation.updateAnimations(delta);	
			lastFrame = time;
			
			//exportCollection();
		}
		
		return false;
	}
}
