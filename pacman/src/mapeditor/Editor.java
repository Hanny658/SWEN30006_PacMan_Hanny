package src.mapeditor;

import java.io.File;

public class Editor
{
	public static void run()
	{
		new src.mapeditor.editor.Controller();
	}
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
