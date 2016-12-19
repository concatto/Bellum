package br.univali.game.window;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import br.univali.game.util.IntVec;
import br.univali.game.window.opengl.GLWindow;
import br.univali.game.window.swing.SwingWindow;

public class WindowFactory {
	private static final String instancesFile = "./instancesfile";
	
	private static int nextWindowPosition(){
		int n = -1;
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(instancesFile));
			n = dis.readInt();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(instancesFile,false));
			dos.writeInt((n+1)%4);
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}
	
	private static IntVec convertWindowPosition(int width, int height){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		switch (nextWindowPosition()){
		case 0:
			return new IntVec(0, 0);
		case 1:
			return new IntVec(
				screenSize.width - width,
				0
			);
		case 2:
			return new IntVec(
				screenSize.width - width,
				screenSize.height - height
			);
		case 3:
			return new IntVec(
				0,
				screenSize.height - height
			);
		}
		return new IntVec(
			screenSize.width/2 - width/2,
			screenSize.height/2 - height/2
		);
	}
	
	public static GameWindow createWindow(RenderMode type, String title, int width, int height)
	{	
		IntVec position = convertWindowPosition(width, height);
		switch (type) {
		case OPENGL:
			return new GLWindow(title, position, width, height);
		case SWING:
			return new SwingWindow(title, position, width, height);
		}
		
		return null;
	}
	
}
