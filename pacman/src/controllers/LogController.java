/**
 * Created by Stephen Zhang (Team 08)
 * <p>
 * Wraps GameCallback to prevent modifying it directly
 *
 * @see src.controllers.LogController
 * @see src.controllers.GameCallback
 */

package src.controllers;

/**
 * A static class used to reveal GameCallback available for any class at any point
 * <p>
 * This is a wrapper class intended not to touch the original GameCallback class.
 *
 * @see GameCallback
 */
public class LogController
{
	private static GameCallback _callback = null;

	public static GameCallback getGameCallback()
	{
		return LogController._callback;
	}

	public static void setGameCallback(GameCallback callback)
	{
		LogController._callback = callback;
	}
}
