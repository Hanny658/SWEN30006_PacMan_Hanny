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

    public boolean checkGameFolder(String gameFolder)
    {
        File folder = new File(gameFolder);
        File[] files = folder.listFiles();

        // Check if there are no maps in the folder
        if (files == null || files.length == 0) { return false; }

        Set<Integer> occupiedNumbers = new HashSet<>();
        Set<String> conflictedFiles = new HashSet<>();

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
                    }
                }
            }
        }
        // Check if there are no maps in the folder
        if (occupiedNumbers.isEmpty())
        {
            LogManager.errorLog(String.format("%s - %s", gameFolder, NO_MAPS));
            return false;
        }
        // Check if there are multiple maps with the same number
        if (!conflictedFiles.isEmpty())
        {
            String errorFiles = String.join("; ", conflictedFiles);
            LogManager.errorLog(String.format("%s - %s: %s", gameFolder, CONFLICT_MAPS, errorFiles));
            return false;
        }
        return true;
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
    public boolean checkGameFolder()
    { return checkGameFolder(DEFAULT_GAME_FOLDER); }
}
