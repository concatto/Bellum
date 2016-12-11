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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.univali.game.server.GameServer;
import br.univali.game.window.RenderMode;

public class Application {
	public Application() {
		JDialog dialog = new JDialog((Dialog) null, "Bellum");

		JRadioButton normal = new JRadioButton("Normal");
		JRadioButton halloween = new JRadioButton("Halloween");

		JPanel themeRadioPanel = new JPanel();
		themeRadioPanel.setLayout(new BoxLayout(themeRadioPanel, BoxLayout.Y_AXIS));
		themeRadioPanel.add(normal);
		themeRadioPanel.add(halloween);
		themeRadioPanel.setBorder(BorderFactory.createTitledBorder("Tema"));
		themeRadioPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		JPanel themeContainer = new JPanel();
		themeContainer.add(themeRadioPanel);
		themeContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		ButtonGroup themeGroup = new ButtonGroup();
		themeGroup.add(normal);
		themeGroup.add(halloween);

		themeGroup.setSelected(normal.getModel(), true);

		JRadioButton client = new JRadioButton("Cliente");
		JRadioButton server = new JRadioButton("Servidor");

		JPanel modeRadioPanel = new JPanel();
		modeRadioPanel.setLayout(new BoxLayout(modeRadioPanel, BoxLayout.Y_AXIS));
		modeRadioPanel.add(client);
		modeRadioPanel.add(server);
		modeRadioPanel.setBorder(BorderFactory.createTitledBorder("Modo"));
		modeRadioPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		JPanel modeContainer = new JPanel();
		modeContainer.add(modeRadioPanel);
		modeContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(client);
		modeGroup.add(server);

		modeGroup.setSelected(normal.getModel(), true);

		JButton openGLButton = new JButton("Iniciar com OpenGL");
		JButton swingButton = new JButton("Iniciar com Swing");

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(openGLButton);
		buttonPanel.add(swingButton);

		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

		JPanel radioContainer = new JPanel();
		radioContainer.add(themeContainer);
		radioContainer.add(modeContainer);
		
		root.add(radioContainer);
		root.add(buttonPanel);

		ActionListener listener = e -> {
			String folder = themeGroup.getSelection() == normal.getModel() ? "regular" : "halloween";
			RenderMode mode = e.getSource() == openGLButton ? RenderMode.OPENGL : RenderMode.SWING;
			
			Runnable r;
			if (modeGroup.getSelection() == server.getModel()) {
				r = () -> new GameServer(mode, folder);
			} else {
				r = () -> new GameClient(mode, folder);
			}
			
			dialog.dispose();
			
			new Thread(r).start();
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

		SwingUtilities.invokeLater(() -> {
			new Application();
		});
	}

}
