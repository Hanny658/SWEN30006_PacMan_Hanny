package src.game.levelchecks;

import ch.aplu.jgamegrid.Location;

import java.util.Arrays;
import java.util.List;

public record LevelCheckResult(boolean success, List<LevelCheckError> errors)
{
	// Success constant, does not need other arguments
	public final static LevelCheckResult SUCCESS = new LevelCheckResult(true, null);
	public record LevelCheckError(String errorMessage, List<Location> errorLocations) {}
}
