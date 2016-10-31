package br.univali.game.event.input;

public abstract class InputEvent {	
	private InputEventType type;
	
	public InputEvent(InputEventType type) {
		this.type = type;
	}

	public InputEventType getType() {
		return type;
	}
}
