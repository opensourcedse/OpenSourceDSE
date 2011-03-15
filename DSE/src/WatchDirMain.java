

import dse.*;

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
