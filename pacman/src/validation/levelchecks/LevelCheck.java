package src.validation.levelchecks;

import src.models.GameMap;

public interface LevelCheck
{
	LevelCheckResult check(GameMap gameMap);
}
