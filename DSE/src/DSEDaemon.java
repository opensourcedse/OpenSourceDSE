
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import dse.*;

public class DSEDaemon extends Thread {
	
	static TrayIcon trayIcon;
	static SystemTray tray;
	DSEDaemon(String name) {
		this.setName(name);
	}
	public void run() {
		
		System.out.println(Thread.currentThread());
		if(this.getName().equalsIgnoreCase("IndexMain"))
			IndexMain.main(null);
			else if(this.getName().equalsIgnoreCase("WatchDirMain")) 
				WatchDirMain.main(null);
			else if(this.getName().equalsIgnoreCase("InitializeWriter")) {
				InitializeWriter.main(null);
			}
			else if(this.getName().equalsIgnoreCase("CustomizationMain")) {
				CustomizationMain.main(null);
			}
			else if(this.getName().equalsIgnoreCase("SearchMain")) {
				SearchMain.main(null);
			}
			else if(this.getName().equalsIgnoreCase("ReadCustomizationFile")) {
				ReadCustomizationFile.main(null);
			}
	}

	ActionListener exitListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        System.out.println("Exiting...");
	        System.exit(0);
	    }
	};
	ActionListener actionListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	        trayIcon.displayMessage("Action Event","An Action Event Has Been Performed!",
	                TrayIcon.MessageType.INFO);
	    }
	};
	ActionListener customizeListener = new ActionListener() {  
	    public void actionPerformed(ActionEvent e) {
	    	DSEDaemon thread4 = new DSEDaemon("CustomizationMain");
	    	thread4.start();
	    }  	   
	};  
	ActionListener indexListener = new ActionListener() {  
	    public void actionPerformed(ActionEvent e) {
	    	DSEDaemon thread3 = new DSEDaemon("IndexMain");
	    	thread3.start();
	    }  	   
	};
	ActionListener searchListener = new ActionListener() {  
	    public void actionPerformed(ActionEvent e) {
	    	DSEDaemon thread5 = new DSEDaemon("SearchMain");
	    	thread5.start();
	    }  	   
	}; 
	
	MouseListener mouseListener = new MouseListener() {
        
        public void mouseClicked(MouseEvent e) {
            System.out.println("Tray Icon - Mouse clicked!");  
        }

        public void mouseEntered(MouseEvent e) {
            System.out.println("Tray Icon - Mouse entered!");                 
        }

        public void mouseExited(MouseEvent e) {
            System.out.println("Tray Icon - Mouse exited!");                 
        }

        public void mousePressed(MouseEvent e) {
            System.out.println("Tray Icon - Mouse pressed!");                 
        }

        public void mouseReleased(MouseEvent e) {
            System.out.println("Tray Icon - Mouse released!");                 
        }
    };
	public void go() {
		try {
			if (SystemTray.isSupported()) {
		    tray = SystemTray.getSystemTray();
		    Image image = Toolkit.getDefaultToolkit().getImage("tray.gif");
		    PopupMenu popup = new PopupMenu();
		    MenuItem exit = new MenuItem("Exit");
		    MenuItem customize = new MenuItem("Options");
		    MenuItem index = new MenuItem("Re-Index");
		    MenuItem search = new MenuItem("Search");
		    MenuItem indexStatus = new MenuItem("IndexStatus");
		    MenuItem about = new MenuItem("About");
		    exit.addActionListener(exitListener);
		    customize.addActionListener(customizeListener);
		    index.addActionListener(indexListener);
		    search.addActionListener(searchListener);
		    popup.add(search);
		    popup.addSeparator();
		    popup.add(customize);
		    popup.add(index);
		    popup.add(indexStatus);
		    popup.addSeparator();
		    popup.add(about);
		    popup.add(exit);
		    trayIcon = new TrayIcon(image,"Tray Demo",popup);
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addActionListener(actionListener);
		    trayIcon.addMouseListener(mouseListener);
		    try {
		      	int i;
		      	TrayIcon[] temp = tray.getTrayIcons();
		       	for(i=0;i<temp.length;i++) {
		       		if(temp[i].getToolTip().equals(trayIcon.getToolTip()))
		       			break;
		       	}
		       	if(i == temp.length)
		    	tray.add(trayIcon);
		    } catch (AWTException e) {
		    	System.err.println("TrayIcon could not be added.");}
			}
			else {
				System.out.println("System Tray is not Supported");
			}
		}catch(Exception e){}
}
	public static void main(String[] args) {
		new DSEDaemon("hello").go();
		DSEDaemon thread1 = new DSEDaemon("InitializeWriter");
		DSEDaemon thread2 = new DSEDaemon("ReadCustomizationFile");
		DSEDaemon thread6 = new DSEDaemon("WatchDirMain");
		thread1.start();
		thread2.start();
		thread6.start();
	}
}
