package src.models.entities;

import src.game.PortalManager;
import src.models.Collidable;
import src.models.Entity;
import src.models.GameVersion;

public class Portal extends Entity implements Collidable
{
	// Sprite images path
	static final String WHITE_PORTAL = "sprites/portalWhite.png";
	static final String YELLOW_PORTAL = "sprites/portalYellow.png";
	static final String GOLD_PORTAL = "sprites/portalDarkGold.png";
	static final String GRAY_PORTAL = "sprites/portalDarkGray.png";

	private PortalColor _color;
	private PortalManager _manager;

	public Portal(PortalColor c)
	{
		super(getImgPath(c));
		this._color = c;
	}

	public PortalColor getColor() { return _color; }

	public void setColor(PortalColor color) { _color = color; }

	public PortalManager getManager() { return _manager; }

	public void setManager(PortalManager manager) { _manager = manager; }

	/** Function that finds correct image for initialise portal entity, return null if not found */
	private static String getImgPath(PortalColor c)
	{
		switch (c)
		{
			case White -> {return WHITE_PORTAL;}
			case Yellow -> {return YELLOW_PORTAL;}
			case DarkGold -> {return GOLD_PORTAL;}
			case DarkGray -> {return GRAY_PORTAL;}
		}
		return null;
	}

	@Override
	public void onCollide(Collidable collidable)
	{
		if (collidable instanceof Entity)
		{
			getManager().teleport((Entity) collidable, this);
		}
	}

	@Override
	public Object clone() { return new Portal(_color); }

	/** Enum for colors of portals */
	public enum PortalColor
	{
		White("White"),
		Yellow("Yellow"),
		DarkGold("DarkGold"),
		DarkGray("DarkGray");
		private final String _value;

		PortalColor(String name)
		{
			_value = name;
		}

		public String getColorName()
		{
			return _value;
		}
	}
}
