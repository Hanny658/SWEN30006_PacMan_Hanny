/**
 * Created by Stephen Zhang & Hanny Zhang (Team 08)
 */

package src.mapeditor;

import java.io.File;

/**
 * A helper class to start the editor with blank map or loaded map
 */
public class Editor
{
	/**
	 * Run the editor with no map file (blank workspace)
	 */
	public static void run() { new src.mapeditor.editor.Controller(); }

	/**
	 * Run the editor with a map loaded (it will load blank if the map is not valid)
	 * @param file the file object of the map
	 */
	public static void run(File file)
	{
		var controller = new src.mapeditor.editor.Controller();
		try
		{
			controller.loadFromFile(file);
		}
		catch (Exception e)
		{
			// Ignore invalid map
		}
	}
}
