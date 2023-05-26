package src.io;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.xml.XMLConstants;
import java.io.File;
import java.io.IOException;

public class GameMapXmlParser
{
	private static final String SIZE_NODE = "size";
	private static final String WIDTH_NODE = "width";
	private static final String HEIGHT_NODE = "height";
	private static final String ROW_NODE = "row";
	private static final String CELL_NODE = "cell";
	public static String TEST_MAP = "moffat";
	public void test() throws IOException, JDOMException
	{
		SAXBuilder builder = new SAXBuilder();
		builder.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		builder.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

		Document xmlDocument = builder.build(new File(TEST_MAP));
		Element root = xmlDocument.getRootElement();

		for (var rowObject : root.getChildren(ROW_NODE))
		{
			Element row = (Element) rowObject;
			for (var cellObject : row.getChildren(CELL_NODE))
			{
				Element cell = (Element) cellObject;
				System.out.println(cell.getValue());
			}
		}
	}
}
