/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * This wraps Actor for the game and provides some common behaviours
 */

package src.models;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.models.entities.Wall;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Entity extends Actor implements Collidable, Cloneable
{
	protected static final int STILL_SPEED = 0;
	/**
	 * The sprite that is currently rendering as
	 * This value will only be used if the Entity is set to be rotatable (support by JGameGrid)
	 */
	private int _spriteId = 0;
	private boolean _isRotatable;
	private int _numSprites;
	private int _speed = STILL_SPEED;
	private final List<Location> _visitedList = new ArrayList<>();
	private final int _listLength = 10;
	protected Random randomiser = new Random(0);

	public Entity(boolean isRotatable, String spriteFilename, int numSprites)
	{
		super(isRotatable, spriteFilename, numSprites);
		this._isRotatable = isRotatable;
		this._numSprites = numSprites;
	}

	public Entity(String spriteFilename)
	{
		super(spriteFilename);
	}

	@Override
	public void act()
	{
		// Only rotate the sprites if it is set to be rotatable
		if (!_isRotatable) return;

		show(_spriteId);

		// The algorithms of rotating sprites has been simplified
		_spriteId = (_spriteId + 1) % _numSprites;
	}

	public void setSeed(int seed)
	{
		randomiser.setSeed(seed);
	}

	/**
	 * Gets the speed of this Entity
	 * <p>
	 * By default, Entity will not move, hence a 0 speed. Override if needed.
	 *
	 * @return an int of speed
	 */
	public int getSpeed() { return _speed; }

	public void setSpeed(int speed) { _speed = speed; }

	/**
	 * Gets a string name of entity for logging usage
	 *
	 * @return simple class name
	 */
	public String getType()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	public void onCollide(Collidable collidable)
	{
		// This is intended to be left empty so that Collidables which doesn't need to do anything
		// when colliding do not need to Override collide()
	}

	@Override
	public Location getCollidableLocation()
	{
		return this.getLocation();
	}

	/**
	 * Initiate a command to move to a new location
	 * Only moves when possible (i.e. not blocked by a wall)
	 *
	 * @param direction the direction to move to
	 */
	public boolean moveTo(double direction)
	{
		int distance = getSpeed();
		// Check if all the locations on the path to the given direction is clear
		for (int d = 1; d <= distance; d++)
		{
			Location path = this.getLocation().getAdjacentLocation(direction, d);
			if (!canMove(path))
				return false;
		}

		// If the path is clear, set to the final location
		Location finalLocation = this.getLocation().getAdjacentLocation(direction, distance);
		this.setLocation(finalLocation);
		return true;
	}

	protected boolean canMove(Location newLocation)
	{
		// Check if in bound
		if (newLocation.getX() >= gameGrid.getNbHorzCells() || newLocation.getX() < 0 ||
				newLocation.getY() >= gameGrid.getNbVertCells() || newLocation.getY() < 0)
		{
			return false;
		}

		for (var entity : gameGrid.getActorsAt(newLocation))
		{
			if (entity instanceof Wall)
				return false;
		}
		return true;
	}

	/** Add the passed-in location to visited list */
	public void addVisitedList(Location location)
	{
		_visitedList.add(location);
		if (_visitedList.size() == _listLength)
			_visitedList.remove(0);
	}

	/** Judge whether the passed-in location is visited for this monster */
	public boolean isVisited(Location location)
	{
		for (Location loc : _visitedList)
			if (loc.equals(location))
				return true;
		return false;
	}

	/** As a backup approach for monsters */
	public void randomMove(double oldDirection)
	{
		// Preserved from the original codebase
		int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
		// Try to turn left/right
		setDirection(oldDirection);   // everytime before making turns, reset current direction
		turn(sign * 90);
		if (!moveTo(getDirection()))
		{
			setDirection(oldDirection);
			// Try to move forward
			if (!moveTo(getDirection()))
			{
				setDirection(oldDirection);

				turn(-sign * 90);  // Try to turn right/left
				if (!moveTo(getDirection()))
				{
					setDirection(oldDirection);
					turn(180);  // Turn backward
					moveTo(getDirection());
				}
			}
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException("Clone is not supported");
	}
}
