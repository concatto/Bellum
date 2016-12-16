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

import br.univali.game.GameConstants;
import br.univali.game.NameGenerator;
import br.univali.game.PlayerRole;
import br.univali.game.Spawner;
import br.univali.game.controllers.AnimationController;
import br.univali.game.controllers.HelicopterController;
import br.univali.game.controllers.LogicController;
import br.univali.game.controllers.PhysicsController;
import br.univali.game.controllers.TankController;
import br.univali.game.graphics.TextureManager;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.Enemy;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.PlayerTank;
import br.univali.game.remote.GameConnection;
import br.univali.game.remote.GameConnectionImpl;
import br.univali.game.remote.GameInformation;
import br.univali.game.remote.RemoteInterface;
import br.univali.game.remote.RemoteInterfaceImpl;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntVec;
import br.univali.game.window.RenderMode;

public class GameServer {
	private static final int HEARTBEAT_TIMEOUT = 3000;
	public static final long PREPARE_TIME = 5000;
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
	}

	private void launchHeartbeat() {
		heartbeatExecutor.scheduleAtFixedRate(() -> {
			for (ListIterator<Client> it = clients.listIterator(); it.hasNext(); ) {
				Client c = it.next();
				
				long delta = System.currentTimeMillis() - c.getConnection().getLastHeartbeat();
				if (delta > HEARTBEAT_TIMEOUT) {
					it.remove();
					serverWindow.publishMessage("Player disconnected: "+c.getIdentifier());
					//Provavelmente algo mais acontecerá quando isso ocorrer.
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	private void prepareGame() {
		int tankIndex = (int) Math.round(Math.random() * (clients.size() - 1));

		serverWindow.publishMessage("Creating player controllers...");

		for (int i = 0; i < clients.size(); i++) {
			Client c = clients.get(i);
			
			if (i == tankIndex) {	
				PlayerTank tank = spawner.spawnTank(worldSize.x / 2f, worldSize.y - GameConstants.GROUND_Y_OFFSET);
				
				collection.addPlayerObject(c.getIdentifier(), tank);
				c.setController(new TankController(spawner, collection, worldSize, tank));
				c.getConnection().setRole(PlayerRole.TANK);
			} else {
				Enemy helicopter = spawner.spawnHelicopter(new FloatVec(50 + (i * 50), 50));
				
				collection.addPlayerObject(c.getIdentifier(), helicopter);
				c.setController(new HelicopterController(spawner, collection, worldSize, helicopter));
				c.getConnection().setRole(PlayerRole.HELICOPTER);
			}
			
			serverWindow.publishMessage("Client " + c.getIdentifier() + " is playing as " + c.getRole());
		}
		
		serverWindow.publishMessage("Starting game...");
		
		try {
			Thread.sleep(PREPARE_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		serverWindow.publishMessage("Creating controllers...");
		
		logic = new LogicController(collection, spawner, worldSize);
		physics = new PhysicsController(collection, logic.getGroundLevel());
		animation = new AnimationController(collection, textureManager);	
		serverWindow.publishMessage("Controllers created.");
		
		
		serverWindow.publishMessage("Players controllers created.");
		running = true;
		
		while (running) {
			beginGame();
		}
		
	}
	
	/**
	 * Inicia o loop principal do jogo.
	 * @return true se o jogador morreu.
	 */
	public boolean beginGame() {
		serverWindow.publishMessage("Game starts.");
		
		lastFrame = System.nanoTime();
		
		while (true) {
			try {
				long time = System.nanoTime();
				float delta = (float) ((time - lastFrame) / 1E6);
				
				//serverWindow.publishMessage("FPS: " + (1000f / delta) + " (Rendering took " + delta + " ms).");
				
				logic.cleanupBullets();
				
				logic.tryGenerateEnemy();
				logic.tryGenerateHealth();
				logic.tryGenerateSpecial();
				
				for (Client c : clients) {
					c.getController().update(delta);
				}
				
				logic.updateEnemies(delta);
				physics.updatePositions(delta);
				
				logic.handleEnemyCollisions(physics.checkEnemyCollisions());
				logic.handleGroundCollisions(physics.checkGroundCollisions());
				logic.handlePlayerCollisions(physics.checkPlayerCollisions());
				logic.handlePickupCollisions(physics.checkPickupCollisions());
			
				for (Client c : clients) {
					CombatObject obj = collection.getPlayerObject(c.getIdentifier());
					
					
					if (obj.shouldRespawn()) {
						if (c.getRole() == PlayerRole.HELICOPTER) {
							logic.respawnHelicopter(obj);
						}
					}
					
					if (obj.getHealth() <= 0 && !obj.isRespawning()) {
						if (c.getRole() == PlayerRole.HELICOPTER) {
							obj.prepareRespawn(3000);
							obj.setAffectedByGravity(true);
						} else if (c.getRole() == PlayerRole.TANK) {
							terminateGame();
						}
					}
				}
				
				animation.updateAnimations(delta);
				lastFrame = time;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//return false;
	}

	private void terminateGame() {
		// TODO Auto-generated method stub
		
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
		
		conn.setServerReadyCallable(() -> running);
		conn.setGameInformationCallable(() -> {
			return new GameInformation(clients.stream()
					.map(c -> c.convertToPlayer())
					.collect(Collectors.toList()));
		});
		
		conn.setReadyConsumer(r -> {
			client.setReady(r);
			serverWindow.publishMessage("Player" + (r ? "" : " not") + " ready: " + client.getIdentifier());
			
			//inicia o jogo se todos estiverem prontos
			if (clients.stream().allMatch(c -> c.isReady())) {
				executor.submit(this::prepareGame);
			}
		});
		
		serverWindow.publishMessage("Player connected: "+identifier);
		
		return conn;
	}
}
