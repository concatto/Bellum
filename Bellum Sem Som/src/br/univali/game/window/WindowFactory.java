package br.univali.game.window;

import br.univali.game.window.opengl.OpenGLWindow;
import br.univali.game.window.swing.SwingWindow;

public class WindowFactory {	
	public static GameWindow createWindow(RenderMode type, String title, int width, int height) {
		switch (type) {
		case OPENGL:
			return new OpenGLWindow(title, width, height);
		case SWING:
			return new SwingWindow(title, width, height);
		}
		
		return null;
	}
	
}
