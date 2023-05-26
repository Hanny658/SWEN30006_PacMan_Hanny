/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 */

package src.models.entities;

import src.Game;
import src.controllers.LogController;
import src.models.Collidable;
import src.models.Consumable;
import src.models.Entity;

public class Pill extends Entity implements Collidable, Consumable
{
	private static final String LOG_NAME = "pills";
	private static final String DEFAULT_SPRITE = "sprites/pill.png";
	private final int PILL_SCORE = 1;

	public Pill()
	{
		super(DEFAULT_SPRITE);
	}

	@Override
	public void consumed()
	{
		LogController.getGameCallback().pacManEatPillsAndItems(this.getLocation(), LOG_NAME);
		Game.getGame().changeScore(PILL_SCORE);
		Game.getGame().changeNumPillsEaten(1);
		this.hide();
	}

	@Override
	public Object clone() throws CloneNotSupportedException { return new Pill(); }
}
