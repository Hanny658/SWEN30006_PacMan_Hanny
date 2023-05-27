package src.game.levelchecks;

import ch.aplu.jgamegrid.Location;
import src.models.Entity;
import src.models.entities.PacMan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacManCheck implements LevelCheck
{
	private static final String NO_PACMAN = "no start for PacMan";
	private static final String TOO_MANY_PACMAN = "more than one start for Pacman";

	@Override
	public LevelCheckResult check(Map<Entity, Location> entities)
	{
		List<Location> pacManLocations = new ArrayList<>();
		for (var entry : entities.entrySet())
		{
			var entitity = entry.getKey();
			var location = entry.getValue();
			if (entitity instanceof PacMan)
				pacManLocations.add(location);
		}

		switch (pacManLocations.size())
		{
			case 0:
				return new LevelCheckResult(false, NO_PACMAN, null);
			case 1:
				return LevelCheckResult.SUCCESS;
			default:
				return new LevelCheckResult(false, TOO_MANY_PACMAN, pacManLocations);
		}
	}
}
