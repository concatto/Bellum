package br.univali.game;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import br.univali.game.controllers.DrawingController;
import br.univali.game.controllers.HUDController;
import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.TextureManager;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.remote.GameConnection;
import br.univali.game.remote.RemoteInterface;
import br.univali.game.util.FloatVec;
import br.univali.game.window.GameWindow;
import br.univali.game.window.RenderMode;
import br.univali.game.window.WindowFactory;

public class GameClient {
	private ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> heartbeatFuture;
	private GameWindow window;
	private Renderer renderer;
	private TextureManager textureManager;
	private HUDController hud;
	private DrawingController drawing;
	private RemoteInterface server;
	private GameConnection connection;
	private GameObjectCollection collection;
	
	public GameClient(RenderMode renderMode, String textureFolder) {
		window = WindowFactory.createWindow(renderMode, "Bellum", 800, 600);
		MainMenu menu = new MainMenu(window);
		
		window.display();
		renderer = window.getRenderer();
		
		boolean connectionError = false;
		do {
			do {
				menu.displayWelcome();
				
				try {
					Registry registry = LocateRegistry.getRegistry(8080);
					server = (RemoteInterface) registry.lookup("server");
					
					connection = server.connectToServer();
					launchHeartbeatTask();
				} catch (RemoteException | NotBoundException e) {
					e.printStackTrace();
					menu.displayConnectionFailure();
					server = null;
				}
			} while (server == null);
			
			WaitingRoom room = new WaitingRoom(window, connection);
			
			room.onReady(ready -> {
				try {
					connection.publishReady(ready);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			});

			try {
				room.display();
			} catch (ConnectException e2) {
				e2.printStackTrace();
				menu.displayConnectionFailure();
				connection = null;
				heartbeatFuture.cancel(true);
				connectionError = true;
			}
		} while (connectionError);
		
		/// Start game
		
		textureManager = new TextureManager(textureFolder);
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
				connection.publishKeyboardEvent(event);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		
		window.onMouseEvent(event -> { 
			try {
				connection.publishMouseEvent(event);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		
		new Thread(() -> {
			while (true) {
				long start = System.currentTimeMillis();
				try {
					connection.publishMousePosition(window.getMousePosition());
					
					collection = server.getGameObjectCollection();
					drawing.setCollection(collection);
					hud.setCollection(collection);
					
					Thread.sleep(16);
				} catch (RemoteException | InterruptedException e1) {
					e1.printStackTrace();
				}
				
				//System.out.println("Took " + (System.currentTimeMillis() - start));
			}
		},"ServerCommunicator").start();
		
		while (true) {
			try {
				Thread.sleep(8);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (collection == null) {
				continue;
			}
			
			drawGame();
			renderer.draw();
			
			if (collection.getTank().getHealth() <= 0) {
				
			}
		}
	}
	
	
	private void launchHeartbeatTask() {
		 heartbeatFuture = heartbeatExecutor.scheduleAtFixedRate(() -> {
			 if (connection == null) return;
			 try {
				 connection.heartbeat();
			 } catch (RemoteException e) {
				 e.printStackTrace();
			 }
		}, 0, 1, TimeUnit.SECONDS);
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
		
		
		try {
			FloatVec pos = connection.getObject().getPosition();
			renderer.setColor(1, 0, 0, 1);
			renderer.drawRectangle(pos.x, pos.y, 30, 30);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
