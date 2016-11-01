package br.univali.game.event.input;

public class MouseEvent extends InputEvent {
	private MouseButton button;
	
	public MouseEvent(MouseButton button, InputEventType type) {
		super(type);
		this.button = button;
	}
	
	public MouseButton getButton() {
		return button;
	}
}
