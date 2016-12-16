package br.univali.game.graphics.opengl;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import br.univali.game.graphics.GameFont;
import br.univali.game.graphics.Renderer;
import br.univali.game.graphics.Texture;
import br.univali.game.util.FloatRect;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntRect;
import br.univali.game.util.IntVec;

public class GLRenderer implements Renderer {
	private long window;
	private float rotation;
	private FloatVec scale;
	private Map<Texture, GLTexture> textureMap = new HashMap<>();
	private Map<Font, TrueTypeFont> fonts = new HashMap<>();
	private TrueTypeFont currentFont = null;
	
	public GLRenderer(long window) {
		this.window = window;
		setRotation(0);
		setScale(new FloatVec(1, 1));
		
		//Precarregamento de fontes
		for (GameFont font : GameFont.values()) {
			setFont(font);
		}
		
		currentFont = null;
	}
	
	private void applyTransforms(float x, float y, float width, float height) {
		glTranslatef(x + (width / 2), y + (height / 2), 0);
		glRotatef(rotation, 0, 0, 1);
		glTranslatef(-width / 2, -height / 2, 0);
		glScalef(scale.x, scale.y, 1);
		
		if (scale.x < 0) {
			glTranslatef(scale.x * width, 0, 0);
		}
	}
	
	@Override
	public void clear() {
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void setColor(float red, float green, float blue) {
		setColor(red, green, blue, 1);
	}
	
	@Override
	public void setColor(float red, float green, float blue, float alpha) {
		glColor4f(red, green, blue, alpha);
		glClearColor(red, green, blue, 1);
	}

	/*
	@Override
	public void drawCircle(float x, float y, float radius) {
		glPushMatrix();
		glLoadIdentity();
		
		applyTransforms(x, y, radius * 2, radius * 2);
		
		glBegin(GL_LINE_LOOP);
		{
			for (int i = 0; i <= 360; i++) {
				double xr = Math.cos(Math.toRadians(i)) * radius;
				double yr = Math.sin(Math.toRadians(i)) * radius;
				
				glVertex2f((float) xr, (float) yr);
			}
		}
		glEnd();
	}
	*/

	@Override
	public void drawRectangle(float x, float y, float width, float height) {
		glPushMatrix();
		glLoadIdentity();
		
		applyTransforms(x, y, width, height);
		
		glBegin(GL_QUADS);
		{
			glVertex2f(0, 0);
			glVertex2f(0, height);
			glVertex2f(width, height);
			glVertex2f(width, 0);
		}
		glEnd();
		glPopMatrix();
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
		rotation = (float) Math.toDegrees(radians);
	}

	@Override
	public void draw() {
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
		
		glMatrixMode(GL11.GL_MODELVIEW);
		glLoadIdentity();
	}

	@Override
	public void setScale(FloatVec scale) {
		this.scale = scale; 
	}

	@Override
	public void setFont(GameFont font) {
		Font f = font.getFont();
		if (fonts.containsKey(f)) {
			currentFont = fonts.get(f);
		} else {
			TrueTypeFont ttf = new TrueTypeFont(f);
			fonts.put(f, ttf);
			currentFont = ttf;
		}
	}

	@Override
	public void drawText(String text, float x, float y) {
		if (currentFont == null) {
			throw new RuntimeException("No font specified");
		}
		
		currentFont.drawString(text, x, y, this);
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
		glEnable(GL_BLEND); 
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		glPushMatrix();
		
		GLTexture glTex;
		if (textureMap.containsKey(texture)) {
			glTex = textureMap.get(texture);
		} else {
			glTex = GLImageLoader.fromBufferedImage(texture.getImage());
			
			textureMap.put(texture, glTex);
		}
		
		glTex.bind();
		
		float xCoef = glTex.getWidth() / glTex.getImageWidth();
		float yCoef = glTex.getHeight() / glTex.getImageHeight();
		
		FloatRect scaled = new FloatRect(frame.x * xCoef, frame.y * yCoef, 
										frame.width * xCoef, frame.height * yCoef);
		
		applyTransforms(x, y, frame.width, frame.height);
		
		glColor3f(1, 1, 1);
		glBegin(GL_QUADS);
		{
			//Anti-horário começando do canto superior esquerdo
			glTexCoord2f(scaled.x, scaled.y);
			glVertex2f(0, 0);
			
			glTexCoord2f(scaled.x, scaled.y + scaled.height);
			glVertex2f(0, frame.height);
			
			glTexCoord2f(scaled.x + scaled.width, scaled.y + scaled.height);
			glVertex2f(frame.width, frame.height);
			
			glTexCoord2f(scaled.x + scaled.width, scaled.y);
			glVertex2f(frame.width, 0);
		}
		glEnd();
		glPopMatrix();
		glDisable(GL_TEXTURE_2D);
	}

	@Override
	public IntVec computeTextSize(String text) {
		return new IntVec(currentFont.getWidth(text), currentFont.getHeight());
	}
}
