package br.univali.game.controllers;

import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.graphics.TextureManager;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.util.Direction;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;
import br.univali.game.util.IntRect;
import br.univali.game.util.IntVec;

public class DrawingController {
	private IntVec windowSize;
	private TextureManager manager;
	private GameObjectCollection collection;
	private Texture backgroundTexture;
	
	public DrawingController(GameObjectCollection collection, TextureManager manager, IntVec windowSize) {
		this.collection = collection;
		this.manager = manager;
		this.windowSize = windowSize;
		this.backgroundTexture = Texture.load("images/background.png");
	}
	
	public void clear(Renderer renderer) {
		renderer.setColor(0.23f, 0.62f, 0.8f);
		renderer.clear();
	}
	
	public void drawObjects(Renderer renderer) {
		for (DrawableObject drawable : collection.getDrawableObjects()) {
			drawObject(drawable, renderer);
		}
	}
	
	private void drawObject(DrawableObject object, Renderer renderer) {
		if (object.getDirection() == Direction.LEFT) {
			renderer.setScale(new FloatVec(-1, 1));
		}
		
		renderer.setRotation(object.getRotation());
		
		Texture tex = manager.getObjectTexture(object.getType());
		IntRect frame = tex.getFrames().get(object.getCurrentFrame());
		
		renderer.drawTextureFrame(tex, object.getX(), object.getY(), frame);
		renderer.setRotation(0);
		
		renderer.setScale(new FloatVec(1, 1));
	}
	
	public void drawBackground(Renderer renderer) {
		FloatVec pos = Geometry.centerVector(backgroundTexture.getSize().toFloat(), windowSize.toFloat());
		
		renderer.drawTexture(backgroundTexture, pos.x, pos.y);
	}
	
	public void setCollection(GameObjectCollection collection) {
		this.collection = collection;
	}
}
