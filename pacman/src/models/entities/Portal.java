package src.models.entities;

import src.controllers.PortalManager;
import src.models.Collidable;
import src.models.Entity;

import java.util.*;

public class Portal extends Entity implements Collidable
{
	/** Enum for colors of portals */
	public enum PortColor {
		White, Yellow, DarkGold, DarkGray
	}
	public PortColor color;

	// Sprite images path
	static final String WHITE_PORTAL = "sprites/portalWhite.png";
	static final String YELLOW_PORTAL = "sprites/portalYellow.png";
	static final String GOLD_PORTAL = "sprites/portalDarkGold.png";
	static final String GRAY_PORTAL = "sprites/portalDarkGray.png";

	public Portal(PortColor c)
	{
		super(getImgPath(c));
		this.color = c;
	}

	/** Function that finds correct image for initialise portal entity, return null if not found */
	private static String getImgPath(PortColor c)
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
			PortalManager.teleport((Entity) collidable, this);
		}
	}

	@Override
	public Object clone() { return new Portal(color); }
}
