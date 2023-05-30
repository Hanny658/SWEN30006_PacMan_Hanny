package src.validation.levelchecks;

import src.models.GameMap;

/**
 * An interface for easily extend level checks
 * <p>
 * Implements Command pattern
 */
public interface LevelCheck
{
	LevelCheckResult check(GameMap gameMap);
}
