package br.univali.game.event.input;

@SuppressWarnings("serial")
public class KeyboardEvent extends InputEvent {	
	private int key;

	public KeyboardEvent(int key, InputEventType type) {
		super(type);
		this.key = key;
	};
	
	/**
	 * Obtém o código da tecla deste evento. Se a tecla pressionada
	 * for um dígito ou uma letra, o código será o valor ASCII
	 * do dígito ou da letra em sua forma maiúscula. Caso
	 * contrário, será uma das constantes desta classe.
	 * @return o código da tecla
	 */
	public int getKey() {
		return key;
	}
}
