package br.univali.game;

import java.util.Random;

public abstract class NameGenerator {
	private static final Random rng = new Random();
	private static final String[] adjectives = {
			"Sleepy", "Happy", "Tired", "Festive", "Thoughtful",
			"Sad", "Serene", "Hopeful", "Merciful", "Angry",
			"Vengeful", "Fearless", "Brave", "Kind"
	};
	
	private static final String[] nouns = {
			"Wolf", "Cat", "Raven", "Shark", "Vampire",
			"Wizard", "Warrior", "Soldier", "Ghost", "Spirit",
			"Beast", "King", "Traveler", "Doctor"
	};
	
	public static String getRandomName() {
		int adjIndex = rng.nextInt(adjectives.length);
		int nounIndex = rng.nextInt(nouns.length);
		
		return adjectives[adjIndex] + " " + nouns[nounIndex];
	}
}
