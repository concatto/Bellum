package br.univali.game.util;

import java.util.EnumSet;

public enum Direction {	
	LEFT, RIGHT, NONE,
	UP, DOWN,
	UP_LEFT, UP_RIGHT,
	DOWN_LEFT, DOWN_RIGHT;
	
	private static final EnumSet<Direction> right = EnumSet.of(RIGHT, UP_RIGHT, DOWN_RIGHT, NONE);
	
	public static boolean isRight(Direction direction) {
		return right.contains(direction);
	}
}
