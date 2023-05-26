package src.models;

import ch.aplu.jgamegrid.Location;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "level")
public class GameMap
{
	private ArrayList<Row> _rows;

	private Size _size;

	public Size getSize() { return _size; }
	@XmlElement(name = "size")

	public void setSize(Size size) { _size = size; }

	public ArrayList<Row> getRows() { return _rows; }

	@XmlElement(name = "row")
	public void setRows(ArrayList<Row> rows) { _rows = rows; }

	public static class Size
	{
		private int _width;

		private int _height;

		public int getWidth() { return _width; }

		@XmlElement(name = "width")
		public void setWidth(int width) { _width = width; }

		public int getHeight() { return _height; }

		@XmlElement(name = "height")
		public void setHeight(int height) { _height = height; }
	}

	public static class Row
	{

		private ArrayList<String> _cells;

		@XmlElement(name = "cell")
		public ArrayList<String> getCells() { return _cells; }

		public void setCells(ArrayList<String> cells) { _cells = cells; }
	}
}
