/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Separated from Monster/MonsterType
 */

package src.models.entities.monsters;

import ch.aplu.jgamegrid.Location;
import src.models.entities.Monster;


public class Troll extends Monster
{
	static final String DEFAULT_SPRITE = "sprites/m_troll.gif";

	public Troll()
	{
		super(DEFAULT_SPRITE);
	}

	@Override
	protected void walkApproach()
	{
		/* Troll is a Random walker */
		Location next = new Location();
		double oldDirection = getDirection();

		// Random walk
		super.randomMove(oldDirection);
		this.addVisitedList(this.getLocation());
		super.walkApproach();
	}
}
