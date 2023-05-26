/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 */

package src.models.entities;

import src.models.Entity;

public class Wall extends Entity
{
	private static final String DEFAULT_SPRITE = "sprites/wall.png";

	public Wall()
	{
		super(DEFAULT_SPRITE);
	}
	@Override
	public Object clone() throws CloneNotSupportedException { return new Wall(); }
}
