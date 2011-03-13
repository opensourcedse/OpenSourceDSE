
import java.nio.file.*;
import java.io.*;

import dse.*;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WatchDirMain {

	public static void watch() {
		try {
			WatchDir watchDir = new WatchDir();
	        watchDir.processEvents();
		}catch(Exception e){System.out.println(e.getMessage());}
	}
	public static void main(String[] args) {
		watch();
	}
}
