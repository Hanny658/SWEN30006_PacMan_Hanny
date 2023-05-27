package src.game.levelchecks;

import ch.aplu.jgamegrid.Location;

import java.util.List;

public record LevelCheckResult(boolean success, String errorMessage, List<Location> errorLocations)
{
	// Success constant, does not need other arguments
	public final static LevelCheckResult SUCCESS = new LevelCheckResult(true, null, null);
}
