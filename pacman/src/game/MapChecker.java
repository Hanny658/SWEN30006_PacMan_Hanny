package src.game;

import ch.aplu.jgamegrid.Location;
import src.io.GameMapXmlParser;
import src.models.Entity;

import java.util.Map;

public class MapChecker {
    private static int LEAST_GP = 2;    // least number of golds and pills
    GameMapXmlParser gameMapper;
    Map<Entity, Location> gameMap;

    public MapChecker(){
        this.gameMapper = new GameMapXmlParser();
        this.gameMap = null;
    }

    /** Checks the map and print to the game logfile */
    public boolean checkMap(String MapXml)
    {
        gameMap = gameMapper.loadMapFromXml(MapXml);

        // Check exactly one starting point for PacMan

        // Check exactly two tiles for each portal appearing on the map

        // Check at least two Gold and Pill in total [number configurable]

        // Check accessibility od Golds and Pills

        return true;
    }

    /** Check for starting point of PacMan */
    
}