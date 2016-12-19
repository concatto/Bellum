package br.univali.game.controllers;

import br.univali.game.PlayerRole;
import br.univali.game.graphics.GameFont;
import br.univali.game.graphics.Renderer;

public class HUDScoreBehaviour {
	private PlayerRole role;

	public HUDScoreBehaviour() {
		this(PlayerRole.NONE);
	}
	
	public HUDScoreBehaviour(PlayerRole role) {
		this.role = role;
	}
	
	public void apply(Renderer renderer, GameScore score) {
		String tankText = "Tank: " + score.getTankScore();
		String helicopterText = "Helicopters: " + score.getHelicoptersScore();
		
		renderer.setFont(GameFont.MEDIUM_BOLD);
		
		switch (role) {
		case TANK:
			renderer.setColor(0, 0.5f, 0);
			renderer.drawText(tankText, 400, 10);
			renderer.setColor(0.5f, 0, 0);
			renderer.drawText(helicopterText, 400, 40);
			break;
		case HELICOPTER:
			renderer.setColor(0, 0.5f, 0);
			renderer.drawText(helicopterText, 400, 10);
			renderer.setColor(0.5f, 0, 0);
			renderer.drawText(tankText, 400, 40);
			break;
		default:
			renderer.setColor(0, 0, 0);
			renderer.drawText(tankText, 400, 10);
			renderer.setColor(0, 0, 0);
			renderer.drawText(helicopterText, 400, 40);
			break;
		}
	}
}
