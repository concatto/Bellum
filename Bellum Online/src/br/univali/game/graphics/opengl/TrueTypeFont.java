package br.univali.game.graphics.opengl;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glPopMatrix;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

/**
 * @author Sri Harsha Chilakapati
 */
public class TrueTypeFont
{
    public static final int STYLE_NORMAL = Font.PLAIN;
    public static final int STYLE_BOLD   = Font.BOLD;
    public static final int STYLE_ITALIC = Font.ITALIC;

    private static final int STANDARD_CHARACTERS = 256;

    private FontChar[] chars = new FontChar[STANDARD_CHARACTERS];

    private boolean antiAlias = true;

    private GLTexture[]   fontTexture;
    private Font        awtFont;
    private FontMetrics fontMetrics;

    public TrueTypeFont(String name, int style, int size)
    {
        this(new Font(name, style, size));
    }

    public TrueTypeFont(Font fnt)
    {
        this(fnt, true);
    }

    public TrueTypeFont(Font fnt, boolean antiAlias)
    {
        this.awtFont = fnt;
        this.antiAlias = antiAlias;

        createSet();
    }

    private void createSet()
    {
        // A temporary BufferedImage to get access to FontMetrics
        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tmp.createGraphics();

        g2d.setFont(awtFont);

        if (antiAlias)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        fontMetrics = g2d.getFontMetrics();

        int positionX = 0;
        int positionY = 0;

        int page = 0;

        final int padding = fontMetrics.getMaxAdvance();
        final int maxTexWidth = 1024;
        final int maxTexHeight = 1024;

        List<GLTexture> pages = new ArrayList<>();

        for (int i = 0; i < STANDARD_CHARACTERS; i++)
        {
            char ch = (char) i;
            chars[i] = new FontChar();

            if (positionX + 2 * padding > maxTexWidth)
            {
                positionX = 0;
                positionY += fontMetrics.getHeight() + padding;
            }

            if (positionY + 2 * padding > maxTexHeight)
            {
                positionX = positionY = 0;
                page++;
            }

            chars[i].advance = fontMetrics.stringWidth("_" + ch) - fontMetrics.charWidth('_');
            chars[i].padding = padding;
            chars[i].page = page;

            chars[i].x = positionX;
            chars[i].y = positionY;
            chars[i].w = chars[i].advance + (2 * padding);
            chars[i].h = fontMetrics.getHeight();

            positionX += chars[i].w + 10;
        }

        g2d.dispose();

        BufferedImage pageImage = new BufferedImage(maxTexWidth, maxTexHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = pageImage.createGraphics();

        g2d.setFont(awtFont);
        g2d.setColor(java.awt.Color.WHITE);

        if (antiAlias)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        page = 0;

        for (int i = 0; i < STANDARD_CHARACTERS; i++)
        {
            FontChar fntChar = chars[i];

            if (page != fntChar.page)
            {
                g2d.dispose();
                pages.add(GLImageLoader.fromBufferedImage(pageImage));
                
                pageImage = new BufferedImage(maxTexWidth, maxTexHeight, BufferedImage.TYPE_INT_ARGB);
                
                g2d = pageImage.createGraphics();

                g2d.setFont(awtFont);
                g2d.setColor(java.awt.Color.WHITE);

                if (antiAlias) {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                }

                page = fntChar.page;
            }

            g2d.drawString(String.valueOf((char) i), chars[i].x + padding, chars[i].y + fontMetrics.getAscent());
        }

        g2d.dispose();

        pages.add(GLImageLoader.fromBufferedImage(pageImage));

        fontTexture = new GLTexture[pages.size()];
        fontTexture = pages.toArray(fontTexture);
    }

    public TrueTypeFont(InputStream is)
    {
        this(is, true);
    }

    public TrueTypeFont(InputStream is, boolean antiAlias)
    {
        this(is, STYLE_NORMAL, 18, antiAlias);
    }

    public TrueTypeFont(InputStream is, int style, int size, boolean antiAlias)
    {
        try {
			this.awtFont = Font.createFont(Font.TRUETYPE_FONT, is);
	        this.antiAlias = antiAlias;
	
	        awtFont = awtFont.deriveFont(style, (float) size);
	
	        createSet();
        } catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
    }

    public void drawString(String text, float x, float y, GLRenderer renderer)
    {
    	GL11.glEnable(GL_BLEND); 
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL_TEXTURE_2D);
		GL11.glPushMatrix();

            float startX = x;

            GLTexture charPage = null;
            GLTexture page = null;

            for (char ch : text.toCharArray()) {
                FontChar c = chars[(int) ch];

                if (ch == '\n')
                {
                    y += fontMetrics.getHeight();
                    x = startX;

                    continue;
                }

                charPage = fontTexture[chars[ch].page];
                
                if (page == null || page != charPage)
                {
                    page = charPage;
                    page.bind();
                }
                
                GL11.glBegin(GL11.GL_QUADS);
                

                float minU = c.x / (float) page.getImageWidth();
                float maxU = (c.x + c.w) / (float) page.getImageWidth();
                float minV = c.y / (float) page.getImageHeight();
                float maxV = (c.y + c.h) / (float) page.getImageHeight();
                
                //System.out.println("x = " + c.x + ", y = " + c.y + ", w = " + c.w + ", h = " + c.h);
                GL11.glTexCoord2f(minU, minV);
                GL11.glVertex2f(x - c.padding, y);
                GL11.glTexCoord2f(maxU, minV);
                GL11.glVertex2f(x + c.w - c.padding, y);
                
                GL11.glTexCoord2f(maxU, maxV);
                GL11.glVertex2f(x + c.w - c.padding, y + c.h);
                
                GL11.glTexCoord2f(minU, maxV);
                GL11.glVertex2f(x - c.padding, y + c.h);

                GL11.glEnd();
                
    			x += c.advance;
            }
        
        glPopMatrix();
		glDisable(GL_TEXTURE_2D);
    }

    public int getWidth(String str)
    {
        int width = 0;
        int lineWidth = 0;

        for (char ch : str.toCharArray())
        {
            if (ch == '\n')
            {
                width = Math.max(width, lineWidth);
                lineWidth = 0;
                continue;
            }

            lineWidth += chars[(int) ch].advance;
        }

        width = Math.max(width, lineWidth);

        return width;
    }

    public TrueTypeFont derive(float size)
    {
        return new TrueTypeFont(awtFont.deriveFont(size));
    }

    public int getHeight()
    {
        return fontMetrics.getHeight();
    }

    private static class FontChar
    {
        public int x;
        public int y;
        public int w;
        public int h;
        public int advance;
        public int padding;
        public int page;
    }
}