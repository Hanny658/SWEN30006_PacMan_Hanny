package src.game;

import src.models.Entity;
import src.models.entities.Portal;

import java.util.ArrayList;
import java.util.List;


public class PortalManager
{
	private ArrayList<PortalPair> _portalRegistry = new ArrayList<>();
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

		PortalPair pair = new PortalPair();
		pair.setPortals(portal1, portal2);
		portal1.setManager(this);
		portal2.setManager(this);
		_portalRegistry.add(pair);
		return true;
	}

	public Portal getPairedPortal(Portal portal)
	{
		for (var pair : _portalRegistry)
		{
			if (pair.getPortal1() == portal)
				return pair.getPortal2();
			else if (pair.getPortal2() == portal)
				return pair.getPortal1();
		}
		return null;
	}

	public void teleport(Entity entity, Portal portal1)
	{
		Portal portal2 = getPairedPortal(portal1);
		entity.setLocation(portal2.getLocation());
	}

	public class PortalPair
	{
		private Portal _portal1, _portal2;
		public void setPortals(Portal portal1, Portal portal2)
		{
			this._portal1 = portal1;
			this._portal2 = portal2;
		}

		public Portal getPortal1() { return _portal1; }
		public Portal getPortal2() { return _portal2; }
	}
}
