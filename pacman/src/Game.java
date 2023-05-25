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
import src.controllers.CollisionController;
import src.controllers.InputController;
import src.controllers.LogController;
import src.controllers.MapLoader;
import src.models.Collidable;
import src.models.GameVersion;
import src.models.MonsterStates;
import src.models.entities.GoldPiece;
import src.models.entities.Monster;
import src.models.entities.PacMan;
import src.models.entities.Pill;
import src.models.entities.monsters.*;

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
	private final PacMan _player = new PacMan();
	private final InputController _playerInput = new InputController(_player);
	private final GameVersion _gameVersion;
	private int _score = 0;
	private int _numPillsEaten = 0;
	private boolean _gameStopped = false;
	private boolean _win = false;

	private Game(Properties properties)
	{
		//Setup game
		super(NUM_CELLS_X, NUM_CELLS_Y, CELL_SIZE, ENGINE_DEBUG_MODE);
		setSimulationPeriod(SIMULATION_PERIOD);
		setTitle(GAME_TITLE);

		_gameVersion = GameVersion.getGameVersion(properties.getProperty("version"));

		//Setup for auto test
		boolean isAutoMode = Boolean.parseBoolean(properties.getProperty("PacMan.isAuto"));
		_player.setAutoMoves(properties.getProperty("PacMan.move"));
		_player.setAuto(isAutoMode);

		MapLoader.loadWithProperty(this, properties);

		initializeActors(properties);

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
	private void initializeActors(Properties properties)
	{
		// Creates simple monsters
		Monster troll = new Troll();
		Monster tx5 = new TX5();

		//Setup simple random seeds
		int seed = Integer.parseInt(properties.getProperty("seed"));
		_player.setSeed(seed);
		troll.setSeed(seed);
		tx5.setSeed(seed);

		// Setup simple slow down
		_player.setSlowDown(SLOW_DOWN_FACTOR);
		troll.setSlowDown(SLOW_DOWN_FACTOR);
		tx5.setSlowDown(SLOW_DOWN_FACTOR);

		// Define simple monsters locations
		String[] trollLocations = properties.getProperty("Troll.location").split(",");
		String[] tx5Locations = properties.getProperty("TX5.location").split(",");
		String[] playerLocations = properties.getProperty("PacMan.location").split(",");
		int tx5X = Integer.parseInt(tx5Locations[0]);
		int tx5Y = Integer.parseInt(tx5Locations[1]);

		int trollX = Integer.parseInt(trollLocations[0]);
		int trollY = Integer.parseInt(trollLocations[1]);

		int playerX = Integer.parseInt(playerLocations[0]);
		int playerY = Integer.parseInt(playerLocations[1]);

		addActor(troll, new Location(trollX, trollY), Location.NORTH);
		addActor(tx5, new Location(tx5X, tx5Y), Location.NORTH);
		addActor(_player, new Location(playerX, playerY));

		// Repeat for multiverse monsters
		if (_gameVersion == GameVersion.MULTIVERSE)
		{
			Monster orion = new Orion();
			Monster alien = new Alien();
			Monster wizard = new Wizard();

			orion.setSeed(seed);
			alien.setSeed(seed);
			wizard.setSeed(seed);

			orion.setSlowDown(SLOW_DOWN_FACTOR);
			alien.setSlowDown(SLOW_DOWN_FACTOR);
			wizard.setSlowDown(SLOW_DOWN_FACTOR);

			String[] orionLocations = properties.getProperty("Orion.location").split(",");
			String[] alienLocations = properties.getProperty("Alien.location").split(",");
			String[] wizardLocations = properties.getProperty("Wizard.location").split(",");

			int orionX = Integer.parseInt(orionLocations[0]);
			int orionY = Integer.parseInt(orionLocations[1]);

			int alienX = Integer.parseInt(alienLocations[0]);
			int alienY = Integer.parseInt(alienLocations[1]);

			int wizardX = Integer.parseInt(wizardLocations[0]);
			int wizardY = Integer.parseInt(wizardLocations[1]);

			addActor(orion, new Location(orionX, orionY), Location.NORTH);
			addActor(alien, new Location(alienX, alienY), Location.NORTH);
			addActor(wizard, new Location(wizardX, wizardY), Location.NORTH);
		}

		// Freeze tx5 for 5 seconds
		tx5.setStateForSeconds(MonsterStates.FROZEN, 5);
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
		return CollisionController.getAllCollidingsAt(atLocation, this);
	}

	/**
	 * Declare the game result by setting the title and logging
	 *
	 * @see src.controllers.GameCallback
	 */
	private void declareGameResult()
	{
		if (_win)
		{
			this.setTitle(WIN_TITLE);
			LogController.getGameCallback().endOfGame(WIN_TITLE);
		}
		else
		{
			this.setTitle(LOSE_TITLE);
			LogController.getGameCallback().endOfGame(LOSE_TITLE);
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
	 * @see src.controllers.GameCallback
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
	 * @see src.controllers.GameCallback
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
		LogController.getGameCallback().pacManLocationChanged(
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
