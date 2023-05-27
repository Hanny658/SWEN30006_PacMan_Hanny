package src.game;

import ch.aplu.jgamegrid.Location;
import src.game.levelchecks.LevelCheck;
import src.game.levelchecks.PacManCheck;
import src.io.LogManager;
import src.models.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelChecker
{
    private static final int LEAST_GP = 2;    // least number of golds and pills
    private static final List<LevelCheck> _checks;
    static
    {
        _checks = new ArrayList<>();
        // Define what to check
        _checks.add(new PacManCheck());
        // ...
    }

    /** Checks the map and print to the game logfile */
    public static boolean checkMap(Map<Entity, Location> entities, String filename)
    {
        for (var check : _checks)
        {
            var result = check.check(entities);
            if (!result.success())
            {
                String errorMessage = result.errorMessage();
                if (result.errorLocations() != null)
                {
                    errorMessage += ": ";
                    var size = result.errorLocations().size();
                    var deliminator = "; ";
                    for (int i = 0; i < size; i++)
                    {
                        var errorLocation = result.errorLocations().get(i);
                        errorMessage += String.format("(%d,%d)%s",
                                errorLocation.x + 1, errorLocation.y + 1, i < size - 1 ? deliminator : "");
                    }
                }
                LogManager.errorLog(String.format("%s - %s", filename, errorMessage));
                // [sample] Level 1_mapname.xml – portal White count is not 2: (2,3); (6,7); (1,8)
                return false;
            }
        }
        // TODO: DEBUG
        System.out.printf("Level check passed.");
        return true;
    }
}