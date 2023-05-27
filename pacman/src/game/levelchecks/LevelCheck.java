package src.game.levelchecks;

import ch.aplu.jgamegrid.Location;
import src.models.Entity;

import java.util.List;
import java.util.Map;

public interface LevelCheck
{
	LevelCheckResult check(Map<Location, Entity> entities);
}
