package br.univali.game;

import java.awt.Dialog;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import br.univali.game.window.RenderMode;

public class Application {
	public Application() {
		JDialog dialog = new JDialog((Dialog) null, "Bellum");

		JRadioButton normal = new JRadioButton("Normal");
		JRadioButton halloween = new JRadioButton("Halloween");

		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
		radioPanel.add(normal);
		radioPanel.add(halloween);
		radioPanel.setBorder(BorderFactory.createTitledBorder("Tema"));
		radioPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		JPanel radioContainer = new JPanel();
		radioContainer.add(radioPanel);
		radioContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		ButtonGroup group = new ButtonGroup();
		group.add(normal);
		group.add(halloween);

		group.setSelected(normal.getModel(), true);

		JButton openGLButton = new JButton("Iniciar com OpenGL");
		JButton swingButton = new JButton("Iniciar com Swing");

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(openGLButton);
		buttonPanel.add(swingButton);

		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

		root.add(radioContainer);
		root.add(buttonPanel);

		ActionListener listener = e -> {
			String folder = group.getSelection() == normal.getModel() ? "regular" : "halloween";
			RenderMode mode = e.getSource() == openGLButton ? RenderMode.OPENGL : RenderMode.SWING;

			dialog.dispose();
			
			new Thread(() -> new Game(mode, folder, NetMode.CLIENT)).start();
		};

		openGLButton.addActionListener(listener);
		swingButton.addActionListener(listener);

		dialog.setContentPane(root);
		dialog.pack();
		dialog.setLocationRelativeTo(null);

		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) {
		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		new Application();
	}

}
