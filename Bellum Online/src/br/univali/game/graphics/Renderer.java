package br.univali.game.graphics;

import java.awt.Font;

import br.univali.game.util.FloatVec;
import br.univali.game.util.IntRect;

public interface Renderer {
	void clear();
	void setColor(float red, float green, float blue);
	void setColor(float red, float green, float blue, float alpha);
	//void drawCircle(float x, float y, float radius);
	void drawRectangle(float x, float y, float width, float height);
	//void drawLine(int x1, int x2, int y1, int y2);
	//void drawLine(int x1, int x2, int y1, int y2, int thickness);
	
	void setFont(Font font);
	void drawText(String text, float x, float y);
	void setScale(FloatVec scale);
	void setRotation(float radians);
	void draw();
	
	void drawTexture(Texture texture, float x, float y);
	void drawTexture(Texture texture, float x, float y, float alpha);
	
	void drawTextureFrame(Texture texture, float x, float y, IntRect frame);
	void drawTextureFrame(Texture texture, float x, float y, IntRect frame, float alpha);
}
