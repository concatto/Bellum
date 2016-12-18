package br.univali.game.server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class SwingServerWindow extends JFrame implements ServerWindow {
	private JButton close;
	private JTextArea area;
	private JScrollPane scrollPane;

	public SwingServerWindow() {
		super("Servidor");
		
		area = new JTextArea();
		area.setFont(new Font("Arial", Font.PLAIN, 12));
		scrollPane = new JScrollPane(area);
		
		JPanel root = new JPanel();
		
		scrollPane.setBorder(BorderFactory.createTitledBorder("Mensagens"));
		scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		
		close = new JButton("Fechar");
		
		JPanel buttonContainer = new JPanel(new FlowLayout());
		buttonContainer.add(close);
		
		
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.add(scrollPane);
		root.add(Box.createRigidArea(new Dimension(0, 10)));
		root.add(buttonContainer);
		root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		setContentPane(root);
		
		setSize(new Dimension(500, 500));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void setOnClose(Runnable action) {
		close.addActionListener(e -> action.run());
	}
	
	public void publishMessage(String message) {
		area.append(message + "\n");
		
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
	}
}
