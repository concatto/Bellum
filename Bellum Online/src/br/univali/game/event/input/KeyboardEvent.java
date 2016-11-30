package br.univali.game.event.input;

@SuppressWarnings("serial")
public class KeyboardEvent extends InputEvent {
	//Códigos de tecla utilizados pelo Swing
	public static final int LEFT = 37;
	public static final int UP = 38;
	public static final int RIGHT = 39;
	public static final int DOWN = 40;
	public static final int SPACE = 32;
	public static final int CTRL = 17;
	
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
