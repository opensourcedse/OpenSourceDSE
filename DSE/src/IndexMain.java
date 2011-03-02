
import dse.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;

public class IndexMain extends IndexGui {
	String file;
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == browseButton) {
			int check = fileChooser.showOpenDialog(frame);
			if(check == fileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile().toString();
				indexText.setText(file);
			}
		}
		else if(event.getSource() == indexButton) {
			dse.IndexFiles.indexer(file);
		}
			
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IndexMain g = new IndexMain();
		g.go();
	}

}
