package br.univali.game.window.swing;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import br.univali.game.event.input.InputEventType;
import br.univali.game.event.input.KeyboardEvent;
import br.univali.game.event.input.MouseButton;
import br.univali.game.event.input.MouseEvent;
import br.univali.game.graphics.swing.SwingRenderer;
import br.univali.game.util.IntVec;
import br.univali.game.window.GameWindow;

public class SwingWindow extends GameWindow {
	private JFrame window;
	private JPanel mainPanel;

	public SwingWindow(String title, int width, int height) {
		super(title, width, height);
		
		window = new JFrame();
		
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(width, height));
		mainPanel.setDoubleBuffered(false);
		
		renderer = new SwingRenderer(mainPanel);
		
		window.add(mainPanel);
		window.pack();
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				mainPanel.requestFocusInWindow();
			}
		});
		
		setTitle(title);
	}

	@Override
	public void display() {
		window.setVisible(true);
	}

	@Override
	public IntVec getMousePosition() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		
		SwingUtilities.convertPointFromScreen(p, mainPanel);
		return new IntVec(p.x, p.y); 
	}

	private MouseButton convertMouseButton(int swingButton) {
		if (swingButton == java.awt.event.MouseEvent.BUTTON1) {
			return MouseButton.LEFT;
		} else if (swingButton == java.awt.event.MouseEvent.BUTTON2) {
			return MouseButton.MIDDLE;
		} else if (swingButton == java.awt.event.MouseEvent.BUTTON3) {
			return MouseButton.RIGHT;
		}
		
		return null;
	}
	
	@Override
	public void onMouseEvent(Consumer<MouseEvent> action) {
		mainPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				MouseButton button = convertMouseButton(e.getButton());
				action.accept(new MouseEvent(button, InputEventType.PRESS));
			}
			
			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				MouseButton button = convertMouseButton(e.getButton());
				action.accept(new MouseEvent(button, InputEventType.RELEASE));
			}
		});
	}

	@Override
	public void onKeyboardEvent(Consumer<KeyboardEvent> action) {
		mainPanel.addKeyListener(new KeyAdapter() {
			private int adaptKey(KeyEvent e) {
				char c = e.getKeyChar();
				if (c > '0' && c < 'Z') {
					return Character.toUpperCase(c);
				} else {
					return e.getKeyCode();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				action.accept(new KeyboardEvent(adaptKey(e), InputEventType.PRESS));
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				action.accept(new KeyboardEvent(adaptKey(e), InputEventType.RELEASE));
			}
		});
	}

	@Override
	public void onCloseRequested(Runnable action) {
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				action.run();
			}
		});
	}

	@Override
	public void destroy() {
		window.setVisible(false);
		window.dispose();
	}

	@Override
	public void setTitle(String title) {
		window.setTitle(title);
	}
}
