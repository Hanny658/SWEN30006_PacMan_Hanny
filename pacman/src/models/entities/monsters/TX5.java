/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Separated from Monster/MonsterType
 */

package src.models.entities.monsters;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.models.entities.Monster;

public class TX5 extends Monster
{
	static final String DEFAULT_SPRITE = "sprites/m_tx5.gif";

	public TX5()
	{
		super(DEFAULT_SPRITE);
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
}
