package src.validation;

import src.validation.levelchecks.*;
import src.io.LogManager;
import src.models.GameMap;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LevelChecker
{
    private static final List<LevelCheck> _checks = new ArrayList<>();
    static
    {
        // Define what to check
        _checks.add(new FileFormatCheck());
        _checks.add(new PacManCheck());
        _checks.add(new PortalCheck());
        _checks.add(new GPNumCheck());
        _checks.add(new GPAccessibilityCheck());
        // ...
    }

    /** Checks the map and print to the game logfile */
    public static boolean checkMap(GameMap gameMap, String filename)
    {
        boolean passed = true;
        boolean checkForGPAccessibility = true;
        boolean fatalError = false;
        for (var check : _checks)
        {
            // No need to check for anything else if encounters fatal error
            if (fatalError)
                break;

            // Do not check Accessibility if failed any test before
            if (check instanceof GPAccessibilityCheck)
                if (!checkForGPAccessibility) continue;

            var result = check.check(gameMap);
            if (!result.success())
            {
                if (check instanceof FileFormatCheck)
                    fatalError = true;
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
                    LogManager.errorLog(String.format("%s - %s", Paths.get(filename).getFileName(), errorMessage));
                }
            }
        }
        // TODO: DEBUG
        if (passed)
            System.err.printf("Level check passed: %s\n", filename);
        return passed;
    }
}