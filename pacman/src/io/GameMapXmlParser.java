package src.io;

import ch.aplu.jgamegrid.Location;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import src.models.Entity;
import src.models.GameMapSchema;
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
	private static final String DEFAULT_MAP = "testamoffat";

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
		NAME_TO_ENTITY.put("PortalWhiteTile", new Portal(Portal.PortalColor.White));
		NAME_TO_ENTITY.put("PortalYellowTile", new Portal(Portal.PortalColor.Yellow));
		NAME_TO_ENTITY.put("PortalDarkGoldTile", new Portal(Portal.PortalColor.DarkGold));
		NAME_TO_ENTITY.put("PortalDarkGrayTile", new Portal(Portal.PortalColor.DarkGray));
	}

	/** Load to a map of entities and their locations from XML */
	public static Map<Location, Entity> loadEntityFromXml(String fileName)
	{
		try
		{
			File xmlFile = new File(fileName);

			JAXBContext jaxbContext = JAXBContext.newInstance(GameMapSchema.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			GameMapSchema map = (GameMapSchema) unmarshaller.unmarshal(xmlFile);
			return getEntitiesAndLocations(map);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static Map<Location, Entity> getEntitiesAndLocations(GameMapSchema map)
	{
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
			}
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return entities;
	}
}
