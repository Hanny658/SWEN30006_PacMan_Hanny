package src.game.levelchecks;

import ch.aplu.jgamegrid.Location;
import src.models.Entity;
import src.models.GameMapSchema;
import src.models.entities.GoldPiece;
import src.models.entities.Pill;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Check at least two Gold and Pill in total [Num configurable] */
public class GPNumCheck implements LevelCheck
{
	private static final short LEAST_GP_NUM = 2;
	private static final String LACK_GP = String.format("less than %d Gold and Pill", LEAST_GP_NUM);

	@Override
	public LevelCheckResult check(Map<Location, Entity> entities, GameMapSchema.Size mapSize)
	{
		int count = 0;
		for (var entry : entities.entrySet())
		{
			var entity = entry.getValue();
			if (entity instanceof Pill || entity instanceof GoldPiece)
				count++;
			if (count == 2) return LevelCheckResult.SUCCESS;
		}
		var error = Arrays.asList(new LevelCheckResult.LevelCheckError(LACK_GP, null));
		return new LevelCheckResult(false, error);
	}
}
