/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * To implement MULTIVERSE
 */

package src.models;

public enum GameVersion
{
	SIMPLE("simple"), MULTIVERSE("multiverse");

	private final String _value;

	GameVersion(String name)
	{
		this._value = name;
	}

	public static GameVersion getGameVersion(String name)
	{
		for (var gameVersion : values())
			if (gameVersion._value.equals(name))
				return gameVersion;
		return null;
	}
}
