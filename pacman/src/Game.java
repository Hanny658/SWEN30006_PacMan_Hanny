/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Busy waiting removed; responsibilities reduced by delegating non-cohesive ones to other classes;
 * magic numbers and magic strings are extracted as constants.
 * Support for multiverse extension implemented.
 */

package src;

import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import src.game.*;
import src.io.GameCallback;
import src.io.LogManager;
import src.io.MapLoader;
import src.models.Collidable;
import src.models.GameVersion;
import src.models.entities.*;

import java.util.List;
import java.util.Properties;

public class Game extends GameGrid
{
	private static final String GAME_TITLE = "[PacMan in the Multiverse]";
	private static final String SCORE_TITLE = "%s Current score: %d";
	private static final String WIN_TITLE = "YOU WIN";
	private static final String LOSE_TITLE = "GAME OVER";
	private static final int NUM_CELLS_X = 20;
	private static final int NUM_CELLS_Y = 11;
	private static final int SIMULATION_PERIOD = 100;
	private static final int END_GAME_DELAY = 120;
	private static final int CELL_SIZE = 20;
	private static final int KEY_REPEAT_PERIOD = 150;
	private static final boolean ENGINE_DEBUG_MODE = false;
	private static final int SLOW_DOWN_FACTOR = 3;
	private static Game _instance = null;
	private PacMan _player;
	private InputManager _playerInput;
	private final GameVersion _gameVersion;
	private int _score = 0;
	private int _numPillsEaten = 0;
	private boolean _gameStopped = false;
	private boolean _win = false;
	private Properties _properties;

	private Game(Properties properties)
	{
		//Setup game
		super(NUM_CELLS_X, NUM_CELLS_Y, CELL_SIZE, ENGINE_DEBUG_MODE);
		setSimulationPeriod(SIMULATION_PERIOD);
		setTitle(GAME_TITLE);

		_gameVersion = GameVersion.getGameVersion(properties.getProperty("version"));

		this._properties = properties;
		//MapLoader.loadWithProperty(this, properties);

	}

	private void startGame()
	{
		boolean mapValid = MapLoader.loadFromXml(this, "testamoffat.xml");
		if (!mapValid)
		{
			System.err.println("Map invalid, Check error log for errors.");
			System.exit(1);
			return;
		}

		for (var entity : this.getActors())
		{
			if (entity instanceof Portal)
			{
				for (var entity2 : this.getActors())
				{
					if (entity2 instanceof Portal)
					{
						PortalManager.registerPortals((Portal) entity, (Portal) entity2);
					}
				}
			}
		}

		//Setup for auto test
		for (var entity : this.getActors())
		{
			if (entity instanceof PacMan)
			{
				_player = (PacMan) entity;
				_playerInput = new InputManager(_player);
			}
		}
		boolean isAutoMode = Boolean.parseBoolean(_properties.getProperty("PacMan.isAuto"));
		_player.setAutoMoves(_properties.getProperty("PacMan.move"));
		_player.setAuto(isAutoMode);

		initializeGameStates(_properties);

		// Setup input controller
		// Refuse input if in auto mode
		if (!isAutoMode)
		{
			addKeyRepeatListener(_playerInput);
			setKeyRepeatPeriod(KEY_REPEAT_PERIOD);
		}

		// Run the game
		doRun();
		show();
	}

	/**
	 * Create actors depending on properties loaded
	 * <p>
	 * Potentially a Builder model could be implemented in the future
	 *
	 * @param properties properties loaded
	 */
	private void initializeGameStates(Properties properties)
	{
		//Setup simple random seeds
		int seed = Integer.parseInt(properties.getProperty("seed"));
		_player.setSeed(seed);

		// Setup simple slow down
		_player.setSlowDown(SLOW_DOWN_FACTOR);
	}

	/**
	 * Create a Singleton instance of Game
	 * <p>
	 * This is not combined with getGame() since the constructor need to be called with argument.
	 * Better implementation may be possible in the future.
	 *
	 * @param properties
	 */
	public static void initGame(Properties properties)
	{
		_instance = new Game(properties);
		_instance.startGame();
	}

	public static Game getGame()
	{
		return _instance;
	}

	/**
	 * Simulation cycle of the game
	 * <p>
	 * When the game is declared to be stopped, stop the cycle immediately and declare game result.
	 *
	 * @see GameGrid
	 */
	@Override
	public void act()
	{
		if (_gameStopped)
		{
			removeKeyRepeatListener(_playerInput);
			doPause();
			delay(END_GAME_DELAY);
			declareGameResult();
		}
	}

	/**
	 * Gets a list of all Collidables that are at the given location
	 *
	 * @param atLocation the location for detection
	 * @return a list of Collidables that are colliding
	 * @see Collidable
	 */
	public List<Collidable> getAllCollidingsAt(Location atLocation)
	{
		return CollisionManager.getAllCollidingsAt(atLocation, this);
	}

	/**
	 * Declare the game result by setting the title and logging
	 *
	 * @see GameCallback
	 */
	private void declareGameResult()
	{
		if (_win)
		{
			this.setTitle(WIN_TITLE);
			LogManager.getGameCallback().endOfGame(WIN_TITLE);
		}
		else
		{
			this.setTitle(LOSE_TITLE);
			LogManager.getGameCallback().endOfGame(LOSE_TITLE);
		}
	}

	/**
	 * Check if the win condition (all pills and golds are eaten) is met. If so, stop the game and declare win.
	 */
	public void checkWin()
	{
		// Win condition is that all pills and golds have been eaten
		for (var actor : this.getActors())
		{
			if (actor instanceof Pill && actor.isVisible() || actor instanceof GoldPiece && actor.isVisible())
				return;
		}
		this.stopGame(true);
	}

	public void changeScore(int byValue)
	{
		_score += byValue;
		this.setTitle(String.format(SCORE_TITLE, GAME_TITLE, _score));
	}

	/**
	 * Record the number of pills eaten for logging usage. This includes pills and golds.
	 *
	 * @param byValue increase by
	 * @see GameCallback
	 */
	public void changeNumPillsEaten(int byValue)
	{
		_numPillsEaten += byValue;
	}

	public int getScore()
	{
		return _score;
	}

	/**
	 * Gets the number of pills and golds eaten
	 *
	 * @return the number
	 * @see GameCallback
	 */
	public int getNumPillsEaten()
	{
		return _numPillsEaten;
	}

	/**
	 * Set the game to be stopped and set the game result
	 *
	 * @param isWin if the game has won
	 */
	public void stopGame(boolean isWin)
	{
		this._gameStopped = true;
		this._win = isWin;
	}

	public PacMan getPlayer()
	{
		return _player;
	}

	public void reportPlayerStatus()
	{
		LogManager.getGameCallback().pacManLocationChanged(
				getPlayer().getLocation(),
				getScore(),
				getNumPillsEaten());
	}

	/**
	 * Set all monsters to FROZEN
	 * <p>
	 * Only works in MULTIVERSE mode.
	 */
	public void setFrozen()
	{
		if (_gameVersion == GameVersion.SIMPLE) return;

		// For each monster, set it frozen
		for (var actor : getActors())
			if (actor instanceof Monster)
				((Monster) actor).setFrozen();
	}

	/**
	 * Set all monsters to FURIOUS
	 * <p>
	 * Only works in MULTIVERSE mode.
	 * State will not change if the monster is already in FROZEN mode.
	 */
	public void setFurious()
	{
		if (_gameVersion == GameVersion.SIMPLE) return;

		// For each monster, set it frozen
		for (var actor : getActors())
			if (actor instanceof Monster)
				((Monster) actor).setFurious();
	}
}

/**
 * Algorithms Are FUN!! WOW~ Tada!!! (Moffat, 2021)
 */
