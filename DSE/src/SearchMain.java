
import dse.*;

public class SearchMain extends SearchGui {
	 static final long serialVersionUID = 1;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SearchGui().setVisible(true);
            }
        });
	}
}

