/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 */

package src.models.entities;

import src.Game;
import src.io.LogManager;
import src.models.Collidable;
import src.models.Consumable;
import src.models.Entity;

public class GoldPiece extends Entity implements Collidable, Consumable, Cloneable
{
	private static final String LOG_NAME = "gold";
	private static final String DEFAULT_SPRITE = "sprites/gold.png";
	private final int GOLD_SCORE = 5;

	public GoldPiece()
	{
		super(DEFAULT_SPRITE);
	}

	@Override
	public void consumed()
	{
		LogManager.getGameCallback().pacManEatPillsAndItems(this.getLocation(), LOG_NAME);
		Game.getGame().changeScore(GOLD_SCORE);
		Game.getGame().changeNumPillsEaten(1);

		// Hide instead of remove for Orion to know where it was
		this.hide();
	}

	@Override
	public Object clone() throws CloneNotSupportedException { return new GoldPiece(); }
}
