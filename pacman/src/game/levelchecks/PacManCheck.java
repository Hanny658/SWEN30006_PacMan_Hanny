package src.game.levelchecks;

import ch.aplu.jgamegrid.Location;
import src.models.GameMap;
import src.models.entities.PacMan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Check that exactly one entry for PacMan */
public class PacManCheck implements LevelCheck
{
	private static final String NO_PACMAN = "no start for PacMan";
	private static final String TOO_MANY_PACMAN = "more than one start for Pacman";

	@Override
	public LevelCheckResult check(GameMap gameMap)
	{
		var entities = gameMap.getEntities();
		List<Location> pacManLocations = new ArrayList<>();
		for (var entry : entities.entrySet())
		{
			var location = entry.getKey();
			var entity = entry.getValue();
			if (entity instanceof PacMan)
				pacManLocations.add(location);
		}

		switch (pacManLocations.size())
		{
			case 0:
				var noPacmanError = Arrays.asList(new LevelCheckResult.LevelCheckError(NO_PACMAN, null));
				return new LevelCheckResult(false, noPacmanError);
			case 1:
				return LevelCheckResult.SUCCESS;
			default:
				var manyPacmanError = Arrays.asList(new LevelCheckResult.LevelCheckError(TOO_MANY_PACMAN, pacManLocations));
				return new LevelCheckResult(false, manyPacmanError);
		}
	}
}
