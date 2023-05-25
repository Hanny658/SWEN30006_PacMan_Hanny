/**
 * Created by Hanny Zhang (Team 08)
 */

package src.models.entities.monsters;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.models.entities.Monster;

import java.util.ArrayList;

public class Alien extends Monster
{
	static final String DEFAULT_SPRITE = "sprites/m_alien.gif";

	public Alien()
	{
		super(DEFAULT_SPRITE);
	}

	@Override
	protected void walkApproach()
	{
		/* Alien is the Shortest Distance Finder */
		Location next = getLocation();
		// Calculate the 8 positions around it and find distance toward player
		Location pacLocation = Game.getGame().getPlayer().getLocation();
		// Alien Only detects the 8 neighbours in distance of 1
		ArrayList<Location> newPositions = this.getLocation().getNeighbourLocations(SPEED_NORM);

		int minimumDistance = gameGrid.getNbHorzCells() * gameGrid.getNbVertCells();  // max possible distance
		int currentDistance = 0;

		// Loop through to find closet point for move
		for (Location loc : newPositions)
		{
			if (!isVisited(loc) && canMove(loc))
			{
				currentDistance = loc.getDistanceTo(pacLocation);
				if (currentDistance == minimumDistance)
				{   // Half n Half
					if (randomiser.nextBoolean())
						next = loc;
				}
				else if (currentDistance < minimumDistance)
				{
					minimumDistance = currentDistance;
					next = loc;
				}
			}
		}

		this.setLocation(next);
		this.addVisitedList(next);
		super.walkApproach();
	}
}
