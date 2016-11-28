package br.univali.game.controllers;

import br.univali.game.GameObjectCollection;
import br.univali.game.MiscType;
import br.univali.game.Texture;
import br.univali.game.TextureManager;
import br.univali.game.graphics.Renderer;
import br.univali.game.objects.DrawableObject;
import br.univali.game.util.Direction;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntRect;
import br.univali.game.util.IntVec;

public class DrawingController {
	private IntVec windowSize;
	private TextureManager manager;
	private GameObjectCollection collection;
	
	public DrawingController(GameObjectCollection collection, TextureManager manager, IntVec windowSize) {
		this.collection = collection;
		this.manager = manager;
		this.windowSize = windowSize;
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
//		FloatRect box = object.getBoundingBox();
//		renderer.setColor(1, 0, 0);
//		renderer.drawRectangle(box.x, box.y, box.width, box.height);
		
		if (object.getDirection() == Direction.LEFT) {
			renderer.setScale(new FloatVec(-1, 1));
		}
		
		renderer.setRotation(object.getRotation());
		
		Texture tex = manager.getObjectTexture(object.getType());
		IntRect frame = tex.getFrames().get(object.getCurrentFrame());
		
		renderer.drawSubImage(tex.getId(), object.getX(), object.getY(), frame);
		renderer.setRotation(0);
		
		renderer.setScale(new FloatVec(1, 1));
	}
	
	public void drawBackground(Renderer renderer) {
		Texture background = manager.getMiscTexture(MiscType.BACKGROUND);
		
		IntVec size = background.getSize();
		float x = windowSize.x / 2 - size.x / 2;
		float y = windowSize.y / 2 - size.y / 2;
		
		renderer.drawImage(background.getId(), x, y);
	}
	
	public void setCollection(GameObjectCollection collection) {
		this.collection = collection;
	}
}
