package src.validation.levelchecks;

import ch.aplu.jgamegrid.Location;

import java.util.List;

/**
 * Results consisting errors (if not successful)
 * @param success if success
 * @param errors list of errors
 */
public record LevelCheckResult(boolean success, List<LevelCheckError> errors)
{
	// Success constant, does not need other arguments
	public final static LevelCheckResult SUCCESS = new LevelCheckResult(true, null);
	public record LevelCheckError(String errorMessage, List<Location> errorLocations) {}
}
