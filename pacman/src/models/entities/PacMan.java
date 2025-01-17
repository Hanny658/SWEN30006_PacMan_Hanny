/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * Refactored from PacActor
 */

package src.models.entities;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.game.CollisionManager;
import src.models.*;

import java.lang.reflect.Array;
import java.util.*;

public class PacMan extends Entity
{
	private static final String DEFAULT_SPRITE = "sprites/pacpix.gif";
	private static final String EXPLOSION_SPRITE = "sprites/explosion3.gif";
	private static final int NUM_SPRITES = 4;
	private static final int DEFAULT_SPEED = 1;
	private static final boolean INVINCIBLE = false;
	private boolean _autoMode = false;

	public PacMan()
	{
		super(true, DEFAULT_SPRITE, NUM_SPRITES);
	}

	@Override
	public void act()
	{
		super.act();
		if (_autoMode)
		{
			moveInAutoMode();
			// At every move, detect collision before reporting status
			// so that collision detection is executed exactly once every move
			CollisionManager.detectCollision(this, this.gameGrid);
		}

		Game.getGame().reportPlayerStatus();
	}

	public void setAutoMode(boolean isAuto) { _autoMode = isAuto; }
	@Override
	public int getSpeed()
	{
		return DEFAULT_SPEED;
	}

	/** Greedy Move, just like TX5, but to the closest Gold/Pill location */
	private void moveInAutoMode()
	{
		Location closestPill = closestGPLocation();
		double oldDirection = getDirection();

		Location.CompassDirection compassDir =
				getLocation().get4CompassDirectionTo(closestPill);
		Location next = getLocation().getNeighbourLocation(compassDir);
		setDirection(compassDir);
		if (!isVisited(next) && canMove(next))
		{
			setLocation(next);
		}
		else
		{
			// normal movement
			int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
			setDirection(oldDirection);
			turn(sign * 90);  // Try to turn left/right
			next = getNextMoveLocation();
			if (canMove(next))
			{
				setLocation(next);
			}
			else
			{
				setDirection(oldDirection);
				next = getNextMoveLocation();
				if (canMove(next)) // Try to move forward
				{
					setLocation(next);
				}
				else
				{
					setDirection(oldDirection);
					turn(-sign * 90);  // Try to turn right/left
					next = getNextMoveLocation();
					if (canMove(next))
					{
						setLocation(next);
					}
					else
					{
						setDirection(oldDirection);
						turn(180);  // Turn backward
						next = getNextMoveLocation();
						setLocation(next);
					}
				}
			}
		}
		this.addVisitedList(next);
	}

	@Override
	public void onCollide(Collidable collidable)
	{
		if (collidable instanceof Consumable)
		{
			((Consumable) collidable).consumed();

			// Let the game check if all the golds/pills have been eaten up
			// every time PacMan eats something (replaces the original busy waiting behaviour)
			Game.getGame().checkWin();
		}
		else if (collidable instanceof Monster)
		{
			if (INVINCIBLE) return;
			// If PacMan hits a monster, explode and stop the game immediately and declare lost
			this.explode();
			Game.getGame().stopGame(false);
		}
	}

	/**
	 * Gets the location of the closest pill/gold from PacMan
	 * <p>
	 * Original algorithm is preserved to strictly preserve original behaviour
	 *
	 * @return a location
	 */
	private Location closestGPLocation()
	{
		int minDistance = Integer.MAX_VALUE;
		Location minLocation = null;

		// Gets a list of pill/gold
		List<Location> pillAndItemLocations = new ArrayList<>();
		for (var actor : Game.getGame().getActors())
			if (actor instanceof Consumable)
				// TODO: Check if IceCube still took in count
				pillAndItemLocations.add(actor.getLocation());

		for (Location location : pillAndItemLocations)
		{
			int distanceToPill = location.getDistanceTo(getLocation());
			if (distanceToPill < minDistance)
			{
				minLocation = location;
				minDistance = distanceToPill;
			}
		}

		return minLocation;
	}

	private void explode()
	{
		// Disable act cycle immediately to prevent it logging after game stopped
		this.setActEnabled(false);
		this.hide();

		// Create a new actor of explosion and replace itself
		Actor explosion = new Actor(EXPLOSION_SPRITE);
		Game.getGame().addActor(explosion, this.getLocation());
	}

	@Override
	public Object clone() throws CloneNotSupportedException { return new PacMan(); }
}
