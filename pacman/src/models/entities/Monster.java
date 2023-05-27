/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Made abstract and reduced responsibilities
 */

package src.models.entities;

import ch.aplu.jgamegrid.Location;
import src.game.CollisionManager;
import src.io.LogManager;
import src.models.Collidable;
import src.models.Entity;
import src.models.MonsterStates;

public abstract class Monster extends Entity implements Collidable
{
	public static final int INF = -1;
	protected static final int SPEED_NORM = 1;
	protected static final int SPEED_FURY = 2;
	private static final int DEFAULT_FURIOUS_TIME = 3;
	private static final int DEFAULT_FROZEN_TIME = 3;
	private static final int SEC_TO_MILLI = 1000;
	private MonsterStates _state = MonsterStates.NORMAL;
	private long _stateFinishTime = 0;

	public Monster(String spriteFile)
	{
		super(spriteFile);
	}

	@Override
	public Location getCollidableLocation()
	{
		return this.getLocation();
	}

	public void setStateForSeconds(MonsterStates state, int seconds)
	{
		// If state is not changing, only adds the timer
		// If state is changing, reset the timer
		if (state == this._state)
			_stateFinishTime = Math.max(_stateFinishTime,
					System.currentTimeMillis() + (long) seconds * SEC_TO_MILLI);
		else
			_stateFinishTime = System.currentTimeMillis() + (long) seconds * SEC_TO_MILLI;
		this._state = state;
	}

	public void setFurious()
	{
		// Monster will not get furious while frozen
		if (_state == MonsterStates.FROZEN)
			return;

		this.setStateForSeconds(MonsterStates.FURIOUS, DEFAULT_FURIOUS_TIME);
	}

	public void setFrozen()
	{
		this.setStateForSeconds(MonsterStates.FROZEN, DEFAULT_FROZEN_TIME);
	}

	@Override
	public void act()
	{
		//if (Game.getGame().isStopped()) return;

		updateStateTimer();

		// If frozen, do nothing
		if (_state == MonsterStates.FROZEN) return;

		walkApproach();
		setHorzMirror(!(getDirection() > 150) || !(getDirection() < 210));
		CollisionManager.detectCollision(this, this.gameGrid);
	}

	private void updateStateTimer()
	{
		// If set to INF, do not revert to NORMAL
		if (_stateFinishTime == INF)
			return;

		if (_stateFinishTime < System.currentTimeMillis())
			_state = MonsterStates.NORMAL;
	}

	/**
	 * Gets the speed of this monster
	 *
	 * @return SPEED_FURY if in furious state, otherwise SPEED_NORM
	 */
	@Override
	public int getSpeed()
	{
		if (_state == MonsterStates.FURIOUS)
			return SPEED_FURY;
		return SPEED_NORM;
	}

	/**
	 * This super method reports location change to GameCallBack
	 * so super.walkApproach() should be the last function to call
	 */
	protected void walkApproach()
	{
		// Report location change
		LogManager.getGameCallback().monsterLocationChanged(this);
	}

	/** This function gets the state for current monster */
	protected MonsterStates getState()
	{
		return this._state;
	}
}
