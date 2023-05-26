package src.models.entities;

import ch.aplu.jgamegrid.Actor;
import src.models.Entity;

import java.util.*;

public class Portal extends Entity
{
	/** Enum for colors of portals */
	public enum PortColor {
		White, Yellow, DarkGold, DarkGray
	}
	private static ArrayList<Portal> allPortals = new ArrayList<>();
	private ArrayList<Actor> tpActors = new ArrayList<>();
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
		Portal.allPortals.add(this);
	}

	public PortColor getColor()
	{
		return color;
	}

	/** Teleport actors when actor stepped onto it */
	public Boolean teleportActor(Actor a)
	{
		if (this.tpActors.contains(a)) { return false; }
		if (this.getLocation().equals(a.getLocation()))
		{
			System.out.println("Actor not in same positions.");
			return false;
		}
		Portal destin = this.findMyMate();
		if (destin == null) return false;
		a.setLocation(destin.getLocation());
		destin.tpActors.add(a);
		return true;
	}

	/** Find the partner portal in all instances */
	public Portal findMyMate()
	{
		for (Portal p : allPortals)
		{
			if (p.color == this.color) return p;
		}
		return null;
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
	public Object clone() { return new Portal(color); }
}
