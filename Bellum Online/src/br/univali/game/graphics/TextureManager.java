package br.univali.game.graphics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.univali.game.objects.ObjectType;
import br.univali.game.util.IntRect;
import br.univali.game.util.IntVec;

public class TextureManager {
	private Texture notFound;
	private Map<ObjectType, Texture> objectTextures = new HashMap<>();
	private String textureFolder;
	
	public TextureManager(String textureFolder) {
		this.textureFolder = textureFolder;
	}
	
	public void loadAllTextures() throws IOException {
		notFound = loadTexture("notfound.png");
		
		objectTextures.put(ObjectType.PLAYER_TANK, loadTexture("tank.png"));
		objectTextures.put(ObjectType.CANNONBALL, loadTexture("cannonball.png"));
		objectTextures.put(ObjectType.BULLET, loadTexture("bullet.png"));
		objectTextures.put(ObjectType.HELICOPTER, loadTexture("helicopter.png"));
		objectTextures.put(ObjectType.EXPLOSION, loadTexture("explosion.png", 4, 4));
		objectTextures.put(ObjectType.SPARK, loadTexture("spark.png", 1, 4));
		objectTextures.put(ObjectType.SHIELD, loadTexture("shield.png", 2, 4));
		objectTextures.put(ObjectType.SPECIAL_BULLET, loadTexture("special.png", 1, 4));
		objectTextures.put(ObjectType.SPECIAL_EXPLOSION, loadTexture("explosionFull.png", 4, 8));
		objectTextures.put(ObjectType.HEALTH_PICKUP, loadTexture("health.png"));
		objectTextures.put(ObjectType.SPECIAL_PICKUP, loadTexture("specialcrate.png"));
		objectTextures.put(ObjectType.FIRE, loadTexture("fire.png", 4, 8));
	}
	
	private Texture loadTexture(String path, int rows, int columns) throws IOException {
		return Texture.load(textureFolder + "/" + path,
				size -> generateFrames(size, rows, columns));
	}
	
	private static List<IntRect> generateFrames(IntVec size, int rows, int columns) {
		List<IntRect> frames = new ArrayList<>();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int x = j * (size.x / columns);
				int y = i * (size.y / rows);
				
				frames.add(new IntRect(x, y, size.x / columns, size.y / rows));
			}
		}
		
		return frames;
	}
	
	private Texture loadTexture(String path) throws IOException {
		return loadTexture(path, 1, 1);
	}

	public Texture getObjectTexture(ObjectType type) {
		return objectTextures.getOrDefault(type, notFound);
	}
}
