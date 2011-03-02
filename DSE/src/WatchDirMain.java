
import java.nio.file.*;
import java.io.*;
import dse.*;
import java.nio.file.Paths;

public class WatchDirMain {

	public static void watch() throws IOException{
		Path dir = Paths.get("//home//prashant//2");
		WatchDir watchDir = new WatchDir(dir,true);
		watchDir.processEvents();
	}
	public static void main(String[] args) throws IOException {
		watch();
	}
}
