/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Separated from Monster/MonsterType
 */

package src.models.entities.monsters;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.models.entities.Monster;
import src.models.entities.Wall;

public class TX5 extends Monster
{
	private static final String DEFAULT_SPRITE = "sprites/m_tx5.gif";
	private static final long FROZEN_TIME = 5;
	private static final int SEC_TO_MILLI = 1000;

	private long _timeToMove = 0;

	public TX5()
	{
		super(DEFAULT_SPRITE);

		// TX5 will not move at the beginning
		this.setSpeed(STILL_SPEED);
	}

	@Override
	public void act()
	{
		super.act();

		// Timer counts at the first tick of object being rendered
		if (_timeToMove == 0)
			_timeToMove = System.currentTimeMillis() + FROZEN_TIME * SEC_TO_MILLI;

		// If time to move, set to normal speed
		if (System.currentTimeMillis() >= _timeToMove)
			this.setSpeed(SPEED_NORM);
	}

	@Override
	protected void walkApproach()
	{
		/* TX5 is Aggressive Follower that keeps trying to move closer to player */
		Location pacLocation = Game.getGame().getPlayer().getLocation();
		double pacDirection = this.getLocation().get4CompassDirectionTo(pacLocation).getDirection();

		Location next = this.getLocation().getAdjacentLocation(pacDirection, getSpeed());
		if (!isVisited(next) && moveTo(pacDirection))
		{
			setDirection(pacDirection);
		}
		else
		{
			// Random walk
			super.randomMove(getDirection());
		}
		addVisitedList(this.getLocation());
		super.walkApproach();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		TX5 new_ins = new TX5();
		new_ins.setSlowDown(3);
		return new_ins;
	}
}
