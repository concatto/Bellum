package br.univali.game;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import br.univali.game.graphics.Renderer;
import br.univali.game.remote.GameConnection;
import br.univali.game.remote.RemoteInterface;
import br.univali.game.window.GameWindow;
import br.univali.game.window.RenderMode;
import br.univali.game.window.WindowFactory;

public class GameClient {
	private ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> heartbeatFuture;
	private GameWindow window;
	private Renderer renderer;
	private RemoteInterface server;
	private GameConnection connection;
	private GameScreen game;
	
	public GameClient(RenderMode renderMode, String textureFolder) {
		window = WindowFactory.createWindow(renderMode, "Bellum", 800, 600);
		window.onCloseRequested(() -> System.exit(0));
		
		StartupScreen startup = new StartupScreen(window);		
		window.display();
		renderer = window.getRenderer();

		boolean connectionError = false;
		do {
			do {
				String host = startup.displayWelcome();
				if (host.isEmpty()) {
					host = "127.0.0.1";
				}
				
				try {
					Registry registry = LocateRegistry.getRegistry(host, 8080);
					server = (RemoteInterface) registry.lookup("server");
					
					connection = server.connectToServer();
					launchHeartbeatTask();
				} catch (RemoteException | NotBoundException e) {
					e.printStackTrace();
					startup.displayConnectionFailure();
					server = null;
				}
			} while (server == null);
			
			WaitingRoomScreen room = new WaitingRoomScreen(window, connection);
			
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
				startup.displayConnectionFailure();
				connection = null;
				heartbeatFuture.cancel(true);
				connectionError = true;
			}
		} while (connectionError);
		
		window.display();
		
		installListeners();
		beginQueryingCollection();
		
		game = new GameScreen(window, connection);
		game.start();
	}


	private void beginQueryingCollection() {
		new Thread(() -> {
			while (true) {
				try {
					connection.publishMousePosition(window.getMousePosition());

					if (game != null) {
						game.setCollection(server.getGameObjectCollection());
					}
//					drawing.setCollection(collection);
//					hud.setCollection(collection);
					
					Thread.sleep(16);
				} catch (RemoteException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		},"ServerCommunicator").start();
	}


	private void installListeners() {
		window.addKeyboardEventConsumer(event -> {
			try {
				connection.publishKeyboardEvent(event);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		
		window.addMouseEventConsumer(event -> { 
			try {
				connection.publishMouseEvent(event);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
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
//		drawing.clear(renderer);
//		drawing.drawBackground(renderer);
//		drawing.drawObjects(renderer);
//		hud.updateHUD();
		//hud.drawCannonCharge(calculateCannonBarFraction(), player.isCannonOnCooldown(), player.isCannonCharging());
		

//		hud.drawShieldEnergy(collection.getTank().getShieldEnergy());
		
		/*if (collection.getTank().getPowerupTime() > 0) {
			hud.drawPowerupBar(collection.getTank().getPowerupTime());
		}*/
		
		
//		try {
//			FloatVec pos = collection.getPlayerObject(connection.getIdentifier()).getPosition();
//			renderer.setColor(1, 0, 0, 1);
//			renderer.drawRectangle(pos.x, pos.y, 30, 30);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
