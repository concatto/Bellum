package br.univali.game.controllers;

import java.util.ArrayList;
import java.util.List;

import br.univali.game.GameObjectCollection;
import br.univali.game.Texture;
import br.univali.game.TextureManager;
import br.univali.game.objects.DrawableObject;

public class AnimationController {
	private TextureManager manager;
	private GameObjectCollection collection;
	
	public AnimationController(GameObjectCollection collection, TextureManager manager) {
		this.collection = collection;
		this.manager = manager;
	}
	
	public void updateAnimations(float delta) {
		List<DrawableObject> toRemove = new ArrayList<>();
		
		for (DrawableObject object : collection.getDrawableObjects()) {
			Texture tex = manager.getObjectTexture(object.getType());
			
			if (tex.getFrames().size() <= 1) continue;
			
			float currentTime = object.getFrameTime() + delta;
			
			if (currentTime > object.getFrameDuration()) {
				object.setFrameTime(0);
				object.advanceFrame();
				
				if (object.getCurrentFrame() >= tex.getFrames().size() - 1) {
					if (object.isAnimationRepeated()) {
						object.setCurrentFrame(0);
					} else {
						toRemove.add(object);
					}
				}
			} else {
				object.setFrameTime(currentTime);
			}
		}
		
		for (DrawableObject r : toRemove) {
			collection.removeObject(r);
		}
	}
}