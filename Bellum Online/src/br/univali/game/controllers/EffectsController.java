package br.univali.game.controllers;

import java.util.HashMap;
import java.util.Map;

import br.univali.game.Spawner;
import br.univali.game.objects.CombatObject;
import br.univali.game.objects.DrawableObject;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.objects.PlayerTank;
import br.univali.game.util.FloatVec;
import br.univali.game.util.Geometry;

public class EffectsController {
	private GameObjectCollection collection;
	private Map<CombatObject, DrawableObject> fireMap = new HashMap<>();
	private Spawner spawner;
	private DrawableObject shield = null;
	
	public EffectsController(GameObjectCollection collection, Spawner spawner) {
		this.collection = collection;
		this.spawner = spawner;
	}
	
	public void update(float delta) {
		fireMap.forEach((obj, fire) -> {
			FloatVec center = Geometry.centralize(fire.getBoundingBox(), obj.getBoundingBox());
			fire.setPosition(center.x, obj.getY() - fire.getHeight() + (obj.getHeight() / 2));
		});
		
		PlayerTank tank = collection.getTank();
		if (tank.isShielded()) {
			if (shield == null) {
				shield = spawner.spawnShield();
			}
			
			shield.setPosition(Geometry.centralize(shield.getBoundingBox(), tank.getBoundingBox()));
		} else if (shield != null) {
			collection.removeEffect(shield);
			shield = null;
		}
	}

	public void putFireTo(CombatObject object) {
		fireMap.put(object, spawner.spawnFire());
	}

	public void removeFireFrom(CombatObject object) {
		DrawableObject fire = fireMap.get(object);
		collection.removeEffect(fire);
		fireMap.remove(object);
	}
}
