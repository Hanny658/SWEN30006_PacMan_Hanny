/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 *
 * @see src.game.CollisionManager
 */

package src.game;

import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import src.models.Collidable;

import java.util.ArrayList;
import java.util.List;

/**
 * A static class handling collision
 * <p>
 * Node that the collision detection system in the JGameGrid library is not working as intended,
 * probably due to game objects having bigger size than the grid size, hence this simpler location-based
 * collision detection system is used throughout the game.
 *
 * @see Collidable
 */
public class CollisionManager
{
	/**
	 * Gets a list of Collidables that are at the given Location in the given GameGrid
	 *
	 * @param atLocation the location to detect
	 * @param grid       the game instance in which to detect
	 * @return A list of colliding Collidables at the location
	 */
	public static List<Collidable> getAllCollidingsAt(Location atLocation, GameGrid grid)
	{
		// Get all collidable objects ready for detection
		ArrayList<Collidable> collidables = new ArrayList<>();

		// If they collide at the given location, return them
		for (var actor : grid.getActors())
		{
			if (actor instanceof Collidable && actor.isVisible())
				if (((Collidable) actor).getCollidableLocation().equals(atLocation))
					collidables.add((Collidable) actor);
		}

		return collidables;
	}

	/**
	 * Handles colliding event for a given Collidable in the given GameGrid
	 * <p>
	 * A call to this function will trigger the collide() event of the given Collidable if a collision is detected.
	 * <p>
	 * This should be called every simulation cycle in order to continuously detect collision.
	 *
	 * @param collidable the Collidable to detect collision for
	 * @param grid       the GameGrid in which other potential Collidables are
	 */
	public static void detectCollision(Collidable collidable, GameGrid grid)
	{
		// Get all collidable objects that is colliding with it
		List<Collidable> collidables = getAllCollidingsAt(collidable.getCollidableLocation(), grid);

		// Remove itself from the list
		collidables.remove(collidable);

		// The rest are the colliding objects
		for (var collidable2 : collidables)
		{
			collidable.onCollide(collidable2);
			collidable2.onCollide(collidable);
		}
	}
}
