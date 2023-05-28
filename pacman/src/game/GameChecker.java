package src.game;

import src.Alistair;
import src.io.LogManager;

import java.io.File;
import java.util.*;

public class GameChecker
{
    private static final String DEFAULT_GAME_FOLDER = "test";
    private static final String NO_MAPS = "no maps found";
    private static final String CONFLICT_MAPS = "multiple maps at same level";

    /** Check the validity of a GameMap Folder and returns null if any check(s) failed.
     *  return a sorted LinkedHashMap that keep entry in order for the game level */
    public LinkedHashMap<Integer, String> checkGameFolder(String gameFolder)
    {
        File folder = new File(gameFolder);
        File[] files = folder.listFiles();

        // Check if there are no files in the folder
        if (files == null || files.length == 0)
        {
            LogManager.errorLog(String.format("%s - %s", gameFolder, NO_MAPS));
            return null;
        }

        Map<Integer, String> mappedIndex = new HashMap<>();
        Set<Integer> occupiedNumbers = new HashSet<>();
        ArrayList<String> conflictedFiles = new ArrayList<>();

        // Now check through the files in folder
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".xml"))
            {
                String fileName = file.getName();
                Integer mapNumber = extractMapNumber(fileName);

                // Check if the file name starts with a number and ends with ".xml"
                if (mapNumber != null)
                {
                    // Check if there is a duplicate map number
                    if (occupiedNumbers.contains(mapNumber))
                    {
                        Alistair.observeAll(String.format("[found DupNum=%d]", mapNumber));
                        conflictedFiles.add(fileName);
                    }
                    else
                    {
                        Alistair.observeAll(String.format("[found DupNum=%d]", mapNumber));
                        occupiedNumbers.add(mapNumber);
                        mappedIndex.put(mapNumber, String.format("%s/%s", gameFolder, fileName));
                    }
                }
            }
        }
        // Check if there are no maps in the folder
        if (occupiedNumbers.isEmpty())
        {
            LogManager.errorLog(String.format("%s - %s", gameFolder, NO_MAPS));
            return null;
        }
        // Check if there are multiple maps with the same number
        if (!conflictedFiles.isEmpty())
        {
            String errorFiles = String.join("; ", conflictedFiles);
            LogManager.errorLog(String.format("%s - %s: %s", gameFolder, CONFLICT_MAPS, errorFiles));
            return null;
        }
        return sortGameMap(mappedIndex);
    }

    /** Extract the number of given xml filename */
    private Integer extractMapNumber(String fileName)
    {
        String[] parts = fileName.split("\\.");
        String fileExtension = parts[parts.length - 1];

        // Only check for xml files
        if (fileExtension.equals("xml"))
        {
            String[] nameParts = fileName.split("\\.");
            String mapName = nameParts[0];

            // Check if the map name starts with a number
            if (mapName.matches("^\\d+.*"))
            {
                return Integer.parseInt(mapName.replaceAll("\\D+", ""));
            }
        }
        // if fileName not meet required pattern
        return null;
    }

    /** Overload for default game folder */
    public LinkedHashMap<Integer, String> checkGameFolder()
    { return checkGameFolder(DEFAULT_GAME_FOLDER); }


    private LinkedHashMap<Integer, String> sortGameMap(Map<Integer, String> map) {
        // Convert the map entries to a list
        List<Map.Entry<Integer, String>> entryList = new ArrayList<>(map.entrySet());

        // Sort the entries
        entryList.sort(Map.Entry.comparingByKey());

        // Create a new 'ordered' map to store the sorted entries
        LinkedHashMap<Integer, String> sortedMap = new LinkedHashMap<>();

        // Copy the sorted entries to the LinkedHashMap
        for (Map.Entry<Integer, String> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
