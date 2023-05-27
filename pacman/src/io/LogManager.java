/**
 * Created by Stephen Zhang (Team 08)
 * <p>
 * Wraps GameCallback to prevent modifying it directly
 *
 * @see src.io.LogManager
 * @see src.io.GameCallback
 */

package src.io;

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
	private static final String ERR_LOG_FILE = "errorLog.txt";
	private static FileWriter _errorLogWriter = null;

	static
	{
		try
		{
			_errorLogWriter = new FileWriter(ERR_LOG_FILE);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void errorLog(String str)
	{
		try
		{
			_errorLogWriter.write(str);

			// TODO: DEBUG
			System.err.println(str);
			_errorLogWriter.write("\n");
			_errorLogWriter.flush();
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
