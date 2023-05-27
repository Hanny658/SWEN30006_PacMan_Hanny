package src.game;

import src.models.Entity;
import src.models.Pair;
import src.models.entities.Portal;

import java.util.ArrayList;
import java.util.List;


public class PortalManager
{
	private ArrayList<Pair<Portal, Portal>> _portalRegistry = new ArrayList<>();
	public boolean isRegistered(Portal portal)
	{
		return getPairedPortal(portal) != null;
	}

	public boolean autoRegister(List<Portal> portals)
	{
		for (var portal1 : portals)
			for (var portal2 : portals)
				registerPortals(portal1, portal2);

		if (_portalRegistry.size() * 2 == portals.size())
			return true;
		return false;
	}

	private boolean registerPortals(Portal portal1, Portal portal2)
	{
		// Must not register itself
		if (portal1 == portal2)
			return false;

		// Must not register a pair of portal twice
		if (isRegistered(portal1) || isRegistered(portal2))
			return false;

		// Portals must be in the same color
		if (portal1.getColor() != portal2.getColor())
			return false;

		Pair<Portal, Portal> pair = new Pair<>(portal1, portal2);
		portal1.setManager(this);
		portal2.setManager(this);
		_portalRegistry.add(pair);
		return true;
	}

	public Portal getPairedPortal(Portal portal)
	{
		for (var pair : _portalRegistry)
		{
			if (pair.item1() == portal)
				return pair.item2();
			else if (pair.item2() == portal)
				return pair.item1();
		}
		return null;
	}

	public void teleport(Entity entity, Portal portal1)
	{
		Portal portal2 = getPairedPortal(portal1);
		entity.setLocation(portal2.getLocation());
	}
}
