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
	private PortalManager _portalManager;
	private int _score = 0;
	private int _numPillsEaten = 0;
	private boolean _gameStopped = false;
	private boolean _win = false;
	private boolean _autoMode = false;
	

	private Game()
	{
		super(NUM_CELLS_X, NUM_CELLS_Y, CELL_SIZE, ENGINE_DEBUG_MODE);
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
		var gameMaps = GameChecker.checkGameFolder();
		if (gameMaps == null)
		{
			System.err.println("Game check failed.");
			System.exit(1);
			return;
		}
		System.err.println("Game check passed.");
		gameMaps.entrySet().forEach(entry -> System.err.printf("Valid map: %d: %s\n", entry.getKey(), entry.getValue()));

		boolean mapValid = MapLoader.loadFromXml(this, "test/testamoffat.xml");
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

		// Play the sound in a loop
		playLoop("test/Liyue.wav");
	}

	public void startGame()
	{
		// Run the game
		System.out.println("DoRun");
		_gameStopped = false;
		doRun();
		System.out.println("Show");
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
		_player.setAutoMoves(properties.getProperty("PacMan.move"));
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
		this._gameStopped = true;
		doPause();


		if (!editorRunning)
		{
			editorRunning = true;
			hide();
			Driver.RunEditor();
		}
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

	/** reset the whole game*/
	public void resetAll(){
//		// reset actors
//		this.doReset();
//		// reset our own parameters
//		initializeGameStates(Game.getGame()._properties);
		initGame();
		getGame().startGame();
	}
}

/**
 * Algorithms Are FUN!! WOW~ Tada!!! (Moffat, 2021)
 */
