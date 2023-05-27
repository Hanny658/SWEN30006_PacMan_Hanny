package src.game.levelchecks;

import ch.aplu.jgamegrid.Location;
import src.models.Entity;

import java.util.Map;

public interface LevelCheck
{
	LevelCheckResult check(Map<Entity, Location> entities);
}
