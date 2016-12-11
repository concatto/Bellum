package br.univali.game.window.opengl;

import org.lwjgl.glfw.GLFW;

import br.univali.game.Keyboard;

public abstract class GLKeyConverter {

	public static int fromGL(int key) {
		if (key >= '0' && key <= 'Z') {
			return key;
		}
		
		switch (key) {
		case GLFW.GLFW_KEY_LEFT:
			return Keyboard.LEFT;
		case GLFW.GLFW_KEY_RIGHT:
			return Keyboard.RIGHT;
		case GLFW.GLFW_KEY_UP:
			return Keyboard.UP;
		case GLFW.GLFW_KEY_DOWN:
			return Keyboard.DOWN;
		case GLFW.GLFW_KEY_LEFT_CONTROL:
		case GLFW.GLFW_KEY_RIGHT_CONTROL:
			return Keyboard.CTRL;
		case GLFW.GLFW_KEY_SPACE:
			return Keyboard.SPACE;
		case GLFW.GLFW_KEY_ENTER:
			return Keyboard.ENTER;
		}
		
		return -1;
	}

	public static int toGL(int key) {
		if (key >= '0' && key <= 'Z') {
			return key;
		}
		
		switch (key) {
		case Keyboard.LEFT:
			return GLFW.GLFW_KEY_LEFT;
		case Keyboard.RIGHT:
			return GLFW.GLFW_KEY_RIGHT;
		case Keyboard.UP:
			return GLFW.GLFW_KEY_UP;
		case Keyboard.DOWN:
			return GLFW.GLFW_KEY_DOWN;
		case Keyboard.CTRL:
			return GLFW.GLFW_KEY_LEFT_CONTROL;
		case Keyboard.SPACE:
			return GLFW.GLFW_KEY_SPACE;
		case Keyboard.ENTER:
			return GLFW.GLFW_KEY_ENTER;
		}
		
		return -1;
	}
}
