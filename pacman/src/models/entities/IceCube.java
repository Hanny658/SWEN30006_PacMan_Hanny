/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 */

package src.models.entities;

import src.Game;
import src.io.LogManager;
import src.models.Collidable;
import src.models.Consumable;
import src.models.Entity;

public class IceCube extends Entity implements Collidable, Consumable
{
	private static final String LOG_NAME = "ice";
	private static final String DEFAULT_SPRITE = "sprites/ice.png";

	public IceCube()
	{
		super(DEFAULT_SPRITE);
	}

	@Override
	public void consumed()
	{
		LogManager.getGameCallback().pacManEatPillsAndItems(this.getLocation(), LOG_NAME);
		this.hide();
	}

	@Override
	public Object clone() throws CloneNotSupportedException { return new IceCube(); }
}
