package br.univali.game.event.input;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class InputEvent implements Serializable {	
	private InputEventType type;
	
	public InputEvent(InputEventType type) {
		this.type = type;
	}

	public InputEventType getType() {
		return type;
	}
}
