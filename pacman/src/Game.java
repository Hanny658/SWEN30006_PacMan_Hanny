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
import src.models.Collidable;
import src.models.entities.*;
import src.io.GameLevels;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Game extends GameGrid
{
	private static final String DEFAULT_PROPERTIES_PATH = "test.properties";
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
	private GameLevels _levels;
	private int _currentLevel = 0;
	private int _seed = 0;
	private boolean _gameStopped = false;
	private boolean _win = false;
	private boolean _autoMode = false;
	

	private Game()
	{
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, CELL_SIZE, ENGINE_DEBUG_MODE);
	}

	public static boolean newGame(String gameFolder)
	{
		// Before creating new instance, dispose the previous instance
		if (_instance != null)
			_instance.getFrame().dispose();

		// Load levels (perform game check and level checks)
		var levels = GameLevels.fromFolder(gameFolder);

		// If any check fails, do not create game instance
		if (levels == null)
			return false;

		// If all check passed, start the game and initialise with the loaded levels
		_instance = new Game();
		_instance.init(levels);
		return true;
	}

	/**
	 * Initialise a game (Load map and properties)
	 */
	private void init(GameLevels levels)
	{
		// TODO: DEBUG
		System.err.println("Game started with maps:");
		levels.getLevels().forEach(value ->
				System.err.printf("%s\n", value));

		// Setup game
		setSimulationPeriod(SIMULATION_PERIOD);
		setTitle(GAME_TITLE);

		loadProperties(DEFAULT_PROPERTIES_PATH);
		_levels = levels;

		// Setup input controller
		addKeyRepeatListener(getInputManager());
		setKeyRepeatPeriod(KEY_REPEAT_PERIOD);
	}

	public boolean isGameStopped() { return _gameStopped; }

	public void startGame()
	{
		loadLevel(_currentLevel);
		doRun();
		show();
	}

	private void loadLevel(int currentLevel)
	{
		getInputManager().setEntityToControl(null);
		removeAllActors();
		doReset();
		// Clear the grid with path blocks
		getBg().clear(Color.lightGray);

		var level = _levels.getLevel(currentLevel);
		List<Portal> portals = new ArrayList<>();
		try
		{
			for (var entry : level.getEntities().entrySet())
			{
				var location = entry.getKey();
				var entity = entry.getValue();
				addActor(entity, location);
				entity.setSeed(_seed);
				if (entity instanceof PacMan)
				{
					_player = (PacMan) entity;
					_player.setSlowDown(SLOW_DOWN_FACTOR);
					_player.setAutoMode(_autoMode);
					if (_autoMode)
						getInputManager().setEntityToControl(null);
					else
						getInputManager().setEntityToControl(_player);
				}
				else if (entity instanceof Portal)
					portals.add((Portal) entity);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		setPaintOrder(PacMan.class);
		_portalManager = new PortalManager();
		_portalManager.autoRegister(portals);
		doRun();
	}

	private InputManager getInputManager()
	{
		if (_playerInput == null)
			_playerInput = new InputManager(this);
		return _playerInput;
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
		try (InputStream input = new FileInputStream(propertiesFilename))
		{
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			// purity by removing empty values
			for (Object key : prop.keySet())
				if (prop.getProperty((String) key).equals(""))
					prop.remove(key);

			//Setup simple random seeds
			_seed = Integer.parseInt(prop.getProperty("seed"));
			// set autoMode
			_autoMode = Boolean.parseBoolean(prop.getProperty("PacMan.isAuto"));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
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
		if (_currentLevel >= _levels.getNumLevels() - 1)
			this.stopGame(true);
		else
			loadLevel(++_currentLevel);
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
