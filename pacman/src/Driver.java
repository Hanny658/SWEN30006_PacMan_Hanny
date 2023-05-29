/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 */

package src;

import src.io.GameCallback;
import src.io.LogManager;
import src.mapeditor.Editor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver
{
	private static final String DEFAULT_GAME_FOLDER = "test";


	/**
	 * Starting point
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			Editor.run();

			return;
		}
		//Editor.run();
		boolean testMode = false;
		String gameFolder = DEFAULT_GAME_FOLDER;
		// Load property file from terminal argument, otherwise load default property
		if (args.length > 0)
		{
			Path path = Paths.get(args[0]);

			if (Files.isDirectory(path))
			{
				// Folder: test mode (start game with folder)
				testMode = true;
				RunGame();

			}
			else if (Files.isRegularFile(path))
			{
				// File: start editor to edit this file
			}
			else
			{

			}
			// Start editor with no file
			gameFolder = args[0];
		}
	}

	public static void RunEditor()
	{
		Editor.run();
	}
	public static void RunGame()
	{
		// GameCallback is barely touched
		// Minimal modification is intended even though there might be better implementation
		// Logging is made available anywhere via a static wrapper
		LogManager.setGameCallback(new GameCallback());
		if (!Game.newGame("good"))
			return;
		var game = Game.getGame();
		game.startGame();
	}
}

/**
 * Algorithms Are FUN!! WOW~ Tada!!! (Moffat, 2021)
 */
