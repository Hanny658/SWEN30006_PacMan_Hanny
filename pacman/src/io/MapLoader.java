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
import src.io.GameMapXmlParser;
import src.models.Entity;
import src.models.MonsterStates;
import src.models.entities.GoldPiece;
import src.models.entities.IceCube;
import src.models.entities.Pill;
import src.models.entities.Wall;
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

	/**
	 * Check if property got a value, if not, load with default
	 */
	public static void loadWithProperty(GameGrid grid, Properties properties)
	{
		// Clear the grid with path blocks
		grid.getBg().clear(Color.lightGray);

		ArrayList<Location> itemLocation;
		// Load Pills if exist in property file
		if (properties.containsKey(KEY_FOR_PILL))
		{
			itemLocation = loadItems(properties, KEY_FOR_PILL);
			for (Location loc : itemLocation)
			{
				Pill pill = new Pill();
				grid.addActor(pill, loc);
			}
		}
		else
		{
			loadWithDefault(grid, '.');
		}
		// Load gold if exist in property file
		if (properties.containsKey(KEY_FOR_GOLD))
		{
			itemLocation = loadItems(properties, KEY_FOR_GOLD);
			for (Location loc : itemLocation)
			{
				GoldPiece gold = new GoldPiece();
				grid.addActor(gold, loc);
			}
		}
		else
		{
			loadWithDefault(grid, 'g');
		}
		// Ice cubes were always default. For extension, make it possible to load if exists
		if (properties.containsKey(KEY_FOR_ICE))
		{
			itemLocation = loadItems(properties, KEY_FOR_ICE);
			for (Location loc : itemLocation)
			{
				IceCube ice = new IceCube();
				grid.addActor(ice, loc);
			}
		}
		else
		{
			loadWithDefault(grid, 'i');
		}
		// Similarly for the Wall Object
		if (properties.containsKey(KEY_FOR_WALL))
		{
			itemLocation = loadItems(properties, KEY_FOR_WALL);
			for (Location loc : itemLocation)
			{
				Wall wall = new Wall();
				grid.addActor(wall, loc);
			}
		}
		else
		{
			loadWithDefault(grid, 'x');
		}
	}

	public static void loadFromXml(GameGrid grid, String xml)
	{
		// Clear the grid with path blocks
		grid.getBg().clear(Color.lightGray);
		GameMapXmlParser parser = new GameMapXmlParser();
		try
		{
			for (var entry : parser.loadMapFromXml().entrySet())
			{
				Entity entity = entry.getKey();
				Location location = entry.getValue();
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
