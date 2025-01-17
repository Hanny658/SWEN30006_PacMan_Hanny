/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Take over the responsibility of handling user input from Game
 *
 * @see src.game.InputManager
 */

package src.game;

import ch.aplu.jgamegrid.GGKeyRepeatListener;
import ch.aplu.jgamegrid.Location;
import src.Game;
import src.models.Entity;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A class handling input from player to control the movement of an Entity
 * <p>
 * Note this class is intended to not support any other command other than movement,
 * since PacMan should be an easy-to-play, simple game without needing to do any magic other than moving.
 */
public class InputManager implements GGKeyRepeatListener
{
	/**
	 * A static constant dictionary of mapping arrow key to direction
	 */
	private static final Map<Integer, Location.CompassDirection> KEY_TO_DIRECTION = new HashMap<>();

	static
	{
		// These are because Java doesn't support having constant dictionary
		KEY_TO_DIRECTION.put(KeyEvent.VK_LEFT, Location.WEST);
		KEY_TO_DIRECTION.put(KeyEvent.VK_UP, Location.NORTH);
		KEY_TO_DIRECTION.put(KeyEvent.VK_DOWN, Location.SOUTH);
		KEY_TO_DIRECTION.put(KeyEvent.VK_RIGHT, Location.EAST);
	}

	private Entity _entityToControl;
	private final Game _game;

	public InputManager(Game game)
	{
		this._game = game;
	}

	/**
	 * Set the entity for player to control. If set to null then any command to entity will be ignored.
	 * @param entityToControl the entity to control
	 */
	public void setEntityToControl(Entity entityToControl) { _entityToControl = entityToControl; }

	/**
	 * The function is called everytime a key has pressed
	 *
	 * @param keyCode the key code of key pressed
	 */
	@Override
	public void keyRepeated(int keyCode)
	{
		// At anytime, press ESC to go back to editor (not requested by specs)
		if (keyCode == KeyEvent.VK_ESCAPE)
			_game.returnToEditor();

		// Check if the pressed key is registered with a direction to move
		if (!KEY_TO_DIRECTION.containsKey(keyCode))
			return; // Fail fast

		if (_entityToControl == null) return;

		var direction = KEY_TO_DIRECTION.get(keyCode);
		_entityToControl.setDirection(direction);

		// Only detect collision if player actually "moves" (blocking by walls won't count)
		if (_entityToControl.moveTo(_entityToControl.getDirection()))
			CollisionManager.detectCollision(_entityToControl, _entityToControl.gameGrid);
	}
}
