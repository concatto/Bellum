package br.univali.game;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
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

		JRadioButton clientSwing = new JRadioButton("Cliente (Swing)");
		JRadioButton clientGL = new JRadioButton("Cliente (OpenGL)");
		JRadioButton server = new JRadioButton("Servidor");

		JPanel modeRadioPanel = new JPanel();
		modeRadioPanel.setLayout(new BoxLayout(modeRadioPanel, BoxLayout.Y_AXIS));
		modeRadioPanel.add(clientSwing);
		modeRadioPanel.add(clientGL);
		modeRadioPanel.add(server);
		modeRadioPanel.setBorder(BorderFactory.createTitledBorder("Modo"));
		modeRadioPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(clientSwing);
		modeGroup.add(clientGL);
		modeGroup.add(server);

		modeGroup.setSelected(clientSwing.getModel(), true);

		JButton startButton = new JButton("Iniciar Jogo");

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(startButton);

		JPanel root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));

		root.add(modeRadioPanel);
		root.add(Box.createRigidArea(new Dimension(0, 16)));
		root.add(buttonPanel);

		ActionListener listener = e -> {			
			Runnable r;
			ButtonModel selection = modeGroup.getSelection();
			if (selection == server.getModel()) {
				r = () -> new GameServer(false);
			} else {
				RenderMode mode = selection == clientSwing.getModel() ? RenderMode.SWING : RenderMode.OPENGL;
				r = () -> new GameClient(mode);
			}
			
			dialog.dispose();
			
			new Thread(r,"Game launcher, at Application.java").start(); //Saindo da Event Dispatch Thread
		};

		startButton.addActionListener(listener);

		dialog.setContentPane(root);
		dialog.pack();
		dialog.setLocationRelativeTo(null);

		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	public static void runServer(){
		new GameServer(true);
	}

	public static void main(String[] args) {
		for (String a : args) {
			if ( a.equals("console") ){
				runServer();
				return;
			}
		}
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
