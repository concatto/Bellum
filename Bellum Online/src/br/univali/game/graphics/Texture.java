package br.univali.game.graphics;

import java.util.List;

import br.univali.game.util.IntRect;
import br.univali.game.util.IntVec;

public class Texture {
	private int id;
	private IntVec size;
	private List<IntRect> frames;
	
	public Texture(int id, IntVec size, List<IntRect> frames) {
		super();
		this.id = id;
		this.size = size;
		this.frames = frames;
	}
	
	public int getId() {
		return id;
	}
	
	public IntVec getSize() {
		return size;
	}
	
	public List<IntRect> getFrames() {
		return frames;
	}
}
