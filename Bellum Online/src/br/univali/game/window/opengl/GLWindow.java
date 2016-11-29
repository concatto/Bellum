package br.univali.game.window.opengl;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import br.univali.game.event.input.InputEventType;
import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseButton;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.graphics.opengl.GLRenderer;
import br.univali.game.util.IntVec;
import br.univali.game.window.GameWindow;

public class GLWindow extends GameWindow {
	private long window;
	private IntVec mousePosition = new IntVec(0, 0);
	
	public GLWindow(String title, int width, int height) {
		super(title, width, height);
		
		GLFWErrorCallback.createPrint(System.err).set();
		GLFW.glfwInit();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_STEREO, GLFW.GLFW_FALSE);
		
		long monitor = GLFW.glfwGetPrimaryMonitor();
		
		@SuppressWarnings("resource")
		GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
		
		window = GLFW.glfwCreateWindow(width, height, "", MemoryUtil.NULL, MemoryUtil.NULL);
		GLFW.glfwSetWindowPos(window, mode.width() / 2 - width / 2, mode.height() / 2 - height / 2);
		
		renderer = new GLRenderer(window);
		
		GLFW.glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double x, double y) {
				mousePosition = new IntVec((int) x, (int) y);
			}
		});
		
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		setTitle(title);
	}
	
	@Override
	public void display() {
		GLFW.glfwShowWindow(window);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, -1, 1);
	}

	@Override
	public IntVec getMousePosition() {		
		return mousePosition;
	}

	@Override
	public void onMouseEvent(Consumer<MouseEvent> mouseAction) {
		GLFW.glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				InputEventType type = (action != GLFW.GLFW_RELEASE) ? InputEventType.PRESS : InputEventType.RELEASE; 
				
				MouseButton b = null;
				if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					b = MouseButton.LEFT;
				} else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					b = MouseButton.RIGHT;
				} else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
					b = MouseButton.MIDDLE;
				}
				
				mouseAction.accept(new MouseEvent(b, type));
			}
		});
	}

	@Override
	public void onKeyboardEvent(Consumer<KeyboardEvent> keyboardAction) {
		GLFW.glfwSetKeyCallback(window, new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				InputEventType type = (action != GLFW.GLFW_RELEASE) ? InputEventType.PRESS : InputEventType.RELEASE;
				
				if (key < '0' || key > 'Z') {
					key = convertKey(key);
				}
				
				keyboardAction.accept(new KeyboardEvent(key, type));
			}
		});
	}

	private static int convertKey(int key) {
		switch (key) {
		case GLFW.GLFW_KEY_LEFT:
			return KeyboardEvent.LEFT;
		case GLFW.GLFW_KEY_RIGHT:
			return KeyboardEvent.RIGHT;
		case GLFW.GLFW_KEY_UP:
			return KeyboardEvent.UP;
		case GLFW.GLFW_KEY_DOWN:
			return KeyboardEvent.DOWN;
		case GLFW.GLFW_KEY_LEFT_CONTROL:
		case GLFW.GLFW_KEY_RIGHT_CONTROL:
			return KeyboardEvent.CTRL;
		case GLFW.GLFW_KEY_SPACE:
			return KeyboardEvent.SPACE;
		}
		
		return -1;
	}

	@Override
	public void onCloseRequested(Runnable action) {
		GLFW.glfwSetWindowCloseCallback(window, new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				action.run();
			}
		});
	}
	
	@Override
	public void destroy() {
		GLFW.glfwDestroyWindow(window);
	}

	@Override
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(window, title);
	}
}
