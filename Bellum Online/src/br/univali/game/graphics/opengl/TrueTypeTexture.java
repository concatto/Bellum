package br.univali.game.graphics.opengl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/**
 * @author Sri Harsha Chilakapati
 */
public class TrueTypeTexture
{
    private static int     activeUnit;
    public static  TrueTypeTexture CURRENT;
    public static  TrueTypeTexture EMPTY;
    private        int     id;
    private        float   width;
    private        float   height;
    private        boolean disposed;

    public TrueTypeTexture()
    {
        id = glGenTextures();
        
    }

    public TrueTypeTexture(int id)
    {
        this.id = id;
    }

    public static int getActiveUnit()
    {
        return activeUnit;
    }

    public static void setActiveUnit(int unit)
    {
        if (unit == activeUnit)
            return;

        
        glActiveTexture(GL_TEXTURE0 + unit);
        

        activeUnit = unit;
    }

    public static TrueTypeTexture fromColor(Color c, int width, int height)
    {
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                buffer.put((byte) (c.getRed() * 255f))
                        .put((byte) (c.getGreen() * 255f))
                        .put((byte) (c.getBlue() * 255f))
                        .put((byte) (c.getAlpha() * 255f));
            }
        }

        buffer.flip();

        return fromByteBuffer(buffer, width, height);
    }

    public static TrueTypeTexture fromByteBuffer(ByteBuffer buffer, int width, int height)
    {
        TrueTypeTexture texture = new TrueTypeTexture();

        texture.bind();
        texture.setFilter(GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR);
        texture.image2d(buffer, GL_UNSIGNED_BYTE, GL_RGBA, width, height, GL_RGBA8);
        texture.generateMipMaps();

        return texture;
    }

    public void bind()
    {
        if (CURRENT == this)
            return;

        if (disposed)
            throw new RuntimeException("Cannot bind a disposed texture!");

        glBindTexture(GL_TEXTURE_2D, id);
        

        CURRENT = this;
    }

    public void setFilter(int min, int mag)
    {
        bind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, min);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mag);
        
    }

    public void image2d(ByteBuffer data, int type, int format, int width, int height, int internalFormat)
    {
        bind();

        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, data);
        

        this.width = width;
        this.height = height;
    }

    public void generateMipMaps()
    {
        bind();
        glGenerateMipmap(GL_TEXTURE_2D);
        
    }

    public static TrueTypeTexture fromBufferedImage(BufferedImage img)
    {
        ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);

        for (int y = 0; y < img.getHeight(); y++)
        {
            for (int x = 0; x < img.getWidth(); x++)
            {
                // Select the pixel
                int pixel = img.getRGB(x, y);
                // Add the RED component
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                // Add the GREEN component
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                // Add the BLUE component
                buffer.put((byte) (pixel & 0xFF));
                // Add the ALPHA component
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.rewind();

        return fromByteBuffer(buffer, img.getWidth(), img.getHeight());
    }

    public void setWrapping(int s)
    {
        bind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, s);
        
    }

    public void setWrapping(int s, int t)
    {
        bind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, s);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, t);
        
    }

    public void setWrapping(int s, int t, int r)
    {
        bind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, s);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, t);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, r);
        
    }

    public ByteBuffer getImage2D(int format)
    {
        return getImage2D(format, GL_FLOAT);
    }

    public ByteBuffer getImage2D(int format, int type)
    {
        int size = 4;

        switch (format)
        {
            case GL_RGB:
            case GL_BGR:
                size = 3;
                break;
        }

        size = (int) (size * width * height * 4);

        ByteBuffer data = BufferUtils.createByteBuffer(size);
        return getImage2D(format, type, data);
    }

    public ByteBuffer getImage2D(int format, int type, ByteBuffer data)
    {
        glGetTexImage(GL_TEXTURE_2D, 0, format, type, data);
        

        return data;
    }

    public void dispose()
    {
        EMPTY.bind();
        
        glDeleteTextures(id);
        
        disposed = true;
    }

    public int getId()
    {
        return id;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public float getMinU()
    {
        return 0;
    }

    public float getMaxU()
    {
        return 1;
    }

    public float getMinV()
    {
        return 0;
    }

    public float getMaxV()
    {
        return 1;
    }

    public boolean isDisposed()
    {
        return disposed;
    }
}