/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Separated from Monster/MonsterType
 */

package src.models.entities.monsters;

import ch.aplu.jgamegrid.Location;
import src.models.entities.Monster;
import src.models.entities.Wall;


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

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Troll new_ins = new Troll();
		new_ins.setSlowDown(3);
		return new_ins;
	}
}
