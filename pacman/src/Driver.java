/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Code page documented and modified for the use of LogController
 */

package src;

import src.controllers.GameCallback;
import src.controllers.LogController;
import src.controllers.MapLoader;
import src.mapeditor.Editor;

import java.util.Properties;

public class Driver
{
	public static final String DEFAULT_PROPERTIES_PATH = "properties/test1.properties";

	/**
	 * Starting point
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		boolean MAYBE = true;
		Editor.run();
		if (MAYBE)
			return;
		// Load property file from terminal argument, otherwise load default property
		String propertiesPath = DEFAULT_PROPERTIES_PATH;
		if (args.length > 0)
			propertiesPath = args[0];

		final Properties properties = MapLoader.loadPropertiesFile(propertiesPath);

		// GameCallback is barely touched
		// Minimal modification is intended even though there might be better implementation
		GameCallback gameCallback = new GameCallback();

		// Logging is made available anywhere via a static wrapper
		LogController.setGameCallback(gameCallback);
		Game.initGame(properties);
	}
}

/**
 * Algorithms Are FUN!! WOW~ Tada!!! (Moffat, 2021)
 */
