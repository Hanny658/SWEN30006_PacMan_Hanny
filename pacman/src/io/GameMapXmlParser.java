package src.io;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import src.models.GameMap;
import src.models.GameMapSchema;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 */
class GameMapXmlParser
{
	/** Load to a map of entities and their locations from XML */
	public static GameMap loadEntityFromXml(String fileName)
	{
		try
		{
			File xmlFile = new File(fileName);

			JAXBContext jaxbContext = JAXBContext.newInstance(GameMapSchema.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			GameMapSchema map = (GameMapSchema) unmarshaller.unmarshal(xmlFile);
			return GameMap.fromGameMapSchema(map);
		}
		catch (Exception e)
		{
		}
		return null;
	}
}
