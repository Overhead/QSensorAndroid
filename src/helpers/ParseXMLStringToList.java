package helpers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import classes.Movie;

public class ParseXMLStringToList {
	
	public static List<Movie> getMoviesForAgeAndGender(String xml, String gender, String age) {
		List<Movie> movieList = new ArrayList<Movie>();
		
		Document xmlDocument = XMLfromString(xml);
		
		NodeList nodes = xmlDocument.getElementsByTagName("item");
		
		for(int i=0; i<nodes.getLength();i++){
			Movie m = new Movie();
			Element e = (Element)nodes.item(i);
			
			m.setMovieName(XMLFunctions.getValue(e, "nameFilm"));
			m.setAge(age);
			m.setGender(gender);
			m.setAverageEda(Double.parseDouble(XMLFunctions.getValue(e, "EmoLvl")));
			
			movieList.add(m);
			
		}

		return movieList;
	}
	
	public static List<Movie> getMoviesFromImdbXMLByName(String xml, String movieName){
		List<Movie> movieList = new ArrayList<Movie>();
		
		Document xmlDocument = XMLfromString(xml);
		
		NodeList nodes = xmlDocument.getElementsByTagName("item");
		
		for(int i=0; i<nodes.getLength();i++){
			Movie m = new Movie();
			Element e = (Element)nodes.item(i);
			
			m.setMovieName(XMLFunctions.getValue(e, "title"));
			m.setAge("0");
			m.setAverageEda(0);
			m.setGender("N/A");
			m.setProductionYear(Integer.parseInt((XMLFunctions.getValue(e, "year"))));
			m.setImdbId(XMLFunctions.getValue(e, "imdbId"));
			
			movieList.add(m);
			
		}

		return movieList;
	}
	
	public static Document XMLfromString(String xml){

		Document doc = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is); 

		} catch (ParserConfigurationException e) {
			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}

		return doc;

	}
	
}
