package br.univali.game;

import java.io.IOException;

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
	private DrawingController drawing;
	private AnimationController animation;
	private boolean running;
	private long lastFrame;
	private PlayerController player;
	private Spawner spawner;
	private HUDController hud;
	private MenuController menu;

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
		
		spawner.spawnTank();
		
		System.out.println("Creating controllers...");
		player = new PlayerController(spawner, collection, window.getSize());
		player.setBulletButton(MouseButton.RIGHT);
		player.setCannonballButton(MouseButton.LEFT);
		player.setLeftKey('A');
		player.setRightKey('D');
		player.setShieldKey(KeyboardEvent.SPACE);
		
		hud = new HUDController(collection, renderer, window.getSize());
		logic = new LogicController(collection, spawner, window.getSize());
		physics = new PhysicsController(collection, logic.getGroundLevel());
		drawing = new DrawingController(collection, textureManager, window.getSize());
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
			if(textureFolder.equals("halloween")){
				//SoundEffect.MENU.stop();
				//SoundEffect.BAT.play();
			}else{
				//SoundEffect.MENU.stop();
				//SoundEffect.BACKGROUND.play();
			}
			
			System.out.println("Done.");
			
			System.out.println("Beginning main loop...");
			boolean dead = beginMainLoop();
			
			if (dead) {
				displayDeathScreen();
			}
			
			collection.clear();
		}
	}

	private void displayDeathScreen() {
		menu.prepareToDie();
		//SoundEffect.BACKGROUND.stop();
		//SoundEffect.DEAD.play();

		
		do {
			drawGame();
			menu.drawYouDied(renderer);
			renderer.draw();
			
		} while (!menu.didClick());
	}
	
	private void beginMenu() {
		window.display();
		
		//SoundEffect.MENU.play();
		do {
			drawGame();
			menu.drawGameMenu(renderer);
			renderer.draw();
		} while (!menu.didClick());
	}
	
	/**
	 * Inicia o loop principal do jogo.
	 * @return true se o jogador morreu.
	 */
	private boolean beginMainLoop() {		
		lastFrame = System.nanoTime();
		player.resetFlags();
		while (running) {		
			long time = System.nanoTime();
			float delta = (float) ((time - lastFrame) / 1E6);
			
			System.out.println("FPS: " + (1000f / delta) + " (Rendering took " + delta + " ms).");
			//SoundEffect.BACKGROUND.restart();
			
			
			
			player.setMousePosition(window.getMousePosition());			
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
			
			drawGame();
			renderer.draw();
			
			lastFrame = time;
		}
		
		return false;
	}
	
	private void drawGame() {
		drawing.clear(renderer);
		drawing.drawBackground(renderer);
		drawing.drawObjects(renderer);
		hud.updateHUD();
		hud.drawCannonCharge(calculateCannonBarFraction(), player.isCannonOnCooldown(), player.isCannonCharging());
		hud.drawShieldEnergy(collection.getTank().getShieldEnergy());
		
		if (collection.getTank().getPowerupTime() > 0) {
			hud.drawPowerupBar(collection.getTank().getPowerupTime());
		}
	}
	
	private float calculateCannonBarFraction() {
		boolean cooldown = player.isCannonOnCooldown();
		
		if (cooldown) {
			return 1 - (player.getRemainingCannonCooldown() / GameConstants.CANNON_COOLDOWN);
		} else {
			return player.getCannonCharge() / GameConstants.MAX_CANNONBALL_TIME;
		}
	}
}
