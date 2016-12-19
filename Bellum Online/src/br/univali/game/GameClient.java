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

import br.univali.game.remote.GameConnection;
import br.univali.game.remote.RemoteInterface;
import br.univali.game.server.ServerFullException;
import br.univali.game.window.GameWindow;
import br.univali.game.window.RenderMode;
import br.univali.game.window.WindowFactory;

public class GameClient {
	private ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledExecutorService communicationExecutor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> heartbeatFuture;
	private ScheduledFuture<?> communicationFuture;
	private GameWindow window;
	private RemoteInterface server;
	private GameConnection connection;
	private GameScreen game;
	private WaitingRoomScreen room;
	private StartupScreen startup;
	
	public GameClient(RenderMode renderMode) {
		window = WindowFactory.createWindow(renderMode, "Bellum", 800, 600);
		window.onCloseRequested(() -> System.exit(0));
		
		startup = new StartupScreen(window);		
		window.display();
		
		boolean gameRunning = connect(startup);
		room = new WaitingRoomScreen(window, connection);
		game = new GameScreen(window, connection);
		installListeners();

		do {
			if (gameRunning) {
				startup.displayJoining();
				gameRunning = false;
			} else {
				displayWaitingRoom();
			}
		
			window.display();
			
			beginQueryingCollection();
			game.start();
			
			communicationFuture.cancel(true);
		} while (true);
	}


	private void displayWaitingRoom() {
		try {
			room.display();
		} catch (ConnectException e2) {
			e2.printStackTrace();
			heartbeatFuture.cancel(true);
		}
	}


	private boolean connect(StartupScreen startup) {
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
				
				if (connection.isServerReady()) {
					return true;
				}
			} catch (RemoteException | NotBoundException | ServerFullException e) {
				e.printStackTrace();
				startup.displayConnectionFailure(e);
				server = null;
			}
		} while (server == null);
		
		return false;
	}


	private void beginQueryingCollection() {
		communicationFuture = communicationExecutor.scheduleAtFixedRate(() -> {
			try {
				connection.publishMousePosition(window.getMousePosition());

				if (game != null) {
					game.setCollection(server.getGameObjectCollection());
				}
				
			} catch (RemoteException e1) {
				
			}
		}, 0, 16, TimeUnit.MILLISECONDS);
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
}
