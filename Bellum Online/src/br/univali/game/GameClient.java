package br.univali.game;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import br.univali.game.controllers.DrawingController;
import br.univali.game.controllers.HUDController;
import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.TextureManager;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.remote.RemoteInterface;
import br.univali.game.window.GameWindow;
import br.univali.game.window.RenderMode;
import br.univali.game.window.WindowFactory;

public class GameClient {
	private GameWindow window;
	private Renderer renderer;
	private TextureManager textureManager;
	private HUDController hud;
	private DrawingController drawing;
	private RemoteInterface server;
	private GameObjectCollection collection;
	
	public GameClient(RenderMode renderMode, String textureFolder) {
		window = WindowFactory.createWindow(renderMode, "Bellum", 800, 600);
		renderer = window.getRenderer();
		textureManager = new TextureManager(renderer, textureFolder);
		
		try {
			Registry registry = LocateRegistry.getRegistry(8080);
			server = (RemoteInterface) registry.lookup("server");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		
		try {
			collection = server.getGameObjectCollection();
		} catch (RemoteException e1) {
			collection = null;
			e1.printStackTrace();
		}
		
		hud = new HUDController(collection, renderer, window.getSize());
		drawing = new DrawingController(collection, textureManager, window.getSize());
		
		System.out.println("Loading textures...");
		try {
			textureManager.loadAllTextures();
			System.out.println("Texture loading completed.");
		} catch (IOException e) {
			System.out.println("Failed to load textures.");
			e.printStackTrace();
		}
		
		window.display();
		window.onKeyboardEvent(event -> {
			try {
				server.publishKeyboardEvent(event);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		
		try {
			System.out.println("My id is " + server.connectToServer());
			server.startGame();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		new Thread(() -> {
			while (true) {
				long start = System.currentTimeMillis();
				try {
					collection = server.getGameObjectCollection();
					drawing.setCollection(collection);
					hud.setCollection(collection);
					
					Thread.sleep(16);
				} catch (RemoteException | InterruptedException e1) {
					e1.printStackTrace();
				}
				
				System.out.println("Took " + (System.currentTimeMillis() - start));
			}
		}).start();
		
		while (true) {
			drawGame();
			renderer.draw();
			
			if (collection.getTank().getHealth() <= 0) {
				
			}
		}
	}
	
	
	private void drawGame() {
		drawing.clear(renderer);
		drawing.drawBackground(renderer);
		drawing.drawObjects(renderer);
		hud.updateHUD();
		//hud.drawCannonCharge(calculateCannonBarFraction(), player.isCannonOnCooldown(), player.isCannonCharging());
		

		hud.drawShieldEnergy(collection.getTank().getShieldEnergy());
		
		/*if (collection.getTank().getPowerupTime() > 0) {
			hud.drawPowerupBar(collection.getTank().getPowerupTime());
		}*/
	}
	
	private float calculateCannonBarFraction() {
		/*boolean cooldown = player.isCannonOnCooldown();
		
		if (cooldown) {
			return 1 - (player.getRemainingCannonCooldown() / GameConstants.CANNON_COOLDOWN);
		} else {
			return player.getCannonCharge() / GameConstants.MAX_CANNONBALL_TIME;
		}*/
		
		return 0;
	}
}
