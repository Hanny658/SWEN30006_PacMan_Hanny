/**
 * Created by Hanny Zhang (Team 08)
 */

package src.models.entities.monsters;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import src.models.entities.GoldPiece;
import src.models.entities.Monster;

import java.util.ArrayList;

public class Orion extends Monster
{
	static final String DEFAULT_SPRITE = "sprites/m_orion.gif";
	private Location aimingGoldPos = null;

	public Orion()
	{
		super(DEFAULT_SPRITE);
	}

	@Override
	protected void walkApproach()
	{
		/* Orion is a Gold Surveillance that walks between golds */
		// If current position is the aiming-gold position, update to a new one
		if (aimingGoldPos == null || this.getLocation().equals(aimingGoldPos))
		{
			this.updateAim();
		}
		Location next = getLocation();
		ArrayList<Location> newPositions = this.getLocation().getNeighbourLocations(this.getSpeed());
		int minimumDistance = gameGrid.getNbHorzCells() * gameGrid.getNbVertCells();  // max possible distance
		int currentDistance = 0;
		// Loop through to find closet point for move
		for (Location loc : newPositions)
		{
			if (this.canMove(loc) && !isVisited(loc))
			{
				currentDistance = loc.getDistanceTo(aimingGoldPos);
				if (currentDistance < minimumDistance)
				{
					minimumDistance = currentDistance;
					next = loc;
				}
			}
		}

		if (!isVisited(next) && canMove(next))
		{
			moveTo(getLocation().getDirectionTo(next));
			addVisitedList(next);
		}
		else
		{
			randomMove(this.getDirection());
		}
	}

	/* Method that updates the orion's aiming gold */
	private void updateAim()
	{
		// Since existing gold have higher priority
		// request to game for existing gold positions and random select one
		ArrayList<Actor> goldPositions = findGolds();

		// The Arraylist could not be empty since the game should be finished in advance
		this.aimingGoldPos = goldPositions.get(randomiser.nextInt(goldPositions.size())).getLocation();
	}

	// Find all gold locations
	private ArrayList<Actor> findGolds()
	{
		ArrayList<Actor> golds = new ArrayList<>();
		for (Actor curr : gameGrid.getActors())
		{
			if (curr instanceof GoldPiece)
				// prior to visible golds
				if (curr.isVisible())
					golds.add(curr);
		}
		if (golds.size() == 0)
		{   // if all golds are eaten, our pitiful Gold Surveillance just go visit the old positions of gold
			for (Actor curr : gameGrid.getActors())
				if (curr instanceof GoldPiece)
					golds.add(curr);
		}
		return golds;
	}
}
