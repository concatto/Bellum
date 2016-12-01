package br.univali.game.controllers;

import java.util.Set;
import java.util.TreeSet;

import br.univali.game.Spawner;
import br.univali.game.event.input.InputEventType;
import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseButton;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.objects.GameObjectCollection;
import br.univali.game.util.Direction;
import br.univali.game.util.IntVec;

public abstract class PlayerController {
	protected Set<MouseButton> pressedButtons = new TreeSet<>();
	protected Set<Integer> pressedKeys = new TreeSet<>();

	protected MouseButton bulletButton;

	protected long lastBullet = 0;
	
	private int leftKey = 'A';
	private int rightKey = 'D';

	protected Spawner spawner;
	protected GameObjectCollection collection;
	
	protected IntVec mousePosition;
	protected IntVec windowSize;
	
	public PlayerController(Spawner spawner, GameObjectCollection collection, IntVec windowSize) {
		this.spawner = spawner;
		this.collection = collection;
		this.windowSize = windowSize;
	}
	
	public void handleKey(KeyboardEvent e) {
		if (e.getType() == InputEventType.PRESS) {
			pressedKeys.add(e.getKey());
		} else {
			pressedKeys.remove(e.getKey());
		}
	}

	public Direction computeDirection() {
		boolean left = pressedKeys.contains(leftKey);
		boolean right = pressedKeys.contains(rightKey);
		
		//XNOR: Se ambos são true ou ambos são false
		if (!(left ^ right)) {
			return Direction.NONE;
		} else if (left) {
			return Direction.LEFT;
		} else { //Right
			return Direction.RIGHT;
		}
	}

	public void handleMouse(MouseEvent e) {
		if (e.getType() == InputEventType.PRESS) {
			pressedButtons.add(e.getButton());
		} else {
			pressedButtons.remove(e.getButton());
		}
	}
	
	public void setMousePosition(IntVec mousePosition) {
		this.mousePosition = mousePosition;
	}
	
	public IntVec getMousePosition() {
		return mousePosition;
	}
	
	public Set<MouseButton> getPressedButtons() {
		return pressedButtons;
	}
	
	public Set<Integer> getPressedKeys() {
		return pressedKeys;
	}
	
	public abstract void update(float delta);  
	
}
