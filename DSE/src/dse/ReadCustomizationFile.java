package dse;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadCustomizationFile {

	public static Set<String> criticalDirectory = new HashSet<String>();
	public static Set<String> notIndexDirectory = new HashSet<String>();
	public static String indexInterval = new String();
	public static boolean hotKey;
	
	public static void main(String[] args) {
		try {
        	File fXmlFile = new File("optionFile.xml");
        	if(fXmlFile.exists()){
        		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            	Document doc = dBuilder.parse(fXmlFile);
            	doc.getDocumentElement().normalize();
            	NodeList nList=doc.getElementsByTagName("criticalDirectory");
            	Node nNode = (Node) nList.item(0);
            	Element eElement = (Element) nNode;
            	nList= eElement.getElementsByTagName("directory");
            	criticalDirectory.clear();
        	    for(int i=0;i<nList.getLength();i++){
        	    	Node nValue = (Node) nList.item(i);
        	    	criticalDirectory.add(nValue.getTextContent());
        	    } 
        	    nList = doc.getElementsByTagName("notIndexedDirectory");
            	nNode = (Node) nList.item(0);
            	eElement = (Element) nNode;
            	nList= eElement.getElementsByTagName("directory");
            	notIndexDirectory.clear();
        	    for(int i=0;i<nList.getLength();i++){
        	    	Node nValue = (Node) nList.item(i);
        	    	notIndexDirectory.add(nValue.getTextContent());
        	    } 
        	    nList=doc.getElementsByTagName("reIndexInterval");
            	nNode = (Node) nList.item(0);
            	indexInterval = nNode.getTextContent();
            	nList = doc.getElementsByTagName("hotKey");
            	nNode = (Node) nList.item(0);
            	if(nNode.getTextContent().equalsIgnoreCase("true")){
            		hotKey = true;
            	}
            	else{
            		hotKey = false;
            	}
        	}
		}catch(Exception e){}
		
	}

}
