package br.univali.game;

public enum PlayerRole {
	HELICOPTER("helicopter"),
	TANK("tank"),
	NONE();
	
	private String value;
	
	private PlayerRole() {
		this.value = "";
	}
	
	private PlayerRole(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return getValue();
	}
}
