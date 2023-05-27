package src.io;

import ch.aplu.jgamegrid.Location;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import src.models.Entity;
import src.models.GameMap;
import src.models.entities.*;
import src.models.entities.monsters.TX5;
import src.models.entities.monsters.Troll;

import java.io.File;
import java.util.*;

public class GameMapXmlParser
{
	private static final String SIZE_NODE = "size";
	private static final String WIDTH_NODE = "width";
	private static final String HEIGHT_NODE = "height";
	private static final String ROW_NODE = "row";
	private static final String CELL_NODE = "cell";
	private static final Map<String, Entity> NAME_TO_ENTITY = new HashMap<>();

	static
	{
		// These are because Java doesn't support having constant dictionary
		NAME_TO_ENTITY.put("WallTile", new Wall());
		NAME_TO_ENTITY.put("PillTile", new Pill());
		NAME_TO_ENTITY.put("GoldTile", new GoldPiece());
		NAME_TO_ENTITY.put("IceTile", new IceCube());
		NAME_TO_ENTITY.put("PacTile", new PacMan());
		NAME_TO_ENTITY.put("TrollTile", new Troll());
		NAME_TO_ENTITY.put("TX5Tile", new TX5());
		NAME_TO_ENTITY.put("PortalWhiteTile", new Portal(Portal.PortColor.White));
		NAME_TO_ENTITY.put("PortalYellowTile", new Portal(Portal.PortColor.Yellow));
		NAME_TO_ENTITY.put("PortalDarkGoldTile", new Portal(Portal.PortColor.DarkGold));
		NAME_TO_ENTITY.put("PortalDarkGrayTile", new Portal(Portal.PortColor.DarkGray));
	}

	public Map<Entity, Location> test1()
	{
		try
		{
			File xmlFile = new File("testamoffat");

			JAXBContext jaxbContext = JAXBContext.newInstance(GameMap.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			GameMap map = (GameMap) unmarshaller.unmarshal(xmlFile);
			return getEntitiesAndLocations(map);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private Map<Entity, Location> getEntitiesAndLocations(GameMap map)
	{
		Map<Entity, Location> entities = new HashMap<>();
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
					entities.put((Entity) NAME_TO_ENTITY.get(cell).clone(), new Location(x, y));
				}
			}
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return entities;
	}
}
