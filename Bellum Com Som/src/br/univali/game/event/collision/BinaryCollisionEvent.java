package br.univali.game.event.collision;

/**
 * Descreve um evento de colisão entre dois objetos do jogo. 
 * @param <T> Objeto origem da colisão
 * @param <U> Objeto alvo da colisão
 */
public class BinaryCollisionEvent<T, U> extends CollisionEvent<T> {
	private U target;
	
	public BinaryCollisionEvent(T origin, U target) {
		super(origin);
		this.target = target;
	}
	
	public U getTarget() {
		return target;
	}
}
