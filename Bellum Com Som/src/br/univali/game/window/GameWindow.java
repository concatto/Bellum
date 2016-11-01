package br.univali.game.window;

import java.util.function.Consumer;

import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.graphics.Renderer;
import br.univali.game.util.IntVec;

public abstract class GameWindow {
	protected Renderer renderer;
	protected int width;
	protected int height;
	
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
	public abstract void onMouseEvent(Consumer<MouseEvent> action);
	
	/**
	 * Define a ação a ser realizada quando qualquer tecla do teclado
	 * for pressionada ou solta.
	 * @param action a ação a ser realizada.
	 */
	public abstract void onKeyboardEvent(Consumer<KeyboardEvent> action);
	
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
}
