
import dse.*;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;

public class SearchMain extends SearchGui {
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == searchButton) {
			resultArea.setText("");
			resultArea.setCaretPosition(0);
			folder= queryText.getText();
			try {
				SearchGui.model.removeAllElements();
				SearchFiles.searchQuery(folder);
			}catch(Exception e){};
		}
		else if(event.getSource() == jump) {
			try {
				dse.SearchGui.resultArea.setText("");
				  dse.SearchGui.resultArea.setCaretPosition(0);
			dse.SearchFiles.nextPage(2);
		     }catch(Exception e){};
		}
		else if(event.getSource() == searchWRButton) {
			resultArea.append("\n\nSearching withing results....");
		}
		else if(event.getSource() == next) {
			try {
				dse.SearchGui.resultArea.setText("");
				  dse.SearchGui.resultArea.setCaretPosition(0);
			dse.SearchFiles.nextPage(1);
			}catch(Exception e){};
		}
		else if(event.getSource() == previous) {
			try {
				dse.SearchGui.resultArea.setText("");
				dse.SearchGui.resultArea.setCaretPosition(0);
			dse.SearchFiles.nextPage(-1);
		}catch(Exception e){};
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SearchMain g = new SearchMain();
		g.go();
	}
}

