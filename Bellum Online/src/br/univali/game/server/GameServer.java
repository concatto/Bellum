package br.univali.game.server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import br.univali.game.NameGenerator;
import br.univali.game.Spawner;
import br.univali.game.controllers.AnimationController;
import br.univali.game.controllers.HelicopterController;
import br.univali.game.controllers.LogicController;
import br.univali.game.controllers.PhysicsController;
import br.univali.game.controllers.TankController;
import br.univali.game.graphics.TextureManager;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.remote.GameConnection;
import br.univali.game.remote.GameConnectionImpl;
import br.univali.game.remote.GameInformation;
import br.univali.game.remote.Player;
import br.univali.game.remote.RemoteInterface;
import br.univali.game.remote.RemoteInterfaceImpl;
import br.univali.game.util.IntVec;
import br.univali.game.window.RenderMode;

public class GameServer {
	private static final int HEARTBEAT_TIMEOUT = 3000;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
	private ServerWindow serverWindow;	
	private TextureManager textureManager;
	private GameObjectCollection collection;
	private LogicController logic;
	private PhysicsController physics;
	private AnimationController animation;
	private boolean running;
	private long lastFrame;
	private Spawner spawner;
	
	private List<Client> clients = new ArrayList<>();
	private Registry registry = null;
	private RemoteInterface remoteInterface;
	
	private IntVec worldSize = new IntVec(800, 600);

	public GameServer(RenderMode renderMode, String textureFolder) {
		serverWindow = new ServerWindow();
		serverWindow.setOnClose(() -> System.exit(0));
		serverWindow.setVisible(true);

		serverWindow.publishMessage("Loading textures...");
		try {
			textureManager = new TextureManager(textureFolder);
			
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
			remoteInterface = new RemoteInterfaceImpl(this);
			
			registry.bind("server", UnicastRemoteObject.exportObject(remoteInterface, 8080));
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
		
		launchHeartbeat();
		
		spawner.spawnTank();
		serverWindow.publishMessage("Creating controllers...");		
		
		logic = new LogicController(collection, spawner, worldSize);
		physics = new PhysicsController(collection, logic.getGroundLevel());
		animation = new AnimationController(collection, textureManager);	
		serverWindow.publishMessage("Controllers created.");
		
//		while (true) {
//			renderer.setColor(0.5f, 0.5f, 0.5f);
//			renderer.clear();
//			renderer.setColor(0, 1, 0);
//			renderer.setFont(new Font("Arial", Font.PLAIN, 32));
//			renderer.drawText("Hello", 0, 0);
//			renderer.draw();
//		}
		
//		running = true;
//		
//		while (running) {
//			logic.prepareGame();
//	
//			serverWindow.publishMessage("Displaying menu...");
//			beginMenu();
//			serverWindow.publishMessage("Done.");
//			
//			serverWindow.publishMessage("Beginning main loop...");
//			boolean dead = beginMainLoop();
//			
//			if (dead) {
//				displayDeathScreen();
//			}
//			
//			try {
//				collection.clear();
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//		}
	}

	private void launchHeartbeat() {
		heartbeatExecutor.scheduleAtFixedRate(() -> {
			for (ListIterator<Client> it = clients.listIterator(); it.hasNext(); ) {
				Client c = it.next();
				if (System.currentTimeMillis() - c.getConnection().getLastHeartbeat() > HEARTBEAT_TIMEOUT) {
					it.remove();
					this.serverWindow.publishMessage("Player disconnected: "+c.getIdentifier());
					//Provavelmente algo mais acontecerá quando isso ocorrer.
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}
	
	private void beginExecution() {
		int tankIndex = (int) Math.round(Math.random() * (clients.size() - 1));
		
		for (int i = 0; i < clients.size(); i++) {		
			Client conn = clients.get(i);
			
			if (i == tankIndex) {
				conn.setController(new TankController(spawner, collection, worldSize));
			} else {
				conn.setController(new HelicopterController(spawner, collection, worldSize));
			}
			
			i++;
		}
		
		executor.submit(() -> {
			running = true;
			
			while (running) {
				beginMainLoop();
			}
		});
	}
	
	/**
	 * Inicia o loop principal do jogo.
	 * @return true se o jogador morreu.
	 */
	public boolean beginMainLoop() {
		lastFrame = System.nanoTime();
		
		while (running) {		
			long time = System.nanoTime();
			float delta = (float) ((time - lastFrame) / 1E6);
			
			//serverWindow.publishMessage("FPS: " + (1000f / delta) + " (Rendering took " + delta + " ms).");
			
			logic.cleanupBullets();
			
			logic.tryGenerateEnemy();
			logic.tryGenerateHealth();
			logic.tryGenerateSpecial();
			
			for (Client c : clients) {
				try {
					c.getController().update(delta);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
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
		}
		
		return false;
	}

	public GameObjectCollection getGameObjectCollection() {
		return collection;
	}

	public GameConnection createConnection() {
		String identifier;
		boolean matches = false;
		do {
			String s = NameGenerator.getRandomName();
			matches = clients.stream().anyMatch(c -> c.getIdentifier().equals(s));
			identifier = s;
		} while (matches);
		
		GameConnectionImpl conn = new GameConnectionImpl(identifier);
		Client client = new Client(conn);
		
		clients.add(client);
		
		conn.setGameInformationCallable(() -> {
			return new GameInformation(clients.stream()
					.map(c -> new Player(c.getIdentifier(), c.isReady()))
					.collect(Collectors.toList()));
		});
		
		conn.setReadyConsumer(r -> {
			client.setReady(r);
			serverWindow.publishMessage( "Player"+ (r ? "":" not") +" ready: "+client.getIdentifier());
		});
		
		if (clients.size() == 1) {
			//beginExecution();
		}
		
		serverWindow.publishMessage("Player connected: "+identifier);
		
		return conn;
	}
}
