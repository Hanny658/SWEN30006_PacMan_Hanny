package src.validation.levelchecks;

import src.models.GameMap;

import java.util.Arrays;

/**
 * Checks if the xml file can be deserialised, and if it contains both size and entity list
 */
public class FileFormatCheck implements LevelCheck
{
	private static final String XML_INVALID = "game map xml file invalid";

	@Override
	public LevelCheckResult check(GameMap gameMap)
	{
		if (gameMap == null || gameMap.getSize() == null)
			return new LevelCheckResult(false,
					Arrays.asList(new LevelCheckResult.LevelCheckError(XML_INVALID, null)));
		return LevelCheckResult.SUCCESS;
	}
}
