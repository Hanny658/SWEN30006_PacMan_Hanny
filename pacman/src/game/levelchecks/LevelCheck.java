package src.game.levelchecks;

import ch.aplu.jgamegrid.Location;
import src.models.Entity;
import src.models.GameMapSchema;

import java.util.Map;

public interface LevelCheck
{
	LevelCheckResult check(Map<Location, Entity> entities, GameMapSchema.Size mapSize);
}
