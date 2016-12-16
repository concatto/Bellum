package br.univali.game.window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.graphics.Renderer;
import br.univali.game.util.IntVec;

public abstract class GameWindow {
	protected Renderer renderer;
	protected int width;
	protected int height;
	protected List<Consumer<KeyboardEvent>> keyboardConsumers = new ArrayList<>();
	protected List<Consumer<MouseEvent>> mouseConsumers = new ArrayList<>();
	
	public GameWindow(String title, int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Renderer getRenderer() {
		return renderer;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * Define o título da janela atual.
	 * @param title o novo título da janela.
	 */
	public abstract void setTitle(String title);
	
	/**
	 * Torna a janela visível.
	 * Este método deve ser invocado antes de qualquer operação de desenho.
	 */
	public abstract void display();
	
	/**
	 * Obtém a posição do cursor do mouse, em coordenadas relativas
	 * ao canto esquerdo superior da tela.
	 * @return a posição do cursor na tela.
	 */
	public abstract IntVec getMousePosition();
	
	/**
	 * Define a ação a ser realizada quando qualquer botão do mouse
	 * for pressionado ou solto.
	 * @param action a ação a ser realizada.
	 */
	public void addMouseEventConsumer(Consumer<MouseEvent> action) {
		mouseConsumers.add(action);
	}
	
	/**
	 * Define a ação a ser realizada quando qualquer tecla do teclado
	 * for pressionada ou solta.
	 * @param action a ação a ser realizada.
	 */
	public void addKeyboardEventConsumer(Consumer<KeyboardEvent> action) {
		keyboardConsumers.add(action);
	}
	
	public void removeMouseEventConsumer(Consumer<MouseEvent> consumer) {
		mouseConsumers.remove(consumer);
	}
	
	public void removeKeyboardEventConsumer(Consumer<KeyboardEvent> consumer) {
		keyboardConsumers.remove(consumer);
	}
	
	/**
	 * Define a ação a ser tomada quando uma requisição de fechamento
	 * for realizada pelo usuário
	 * @param action a ação de fechamento
	 */
	public abstract void onCloseRequested(Runnable action);
	
	/**
	 * Fecha e destrói a janela atual.
	 */
	public abstract void destroy();

	public IntVec getSize() {
		return new IntVec(width, height);
	}

	/**
	 * Consulta o estado de uma tecla do teclado.
	 * @param key a tecla desejada
	 * @return <i>true</i> se estiver pressionada, <i>false</i> caso contrário
	 */
	public abstract boolean isKeyPressed(int key);
}
