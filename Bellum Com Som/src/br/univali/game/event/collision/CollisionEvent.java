package br.univali.game.event.collision;

public class CollisionEvent<T> {
	private T origin;

	public CollisionEvent(T origin) {
		this.origin = origin;
	}
	
	public T getOrigin() {
		return origin;
	}
}
