package br.univali.game.graphics.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntRect;
import br.univali.game.util.IntVec;

public class SwingRenderer implements Renderer {
	private BufferedImage image;
	private Graphics2D graphics;
	private int width;
	private int height;
	private float currentRotation;
	private JPanel targetPanel;
	private FloatVec scale;
	
	public SwingRenderer(JPanel targetPanel) {
		this.targetPanel = targetPanel;
		
		Dimension d = targetPanel.getPreferredSize();
		this.width = d.width;
		this.height = d.height;
		createImage();
		setRotation(0);
		setScale(new FloatVec(1, 1));
	}
	
	private void applyTransforms(float x, float y, float width, float height) {
		AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
		transform.scale(scale.x, scale.y);
		if (scale.x < 0) {
			transform.translate(scale.x * width, 0);
		}
		
		transform.rotate(currentRotation, (width / 2), (height / 2));
		graphics.setTransform(transform);
	}
	
	private void createImage() {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
	}

	@Override
	public void clear() {
		graphics.setTransform(new AffineTransform());
		graphics.fillRect(0, 0, width, height);
	}

	@Override
	public void setColor(float red, float green, float blue) {
		setColor(red, green, blue, 1);
	}
	
	@Override
	public void setColor(float red, float green, float blue, float alpha) {
		graphics.setColor(new Color(red, green, blue, alpha));
	}

	/*
	@Override
	public void drawCircle(float x, float y, float radius) {
		graphics.fillOval(Math.round(x), Math.round(y), Math.round(radius * 2), Math.round(radius * 2));
	}
	*/

	@Override
	public void drawRectangle(float x, float y, float width, float height) {
		applyTransforms(x, y, width, height);
		graphics.fillRect(0, 0, Math.round(width), Math.round(height));
	}

	/*
	@Override
	public void drawLine(int x1, int x2, int y1, int y2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawLine(int x1, int x2, int y1, int y2, int thickness) {
		// TODO Auto-generated method stub
	}
	*/

	@Override
	public void setRotation(float radians) {
		currentRotation = radians;
	}
	
	@Override
	public void setScale(FloatVec scale) {
		this.scale = scale;
	}
	
	@Override
	public void draw() {
		Graphics g = targetPanel.getGraphics();
		if (g != null) {
			g.drawImage(image, 0, 0, width, height, null);
			g.dispose();
		}
	}

	@Override
	public void setFont(Font font) {
		graphics.setFont(font);
	}

	@Override
	public void drawText(String text, float x, float y) {
		applyTransforms(x, y, 0, 0);
		graphics.drawString(text, 0, graphics.getFontMetrics().getAscent());
	}

	@Override
	public void drawTexture(Texture texture, float x, float y) {
		drawTexture(texture, x, y, 1);
	}

	@Override
	public void drawTexture(Texture texture, float x, float y, float alpha) {
		IntVec size = texture.getSize();
		drawTextureFrame(texture, x, y, new IntRect(0, 0, size.x, size.y), alpha);
	}

	@Override
	public void drawTextureFrame(Texture texture, float x, float y, IntRect frame) {
		drawTextureFrame(texture, x, y, frame, 1);
	}
	
	@Override
	public void drawTextureFrame(Texture texture, float x, float y, IntRect frame, float alpha) {
		BufferedImage sub = texture.getImage().getSubimage(frame.x, frame.y, frame.width, frame.height);
		
		applyTransforms(x, y, frame.width, frame.height);
		
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		graphics.drawImage(sub, 0, 0, null);
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}

	@Override
	public IntVec computeTextSize(String text) {
		FontMetrics metrics = graphics.getFontMetrics();
		return new IntVec(metrics.stringWidth(text), metrics.getHeight());
	}
}
