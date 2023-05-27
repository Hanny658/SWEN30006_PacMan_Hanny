package src.game;

import ch.aplu.jgamegrid.Location;
import src.game.levelchecks.*;
import src.io.LogManager;
import src.models.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelChecker
{
    private static final List<LevelCheck> _checks;
    static
    {
        _checks = new ArrayList<>();
        // Define what to check
        _checks.add(new PacManCheck());
        _checks.add(new PortalCheck());
        _checks.add(new GPNumCheck());
        // ...
    }

    /** Checks the map and print to the game logfile */
    public static boolean checkMap(Map<Location, Entity> entities, String filename)
    {
        boolean passed = true;
        boolean checkForGPAccessibility = true;
        for (var check : _checks)
        {
            // Do not check Accessibility if failed any test before
            if (check instanceof GPAccessibilityCheck)
                if (!checkForGPAccessibility) continue;

            var result = check.check(entities);
            if (!result.success())
            {
                // GPNumCheck won't affect GPAccessibilityCheck
                if (!(check instanceof GPNumCheck))
                    checkForGPAccessibility = false;

                passed = false;
                for (var error : result.errors())
                {
                    String errorMessage = error.errorMessage();
                    if (error.errorLocations() != null)
                    {
                        errorMessage += ": ";
                        var size = error.errorLocations().size();
                        var deliminator = "; ";
                        for (int i = 0; i < size; i++)
                        {
                            var errorLocation = error.errorLocations().get(i);
                            errorMessage += String.format("(%d,%d)%s",
                                    errorLocation.x + 1, errorLocation.y + 1, i < size - 1 ? deliminator : "");
                        }
                    }
                    LogManager.errorLog(String.format("%s - %s", filename, errorMessage));
                    // [sample] Level 1_mapname.xml – portal White count is not 2: (2,3); (6,7); (1,8)
                }
            }
        }
        // TODO: DEBUG
        if (passed)
            System.out.printf("Level check passed.");
        return passed;
    }
}