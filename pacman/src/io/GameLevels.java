/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 */

package src.io;

import src.models.GameMap;
import src.models.Pair;
import src.validation.LevelChecker;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class representing a list of levels for the game to advance
 * <p>
 * Before loading levels, it also checks for Game folder integrity (game check)
 * a. at least one correctly named map file in the folder
 * b. the sequence of map files well-defined (only one map named with a particular number)
 */
public class GameLevels
{
    private static final String NO_MAPS = "no maps found";
    private static final String CONFLICT_MAPS = "multiple maps at same level";
    private final List<GameMap> _levels;


    private GameLevels(List<GameMap> levels)
    {
        this._levels = levels;
    }

    /**
     * Load levels from a folder
     * @param gameFolder the folder to check
     * @return a sorted LinkedHashMap that keep entry in order for the game level
     */
    public static GameLevels fromFolder(String gameFolder)
    {
        var sortedMapFiles = getValidMaps(gameFolder);
        if (sortedMapFiles == null)
            return null;

        // Conduct level checking if file checking all passed
        var sortedGameMaps = loadLevels(sortedMapFiles);
        if (sortedGameMaps == null)
            return null;
        return new GameLevels(sortedGameMaps);
    }

    /**
     * Only load a single map (for map test)
     * @param mapFilename name of the map
     * @return a list containing only the single map
     */
    public static GameLevels fromSingleMap(String mapFilename)
    {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        map.put(0, mapFilename);
        var gameMap = loadLevels(map);
        if (gameMap == null)
            return null;
        return new GameLevels(gameMap);
    }

    private static LinkedHashMap<Integer, String> getValidMaps(String gameFolder)
    {
        File folder = new File(gameFolder);
        File[] files = folder.listFiles();

        // Check if there are no files in the folder
        if (files == null || files.length == 0)
        {
            LogManager.errorLog(String.format("%s - %s", gameFolder, NO_MAPS));
            return null;
        }

        List<Pair<Integer, String>> allGameMaps = new ArrayList<>();
        Arrays.asList(files).forEach(file ->
        {
            var index = extractMapNumber(file.getName());
            if (index != null)
                allGameMaps.add(new Pair<>(index, file.getName()));
        });

        Map<Integer, String> validGameMaps = new HashMap<>();
        ArrayList<Integer> conflictedIndicies = new ArrayList<>();
        allGameMaps.forEach(pair ->
        {
            var index = pair.item1();
            var filename = pair.item2();
            if (validGameMaps.containsKey(index))
                conflictedIndicies.add(index);
            else
                validGameMaps.put(index, String.valueOf(Paths.get(gameFolder, filename)));
        });

        if (validGameMaps.isEmpty())
        {
            LogManager.errorLog(String.format("%s - %s", gameFolder, NO_MAPS));
            return null;
        }

        // Check if there are multiple maps with the same number
        if (!conflictedIndicies.isEmpty())
        {
            List<String> conflictedFiles = new ArrayList<>();
            conflictedIndicies.forEach(conflictedIndex ->
                    allGameMaps.forEach(pair ->
                    {
                        var index = pair.item1();
                        var filename = pair.item2();
                        if (Objects.equals(index, conflictedIndex))
                            conflictedFiles.add(filename);
                    }));
            String errorFiles = String.join("; ", conflictedFiles);
            LogManager.errorLog(String.format("%s - %s: %s", gameFolder, CONFLICT_MAPS, errorFiles));
            return null;
        }
        return sortGameMap(validGameMaps);
    }

    private static List<GameMap> loadLevels(LinkedHashMap<Integer, String> levels)
    {
        boolean levelsValid = true;
        var sortedGameMaps = new ArrayList<GameMap>();
        for (var mapFile : levels.entrySet())
        {
            var filename = mapFile.getValue();
            var index = mapFile.getKey();
            var map = GameMapXmlParser.loadEntityFromXml(filename);
            if (!LevelChecker.checkMap(map, filename))
                levelsValid = false;
            if (levelsValid)
                sortedGameMaps.add(map);
        }
        if (!levelsValid)
            return null;
        return sortedGameMaps;
    }

    /** Extract the number of given xml filename */
    private static Integer extractMapNumber(String fileName)
    {
        // Check if file ends with .xml
        if (!fileName.endsWith(".xml"))
            return null;

        // Check if the map name starts with a number
        var split = fileName.split("[^-?0-9]");
        if (split.length == 0 || split[0].isEmpty()) return null;
        return Integer.parseInt(split[0]);
    }

    private static LinkedHashMap<Integer, String> sortGameMap(Map<Integer, String> map)
    {
        // Convert the map entries to a list
        List<Map.Entry<Integer, String>> entryList = new ArrayList<>(map.entrySet());

        // Sort the entries
        entryList.sort(Map.Entry.comparingByKey());

        // Create a new 'ordered' map to store the sorted entries
        LinkedHashMap<Integer, String> sortedMap = new LinkedHashMap<>();

        // Copy the sorted entries to the LinkedHashMap
        for (Map.Entry<Integer, String> entry : entryList)
            sortedMap.put(entry.getKey(), entry.getValue());

        return sortedMap;
    }

    /** Get all the levels */
    public List<GameMap> getLevels()
    {
        return this._levels;
    }

    public GameMap getLevel(int index)
    {
        return getLevels().get(index);
    }

    public int getNumLevels()
    {
        return getLevels().size();
    }
}
