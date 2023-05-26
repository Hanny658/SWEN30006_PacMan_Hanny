/**
 * Created by Stephen Zhang (Team 08)
 * <p>
 * Partially implements Event model
 */

package src.models;

import ch.aplu.jgamegrid.Location;
import src.controllers.CollisionManager;

/**
 * @see CollisionManager
 */
public interface Collidable
{
	void onCollide(Collidable collidable);

	Location getCollidableLocation();
}
