/**
 * Modified by Stephen Zhang & Hanny Zhang (Team 08)
 * <p>
 * Responsibilities striped further
 * Implements Singleton pattern
 */

package src;

import ch.aplu.jgamegrid.GameGrid;
import src.game.InputManager;
import src.game.PortalManager;
import src.io.GameCallback;
import src.io.GameLevels;
import src.io.LogManager;
import src.models.entities.GoldPiece;
import src.models.entities.PacMan;
import src.models.entities.Pill;
import src.models.entities.Portal;

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
	boolean editorRunning = false;
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
	private String _testMap = null;

	private Game()
	{
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, CELL_SIZE, ENGINE_DEBUG_MODE);
	}

	public static boolean newGame(String path, boolean testMap)
	{
		// Before creating new instance, dispose the previous instance
		if (_instance != null)
			_instance.getFrame().dispose();

		 GameLevels levels = null;
		if (testMap)
		{
			// Load single level in a list of levels (contains only one item)
			levels = GameLevels.fromSingleMap(path);
		}
		else
			// Load levels (perform game check and level checks)
			levels = GameLevels.fromFolder(path);

		// If any check fails, do not create game instance
		if (levels == null)
			return false;

		// If all check passed, start the game and initialise with the loaded levels
		_instance = new Game();
		_instance.init(levels);
		if (testMap)
			_instance._testMap = path;
		return true;
	}

	/**
	 * Get the instance of the game (or create one if non-exist)
	 * @return a singleton instance of Game
	 */
	public static Game getGame()
	{
		// Create instance
		if (_instance == null)
			_instance = new Game();
		return _instance;
	}

	public boolean isGameStopped() { return _gameStopped; }

	private InputManager getInputManager()
	{
		if (_playerInput == null)
			_playerInput = new InputManager(this);
		return _playerInput;
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
	public void changeScore(int byValue)
	{
		_score += byValue;
		this.setTitle(String.format(SCORE_TITLE, GAME_TITLE, _score));
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

	/**
	 * Initialise a game (Load map and properties)
	 */
	private void init(GameLevels levels)
	{
		// Setup game
		setSimulationPeriod(SIMULATION_PERIOD);
		setTitle(GAME_TITLE);

		loadProperties(DEFAULT_PROPERTIES_PATH);
		_levels = levels;

		// Setup input controller
		addKeyRepeatListener(getInputManager());
		setKeyRepeatPeriod(KEY_REPEAT_PERIOD);
	}

	/**
	 * Load the current level and start the game
	 */
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
	 * Check if the win condition (all pills and golds are eaten) is met.
	 * If so, advance to the next level until the last, then stop the game and declare win.
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

	/**
	 * Return to editor (if in test map mode, load this map, otherwise leave empty)
	 */
	public void returnToEditor()
	{
		if (editorRunning)
			return;

		this._gameStopped = true;
		doPause();
		hide();
		if (_testMap != null)
			Driver.RunEditor(_testMap);
		else
			Driver.RunEditor();
		editorRunning = true;
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
