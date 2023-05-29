package src.validation.levelchecks;

import ch.aplu.jgamegrid.Location;
import src.Alistair;
import src.models.Entity;
import src.models.GameMap;
import src.models.entities.*;

import java.util.*;

/** Check whether each Gold and Pill is accessible to PacMan */
public class GPAccessibilityCheck implements LevelCheck
{
	private static final String GOLD_NOT_ACCESSIBLE = "Gold not accessible";
	private static final String PILL_NOT_ACCESSIBLE = "Pill not accessible";

	private boolean containsLocation(Set<Location> locations, Location location)
	{
		for (var item : locations)
			if (item.getX() == location.getX() && item.getY() == location.getY())
				return true;
		return false;
	}

	@Override
	public LevelCheckResult check(GameMap gameMap)
	{
		Location pacLocation = null;
		var entities = gameMap.getEntities();
		var mapSize = gameMap.getSize();
		// Extract pacman location
		for (var entry : entities.entrySet())
		{
			var entity = entry.getValue();
			if (entity instanceof PacMan)
			{
				pacLocation = entry.getKey();
				break;
			}
		}
		assert pacLocation!=null;

		// Get through all the reachable positions from Pacman
		Queue<Location> queue = new LinkedList<>();
		Set<Location> visited = new HashSet<>();
		Set<Location> accessibleArea = new HashSet<>();

		queue.add(pacLocation);
		visited.add(pacLocation);

		while (!queue.isEmpty())
		{
			Location current = queue.poll();
			if (current.getX() >= mapSize.getWidth() || current.getX() < 0 ||
					current.getY() >= mapSize.getHeight() || current.getY() < 0)
				continue;

			// Check if the location is not in the map (path)
			if (!containsLocation(entities.keySet(), current))
			{
				// Path
				accessibleArea.add(current);

				// Expand to neighboring locations
				List<Location> neighbors = get4NeighborLocations(current);
				for (Location neighbor : neighbors)
				{
					if (!containsLocation(visited, neighbor))
					{
						accessibleArea.add(neighbor);
						queue.add(neighbor);
						visited.add(neighbor);
					}
				}
			}
			else
			{
				// NOT Path
				Entity entity = null;
				for (var entry : entities.entrySet())
				{
					var location = entry.getKey();
					var ent = entry.getValue();
					if (location.equals(current))
						entity = ent;
				}


				// If a wall encountered, stop spanning
				if (entity instanceof Wall)
				{
					continue;
				}
				else if (entity instanceof Portal)
				{
					// Find the destination location and mark it as accessible
					Location destination = tpPointOf(entities, (Portal) entity, current);

					/**
					 * To deal with situation that:
					 *  □  □  □	  Can't Go back   □  □  □  □  □  □
					 *  □  ⊛  □     <---------    □  ◌  ⊛  <-- ◕ PacMan
					 *  □  □  □ 	--- X --->	  □  □  □  □
					 *  We mark those dead-end portal as not accessible
					 * */
					if (isDeadEnd(gameMap, destination))
					{
						Alistair.observeAll("Observed Deadend!");
						continue;
					}

					accessibleArea.add(destination);

					// Expand from the destination location
					if (!containsLocation(visited, destination))
					{
						queue.add(destination);
						visited.add(destination);
					}
				}
				// It's some entity else, accessible.
				accessibleArea.add(current);

				// Expand to neighboring locations
				List<Location> neighbors = get4NeighborLocations(current);
				for (Location neighbor : neighbors)
				{
					if (!containsLocation(visited, neighbor))
					{
						queue.add(neighbor);
						visited.add(neighbor);
					}
				}
			}
		}

		List<LevelCheckResult.LevelCheckError> errors = new ArrayList<>();
		List<Location> inaccessiblePills = new ArrayList<>();
		List<Location> inaccessibleGolds = new ArrayList<>();

		// Now we have a set of accessible area
		for (var entry : entities.entrySet())
		{
			var entity = entry.getValue();
			var location = entry.getKey();
			if (entity instanceof Pill)
				if (!containsLocation(accessibleArea, location))
					inaccessiblePills.add(location);
			if (entity instanceof GoldPiece)
				if (!containsLocation(accessibleArea, location))
					inaccessibleGolds.add(location);
		}
		if (inaccessiblePills.size() > 0)
			errors.add(new LevelCheckResult.LevelCheckError(PILL_NOT_ACCESSIBLE, inaccessiblePills));
		if (inaccessibleGolds.size() > 0)
			errors.add(new LevelCheckResult.LevelCheckError(GOLD_NOT_ACCESSIBLE, inaccessibleGolds));

		if (errors.size() > 0)
			return new LevelCheckResult(false, errors);
		return LevelCheckResult.SUCCESS;
	}

	private static List<Location> get4NeighborLocations(Location current)
	{
		List<Location> neighbors = new ArrayList<>();
		int x = current.getX(), y = current.getY();
		neighbors.add(new Location(x + 1, y));
		neighbors.add(new Location(x - 1, y));
		neighbors.add(new Location(x, y + 1));
		neighbors.add(new Location(x, y - 1));
		return neighbors;
	}

	// Find the destination of given portal of given position
	private Location tpPointOf(Map<Location, Entity> entities, Portal p, Location curr)
	{
		for (var entry : entities.entrySet())
		{
			var entity = entry.getValue();
			if (entity == p) continue;
			if (entity instanceof Portal)
				if (((Portal) entity).getColor() == p.getColor())
					return entry.getKey();
		}
		return null;
	}

	/** Check if the portal is dead-ended */
	private boolean isDeadEnd(GameMap map, Location p)
	{
		List<Location> neighbors = get4NeighborLocations(p);
		for (Location neighbor : neighbors)
		{
			var locationX = neighbor.getX();
			var locationY = neighbor.getY();
			var maxX = map.getSize().getWidth();
			var maxY = map.getSize().getHeight();
			boolean locationInsideMap = locationX < maxX && locationX >= 0
					&& locationY < maxY && locationY >= 0;
			if (locationInsideMap && !(map.getEntityAt(neighbor) instanceof Wall))
				return false;
		}
		return true;
	}
}
