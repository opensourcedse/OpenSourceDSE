
import dse.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;

public class IndexMain extends IndexGui {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IndexGui().setVisible(true);
            }
        });
	}

}
