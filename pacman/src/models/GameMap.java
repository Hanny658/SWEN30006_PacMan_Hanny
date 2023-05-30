/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 */

package src.models;

import ch.aplu.jgamegrid.Location;
import ch.aplu.util.Size;
import src.models.entities.*;
import src.models.entities.monsters.TX5;
import src.models.entities.monsters.Troll;

import java.util.HashMap;
import java.util.Map;

/**
 * A class representing a single game map (entities and their locations)
 */
public class GameMap
{
	private static final Map<String, Entity> NAME_TO_ENTITY = new HashMap<>();

	static
	{
		NAME_TO_ENTITY.put("WallTile", new Wall());
		NAME_TO_ENTITY.put("PillTile", new Pill());
		NAME_TO_ENTITY.put("GoldTile", new GoldPiece());
		NAME_TO_ENTITY.put("IceTile", new IceCube());
		NAME_TO_ENTITY.put("PacTile", new PacMan());
		NAME_TO_ENTITY.put("TrollTile", new Troll());
		NAME_TO_ENTITY.put("TX5Tile", new TX5());
		NAME_TO_ENTITY.put("PortalWhiteTile", new Portal(Portal.PortalColor.White));
		NAME_TO_ENTITY.put("PortalYellowTile", new Portal(Portal.PortalColor.Yellow));
		NAME_TO_ENTITY.put("PortalDarkGoldTile", new Portal(Portal.PortalColor.DarkGold));
		NAME_TO_ENTITY.put("PortalDarkGrayTile", new Portal(Portal.PortalColor.DarkGray));
	}

	private final Map<Location, Entity> _entities;
	private final Size _size;

	/**
	 * Create a new game map with entities and given map sizwe
	 * @param entities the entities
	 * @param mapSize the map size
	 */
	public GameMap(Map<Location, Entity> entities, Size mapSize)
	{
		_entities = entities;
		_size = mapSize;
	}

	/**
	 * Get a map of entities (value) and their locations (key).
	 * @return a map of entities and locations
	 */
	public Map<Location, Entity> getEntities() { return _entities; }

	/**
	 * Get the entity at the given location
	 * @param atLocation the location
	 * @return the entity that is on the give location
	 */
	public Entity getEntityAt(Location atLocation)
	{
		for (var entry : getEntities().entrySet())
		{
			var location = entry.getKey();
			var entity = entry.getValue();
			if (location.equals(atLocation))
				return entity;
		}
		return null;
	}

	public Size getSize() { return _size; }

	/**
	 * Converts GameMapSchema to GameMap
	 * @param map the schema
	 * @return a game map
	 */
	public static GameMap fromGameMapSchema(GameMapSchema map)
	{
		if (map == null)
			return null;

		Map<Location, Entity> entities = new HashMap<>();
		try
		{
			int x = -1, y = -1;
			for (var row : map.getRows())
			{
				y++;
				x = -1;
				for (var cell : row.getCells())
				{
					x++;
					if (!NAME_TO_ENTITY.containsKey(cell)) continue;
					entities.put(new Location(x, y), (Entity) NAME_TO_ENTITY.get(cell).clone());
				}
				// Size mismatch
				if (x + 1 != map.getMapSize().getWidth())
					return null;
			}
			// Size mismatch
			if (y + 1 != map.getMapSize().getHeight())
				return null;
			return new GameMap(entities, map.getMapSize());
		}
		catch (Exception e)
		{
		}
		return null;
	}
}
