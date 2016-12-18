package br.univali.game.util;

import java.awt.geom.Line2D;

public abstract class Geometry {
	public static boolean intersects(FloatRect a, FloatRect b) {
		return !((b.x + b.width <= a.x) || (b.y + b.height <= a.y) || (b.x >= a.x + a.width)
				|| (b.y >= a.y + a.height));
	}

	public static boolean intersects(IntRect a, IntRect b) {
		return !((b.x + b.width <= a.x) || (b.y + b.height <= a.y) || (b.x >= a.x + a.width)
				|| (b.y >= a.y + a.height));
	}

	/*public static boolean contains(IntRect external, IntRect internal) {
		return (external.x >= internal.x) && (external.y >= internal.y)
				&& (external.x + external.width <= internal.x + internal.width)
				&& (external.y + external.height <= internal.y + internal.height);
	}

	public static boolean contains(FloatRect external, FloatRect internal) {
		return (external.x >= internal.x) && (external.y >= internal.y)
				&& (external.x + external.width <= internal.x + internal.width)
				&& (external.y + external.height <= internal.y + internal.height);
	}*/
	
	public static boolean intersects(FloatVec lineStart, FloatVec lineEnd, FloatRect rect) {
		FloatVec[] vertices = vertices(rect);
		
		FloatVec[][] rectLines = {
			new FloatVec[] {vertices[0], vertices[1]},
			new FloatVec[] {vertices[1], vertices[2]},
			new FloatVec[] {vertices[2], vertices[3]},
			new FloatVec[] {vertices[3], vertices[0]},
		};
		
		for (FloatVec[] line : rectLines) {
			if (Line2D.linesIntersect(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y, line[0].x, line[0].y, line[1].x, line[1].y)) {
				return true;
			}
		}
		
		return false;
	}

	public static double euclideanDistance(FloatVec a, FloatVec b) {
		float x = (a.x - b.x);
		float y = (a.y - b.y);
		return Math.sqrt(x * x + y * y);
	}

	public static double norm(FloatVec vector) {
		return euclideanDistance(vector, new FloatVec(0, 0));
	}

	public static FloatVec centralPoint(FloatRect rect) {
		return new FloatVec(rect.x + (rect.width / 2), rect.y + (rect.height / 2));
	}
	
	public static FloatVec centralize(FloatRect subject, FloatRect context) {
		return Geometry.toTopLeft(new FloatVec(subject.width, subject.height), Geometry.centralPoint(context));
	}

	public static float angle(FloatVec source, FloatVec destination) {
		return (float) Math.atan2(destination.y - source.y, destination.x - source.x);
	}
	
	/**
	 * Retorna os vértices do retângulo, começando do canto superior
	 * esquerdo e seguindo no sentido horário.
	 * @param rect o retângulo em questão 
	 * @return os vértices de rect
	 */
	public static FloatVec[] vertices(FloatRect rect) {
		return new FloatVec[] {
			new FloatVec(rect.x, rect.y),
			new FloatVec(rect.x + rect.width, rect.y),
			new FloatVec(rect.x + rect.width, rect.y + rect.height),
			new FloatVec(rect.x, rect.y + rect.height)
		};
	}
	
	public static boolean contains(FloatVec position, FloatRect rect) {
		return (position.x >= rect.x) && (position.y >= rect.y)
				&& (position.x <= rect.x + rect.width)
				&& (position.y <= rect.y + rect.height);
	}

	public static FloatVec centerVector(FloatVec subject, FloatVec context) {
		float x = context.x / 2 - subject.x / 2;
		float y = context.y / 2 - subject.y / 2;
		return new FloatVec(x, y);
	}
	
	public static FloatVec toTopLeft(FloatVec size, FloatVec center) {
		return new FloatVec(center.x - size.x / 2, center.y - size.y / 2);
	}

	public static boolean isWithinLateralBounds(FloatRect itemBox, FloatRect windowBox) {
		return itemBox.x + itemBox.width > 0 && itemBox.x < windowBox.width;
	}
}
