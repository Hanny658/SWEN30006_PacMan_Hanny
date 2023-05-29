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
import src.models.entities.*;
import src.io.GameLevels;

import java.util.ArrayList;
import java.util.List;

public class Game extends GameGrid
{
	private static final String DEFAULT_PROPERTIES_PATH = "test.properties";
	private static final String FOLDER_PATH = "test";
	private static final String GAME_TITLE = "[PacMan in the TorusVerse]";
	private static final String SCORE_TITLE = "%s Current score: %d";
	private static final String WIN_TITLE = "YOU WIN";
	private static final String LOSE_TITLE = "GAME OVER";
	private static final int DEFAULT_WIDTH = 20;
	private static final int DEFAULT_HEIGHT = 11;
	private static final int SIMULATION_PERIOD = 100;
	private static final int END_GAME_DELAY = 120;
	private static final int CELL_SIZE = 20;
	private static final int KEY_REPEAT_PERIOD = 150;
	private static final boolean ENGINE_DEBUG_MODE = false;
	private static final int SLOW_DOWN_FACTOR = 3;
	private static Game _instance = null;
	private PacMan _player;
	private InputManager _playerInput;
	private PortalManager _portalManager;
	private int _score = 0;
	private int _numPillsEaten = 0;
	private boolean _gameStopped = false;
	private boolean _win = false;
	private boolean _autoMode = false;
	

	private Game()
	{
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, CELL_SIZE, ENGINE_DEBUG_MODE);
	}

	public static void newGame()
	{
		if (_instance != null)
		{
			_instance.getFrame().dispose();
			_instance = null;
		}
		getGame();
	}

	/**
	 * Initialise a game (reload map and properties)
	 */
	public void initGame()
	{
		//Setup game
		setSimulationPeriod(SIMULATION_PERIOD);
		setTitle(GAME_TITLE);
		var gameLevels = GameLevels.fromFolder(FOLDER_PATH);
		if (gameLevels == null)
		{
			System.exit(1);
			return;
		}

		// TODO: DEBUG
		gameLevels.getLevels().forEach((key, value) -> System.err.printf("Valid map: %d: %s\n", key, value));

		boolean mapValid = MapLoader.fromXml(this, "test/testamoffat.xml");
		if (!mapValid)
		{
			System.err.println("Map invalid, Check error log for errors.");
			System.exit(1);
			return;
		}

		_portalManager = new PortalManager();
		List<Portal> portals = new ArrayList<>();
		for (var entity : this.getActors())
			if (entity instanceof Portal)
				portals.add((Portal) entity);

		_portalManager.autoRegister(portals);

		//Setup for auto test
		for (var entity : this.getActors())
		{
			if (entity instanceof PacMan)
			{
				_player = (PacMan) entity;
				_playerInput = new InputManager(this, _player);
			}
		}

		// Setup simple slow down
		_player.setSlowDown(SLOW_DOWN_FACTOR);

		loadProperties(DEFAULT_PROPERTIES_PATH);
		
		// Setup input controller
		// Refuse input if in auto mode
		if (!_autoMode)
		{
			addKeyRepeatListener(_playerInput);
			setKeyRepeatPeriod(KEY_REPEAT_PERIOD);
		}
	}

	public void startGame()
	{
		// Run the game
		_gameStopped = false;
		doRun();
		show();
	}

	/**
	 * Create actors depending on properties loaded
	 * <p>
	 * Potentially a Builder model could be implemented in the future
	 *
	 * @param propertiesFilename property name loaded
	 */
	private void loadProperties(String propertiesFilename)
	{
		var properties = MapLoader.loadPropertiesFile(propertiesFilename);
		//Setup simple random seeds
		int seed = Integer.parseInt(properties.getProperty("seed"));
		_player.setSeed(seed);
		
		// set autoMode
		_autoMode = Boolean.parseBoolean(properties.getProperty("PacMan.isAuto"));
	}

	public static Game getGame()
	{
		// Create instance
		if (_instance == null)
			_instance = new Game();
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
			this.hide();
			returnToEditor();
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

	boolean editorRunning = false;
	public void returnToEditor()
	{
		if (editorRunning)
			return;

		this._gameStopped = true;
		doPause();
		hide();
		Driver.RunEditor();
		editorRunning = true;
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
}

/**
 * Algorithms Are FUN!! WOW~ Tada!!! (Moffat, 2021)
 */
