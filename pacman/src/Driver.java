/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 */

package src;

import src.io.GameCallback;
import src.io.LogManager;
import src.mapeditor.Editor;

import java.io.File;
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
		// No argument: editor with no map
		if (args.length == 0)
		{
			Editor.run();
			return;
		}

		// Load property file from terminal argument, otherwise load default property
		if (args.length > 0)
		{
			Path path = Paths.get(args[0]);

			if (Files.isDirectory(path))
			{
				// Folder: test mode (start game with folder)
				RunGame(String.valueOf(path), false);
			}
			else if (Files.isRegularFile(path))
			{
				// File: start editor to edit this file
				Editor.run(new File(String.valueOf(path)));
			}
			else
			{
				// Start editor with no file
				Editor.run();
			}
		}
	}

	public static void RunEditor()
	{
		Editor.run();
	}

	public static void RunEditor(String mapFilename)
	{
		Editor.run(new File(mapFilename));
	}

	public static void RunGame(String path, boolean testMap)
	{
		// GameCallback is barely touched
		// Minimal modification is intended even though there might be better implementation
		// Logging is made available anywhere via a static wrapper
		LogManager.setGameCallback(new GameCallback());
		if (!Game.newGame(path, testMap))
		{
			if (testMap)
				RunEditor(path);
			else
				RunEditor();
			return;
		}
		var game = Game.getGame();
		game.startGame();
	}
}

/**
 * Algorithms Are FUN!! WOW~ Tada!!! (Moffat, 2021)
 */
