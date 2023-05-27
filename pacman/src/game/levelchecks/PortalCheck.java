package src.game.levelchecks;

import ch.aplu.jgamegrid.Location;
import ch.aplu.util.Size;
import src.models.Entity;
import src.models.GameMapSchema;
import src.models.entities.Portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Check whether portals are in pairs */
public class PortalCheck implements LevelCheck
{
	private static final String FAIL_MSG = "portal %s count is not 2";
	@Override
	public LevelCheckResult check(Map<Location, Entity> entities, GameMapSchema.Size mapSize)
	{
		Map<Portal.PortalColor, List<Location>> portals = new HashMap<>();
		List<LevelCheckResult.LevelCheckError> errors = new ArrayList<>();
		for (var entry : entities.entrySet())
		{
			var location = entry.getKey();
			var entity = entry.getValue();
			if (entity instanceof Portal)
			{
				var color = ((Portal) entity).getColor();

				// TODO: Test
				var list = portals.get(color);
				if (list == null)
				{
					portals.put(color, new ArrayList<>());
					list = portals.get(color);
				}
				list.add(location);
			}
		}

		for (var entry : portals.entrySet())
		{
			var color = entry.getKey();
			var portalLocations = entry.getValue();
			if (portalLocations.size() != 2)
			{
				errors.add(new LevelCheckResult.LevelCheckError(
						String.format(FAIL_MSG, color.getColorName()),
						portalLocations));
			}
		}
		if (errors.size() > 0)
			return new LevelCheckResult(false, errors);
		return LevelCheckResult.SUCCESS;
	}
}
