/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Made abstract and reduced responsibilities
 */

package src.models.entities;

import ch.aplu.jgamegrid.Location;
import src.game.CollisionManager;
import src.io.LogManager;
import src.models.Collidable;
import src.models.Entity;

public abstract class Monster extends Entity implements Collidable
{
	protected static final int SPEED_NORM = 1;

	public Monster(String spriteFile)
	{
		super(spriteFile);

		// Default speed for monsters
		this.setSpeed(SPEED_NORM);
	}

	@Override
	public Location getCollidableLocation()
	{
		return this.getLocation();
	}

	@Override
	public void act()
	{
		if (getSpeed() == STILL_SPEED)
			return;

		walkApproach();
		setHorzMirror(!(getDirection() > 150) || !(getDirection() < 210));
		CollisionManager.detectCollision(this, this.gameGrid);
	}

	/**
	 * This super method reports location change to GameCallBack
	 * so super.walkApproach() should be the last function to call
	 */
	protected void walkApproach()
	{
		// Report location change
		LogManager.getGameCallback().monsterLocationChanged(this);
	}
}
