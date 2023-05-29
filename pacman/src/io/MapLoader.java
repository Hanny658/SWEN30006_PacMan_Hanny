/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * This is refactored from PropertiesLoader and PacManGameGrid.
 * Provides more flexibility and extensibility.
 */

package src.io;

import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import ch.aplu.util.Size;
import src.game.LevelChecker;
import src.models.Entity;
import src.models.MonsterStates;
import src.models.entities.*;
import src.models.entities.monsters.TX5;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * A static class
 */
public final class MapLoader
{
	private static final Size DEFAULT_GRID_SIZE = new Size(20, 11);
	private static final String KEY_FOR_PILL = "Pills.location";
	private static final String KEY_FOR_GOLD = "Gold.location";
	private static final String KEY_FOR_ICE = "Ice.location";
	private static final String KEY_FOR_WALL = "Wall.location";

	private static final String DEFAULT_MAZE =
			"xxxxxxxxxxxxxxxxxxxx" + // 0
			"x....x....g...x....x" + // 1
			"xgxx.x.xxxxxx.x.xx.x" + // 2
			"x.x.......i.g....x.x" + // 3
			"x.x.xx.xx  xx.xx.x.x" + // 4
			"x......x    x......x" + // 5
			"x.x.xx.xxxxxx.xx.x.x" + // 6
			"x.x......gi......x.x" + // 7
			"xixx.x.xxxxxx.x.xx.x" + // 8
			"x...gx....g...x....x" + // 9
			"xxxxxxxxxxxxxxxxxxxx";  // 10

	/**
	 * Load one kind of item with given key
	 */
	private static void loadWithDefault(GameGrid grid, char key)
	{
		// Copy structure into integer array
		for (int y = 0; y < DEFAULT_GRID_SIZE.getHeight(); y++)
		{
			for (int x = 0; x < DEFAULT_GRID_SIZE.getWidth(); x++)
			{
				if (DEFAULT_MAZE.charAt(DEFAULT_GRID_SIZE.getWidth() * y + x) == key)
				{
					switch (key)
					{
						case 'x':
							Wall wall = new Wall();
							grid.addActor(wall, new Location(x, y));
							break;
						case '.':
							Pill pill = new Pill();
							grid.addActor(pill, new Location(x, y));
							break;
						case 'i':
							IceCube iceCube = new IceCube();
							grid.addActor(iceCube, new Location(x, y));
							break;
						case 'g':
							GoldPiece gold = new GoldPiece();
							grid.addActor(gold, new Location(x, y));
					}
				}
			}
		}
	}

	/**
	 * Purify the properties by remove empty values
	 */
	private static void propPurify(Properties properties)
	{
		for (Object key : properties.keySet())
		{
			if (properties.getProperty((String) key).equals(""))
			{
				properties.remove(key);
			}
		}
	}

	/**
	 * Load Item location from property
	 */
	private static ArrayList<Location> loadItems(Properties properties, String propertyName)
	{
		ArrayList<Location> itemLocations = new ArrayList<>();
		String pillsLocationString = properties.getProperty(propertyName);
		if (pillsLocationString != null)
		{
			String[] singlePillLocationStrings = pillsLocationString.split(";");
			for (String singlePillLocationString : singlePillLocationStrings)
			{
				String[] locationStrings = singlePillLocationString.split(",");
				itemLocations.add(new Location(Integer.parseInt(locationStrings[0]), Integer.parseInt(locationStrings[1])));
			}
		}
		return itemLocations;
	}

	public static boolean loadFromXml(GameGrid grid, String filename)
	{
		var gameMap = GameMapXmlParser.loadEntityFromXml(filename);
		var valid = LevelChecker.checkMap(gameMap, filename);
		if (!valid)
			return false;
		// Clear the grid with path blocks
		grid.getBg().clear(Color.lightGray);
		try
		{
			for (var entry : gameMap.getEntities().entrySet())
			{
				var location = entry.getKey();
				var entity = entry.getValue();
				grid.addActor(entity, location);
				if (entity instanceof TX5)
				{
					entity.setDirection(Location.NORTH);
					((TX5) entity).setStateForSeconds(MonsterStates.FROZEN, 5);
				}
			}
		}
		catch (Exception e)
		{
			// Who cares
		}
		grid.setPaintOrder(PacMan.class);
		return true;
	}

	public static Properties loadPropertiesFile(String propertiesFile)
	{
		try (InputStream input = new FileInputStream(propertiesFile))
		{

			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// purity by removing empty values
			propPurify(prop);

			return prop;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

}
