package br.univali.game.graphics.opengl;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import br.univali.game.graphics.Renderer;
import br.univali.game.util.FloatRect;
import br.univali.game.util.FloatVec;
import br.univali.game.util.IntRect;
import br.univali.game.util.IntVec;

public class OpenGLRenderer implements Renderer {
	private long window;
	private float rotation;
	private FloatVec scale;
	private TextureLoader textureLoader = new TextureLoader();
	private List<Texture> textureList = new ArrayList<>();
	
	public OpenGLRenderer(long window) {
		this.window = window;
		setRotation(0);
		setScale(new FloatVec(1, 1));
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
	public int loadImage(String path) throws IOException {
		Texture tex = textureLoader.getTexture(path);
		
		int position = textureList.size();
		textureList.add(tex);
		return position;
	}

	@Override
	public void drawImage(int image, float x, float y) {
		drawImage(image, x, y, 1);
	}
	
	@Override
	public void drawImage(int image, float x, float y, float alpha) {
		glEnable(GL_BLEND); 
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		glPushMatrix();
		
		Texture tex = textureList.get(image);
		tex.bind();
		
		applyTransforms(x, y, tex.getImageWidth(), tex.getImageHeight());

		glColor4f(1, 1, 1, alpha);
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, 0);
			glVertex2f(0, 0);
			
			glTexCoord2f(0, tex.getHeight());
			glVertex2f(0, tex.getImageHeight());
			
			glTexCoord2f(tex.getWidth(), tex.getHeight());
			glVertex2f(tex.getImageWidth(), tex.getImageHeight());
			
			glTexCoord2f(tex.getWidth(), 0);
			glVertex2f(tex.getImageWidth(), 0);
		}
		glEnd();
		glPopMatrix();
		glDisable(GL_TEXTURE_2D);
		
		glColor4f(1, 1, 1, 1);
	}

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
	public void drawSubImage(int image, float x, float y, IntRect rect) {
		glEnable(GL_BLEND); 
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		glPushMatrix();
		
		Texture tex = textureList.get(image);
		tex.bind();
		
		float xCoef = tex.getWidth() / tex.getImageWidth();
		float yCoef = tex.getHeight() / tex.getImageHeight();
		
		FloatRect scaled = new FloatRect(rect.x * xCoef, rect.y * yCoef, 
										rect.width * xCoef, rect.height * yCoef);
		
		applyTransforms(x, y, rect.width, rect.height);
		
		glColor3f(1, 1, 1);
		glBegin(GL_QUADS);
		{
			//Anti-horário começando do canto superior esquerdo
			glTexCoord2f(scaled.x, scaled.y);
			glVertex2f(0, 0);
			
			glTexCoord2f(scaled.x, scaled.y + scaled.height);
			glVertex2f(0, rect.height);
			
			glTexCoord2f(scaled.x + scaled.width, scaled.y + scaled.height);
			glVertex2f(rect.width, rect.height);
			
			glTexCoord2f(scaled.x + scaled.width, scaled.y);
			glVertex2f(rect.width, 0);
		}
		glEnd();
		glPopMatrix();
		glDisable(GL_TEXTURE_2D);
	}

	@Override
	public IntVec getImageSize(int image) {
		Texture tex = textureList.get(image);
		return new IntVec(tex.getImageWidth(), tex.getImageHeight());
	}

	@Override
	public void setScale(FloatVec scale) {
		this.scale = scale; 
	}
}
