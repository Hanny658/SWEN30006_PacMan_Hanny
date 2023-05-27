/**
 * Created by Stephen Zhang (Team 08)
 * <p>
 * Wraps GameCallback to prevent modifying it directly
 *
 * @see src.io.LogManager
 * @see src.io.GameCallback
 */

package src.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A static class used to reveal GameCallback available for any class at any point
 * <p>
 * This is a wrapper class intended not to touch the original GameCallback class.
 *
 * @see GameCallback
 */
public class LogManager
{
	private static final String errorLogFile = "errorLog.txt";
	private static FileWriter fileWriter = null;

	public LogManager()
	{
		try
		{
			fileWriter = new FileWriter(new File(errorLogFile));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	private static void logError(String str)
	{
		try
		{
			fileWriter.write(str);
			fileWriter.write("\n");
			fileWriter.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static GameCallback _callback = null;

	public static GameCallback getGameCallback()
	{
		return LogManager._callback;
	}

	public static void setGameCallback(GameCallback callback)
	{
		LogManager._callback = callback;
	}
}
