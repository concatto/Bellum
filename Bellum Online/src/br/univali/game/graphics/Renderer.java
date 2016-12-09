package br.univali.game.graphics;

import java.awt.Font;
import java.io.IOException;

import br.univali.game.util.FloatVec;
import br.univali.game.util.IntRect;
import br.univali.game.util.IntVec;

public interface Renderer {
	void clear();
	void setColor(float red, float green, float blue);
	void setColor(float red, float green, float blue, float alpha);
	//void drawCircle(float x, float y, float radius);
	void drawRectangle(float x, float y, float width, float height);
	//void drawLine(int x1, int x2, int y1, int y2);
	//void drawLine(int x1, int x2, int y1, int y2, int thickness);
	int loadImage(String path) throws IOException;
	void drawImage(int image, float x, float y);
	void drawImage(int image, float x, float y, float alpha);
	void drawSubImage(int image, float x, float y, IntRect rect);
	void setFont(Font font);
	void drawText(String text, float x, float y);
	IntVec getImageSize(int image);
	void setScale(FloatVec scale);
	void setRotation(float radians);
	void draw();
}
