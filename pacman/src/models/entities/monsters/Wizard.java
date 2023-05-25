/**
 * Created by Hanny Zhang (Team 08)
 */

package src.models.entities.monsters;

import ch.aplu.jgamegrid.Location;
import src.models.MonsterStates;
import src.models.entities.Monster;

import java.util.ArrayList;

// Class wrote by Hanny for extension of PiM Game
public class Wizard extends Monster
{
	static final String DEFAULT_SPRITE = "sprites/m_wizard.gif";

	public Wizard()
	{
		super(DEFAULT_SPRITE);
	}

	@Override
	protected void walkApproach()
	{
		/* Wizard is a Wall-Through Walker that walk through 1-pix wall */
		Location next = getLocation();
		Location beyond;
		Location.CompassDirection compassDir;
		// Select from the 8 positions around it randomly
		ArrayList<Location> newPositions = this.getLocation().getNeighbourLocations(1);
		ArrayList<Integer> checked = new ArrayList<>();
		Integer currentIndex = randomiser.nextInt(8);
		checked.add(currentIndex);

		while (true)
		{
			if (checked.contains(currentIndex) && checked.size() <= 8)
			{
				// If same position generated, regenerate!
				currentIndex = randomiser.nextInt(8);
			}
			else
			{
				break; // escape from infinity loop
			}
			next = newPositions.get(currentIndex);
			compassDir = getLocation().getCompassDirectionTo(next);
			checked.add(currentIndex);
			if (!canMove(next))
			{
				// If selected one is Wall, check block beyond the wall
				beyond = next.getNeighbourLocation(compassDir);
				if (canMove(beyond))
				{
					next = beyond;
					break;
				}
			}
			else if (this.canMove(next))
			{
				// if movable, just move to the selected block
				break;
			}
		}
		if (canMove(next))
		{
			// If furious, try to move further(but not through wall)
			if (this.getState() == MonsterStates.FURIOUS)
			{
				double furtherDirection = this.getLocation().get4CompassDirectionTo(next).getDirection();
				moveTo(furtherDirection);
			}
			this.setLocation(next);
			this.addVisitedList(next);
		}
		super.walkApproach();
	}
}
