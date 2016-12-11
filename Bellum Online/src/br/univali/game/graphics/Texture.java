package br.univali.game.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;

import br.univali.game.util.IntRect;
import br.univali.game.util.IntVec;

public class Texture {
	private BufferedImage image;
	private IntVec size;
	private List<IntRect> frames;
	
	public Texture(BufferedImage image, IntVec size, List<IntRect> frames) {
		this.image = image;
		this.size = size;
		this.frames = frames;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public IntVec getSize() {
		return size;
	}
	
	public List<IntRect> getFrames() {
		return frames;
	}

	public static Texture load(String path) {
		return load(path, size -> Collections.singletonList(new IntRect(0, 0, size.x, size.y)));
	}
	
	public static Texture load(String path, Function<IntVec, List<IntRect>> frameGenerator) {
		try {
			BufferedImage image = ImageIO.read(Texture.class.getClassLoader().getResource(path));
			IntVec size = new IntVec(image.getWidth(), image.getHeight());
			
			return new Texture(image, size, frameGenerator.apply(size));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
