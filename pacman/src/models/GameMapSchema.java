package src.models;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;

/**
 * A model of XML schema for deserialising game map XML file
 */
@XmlRootElement(name = "level")
public class GameMapSchema
{
	private static final String SIZE_NODE = "size";
	private static final String WIDTH_NODE = "width";
	private static final String HEIGHT_NODE = "height";
	private static final String ROW_NODE = "row";
	private static final String CELL_NODE = "cell";

	private ArrayList<Row> _rows;

	private Size _size;

	public Size getSize() { return _size; }
	@XmlElement(name = SIZE_NODE)

	public void setSize(Size size) { _size = size; }

	public ArrayList<Row> getRows() { return _rows; }

	@XmlElement(name = ROW_NODE)
	public void setRows(ArrayList<Row> rows) { _rows = rows; }

	public static class Size
	{
		private int _width;

		private int _height;

		public int getWidth() { return _width; }

		@XmlElement(name = WIDTH_NODE)
		public void setWidth(int width) { _width = width; }

		public int getHeight() { return _height; }

		@XmlElement(name = HEIGHT_NODE)
		public void setHeight(int height) { _height = height; }
	}

	public static class Row
	{

		private ArrayList<String> _cells;

		@XmlElement(name = CELL_NODE)
		public ArrayList<String> getCells() { return _cells; }

		public void setCells(ArrayList<String> cells) { _cells = cells; }
	}
}
