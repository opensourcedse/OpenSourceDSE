package dse;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.awt.TrayIcon;
import java.awt.SystemTray;


public class SearchGui extends JFrame {
    
    public SearchGui() {
        initComponents();
       
    }
    
    private void initComponents() {
    	model = new DefaultListModel();
        jLabel1 = new JLabel();
        queryText = new JTextField();
        searchButton = new JButton();
        nextButton = new JButton();
        previousButton = new JButton();
        cancelButton = new JButton();
        jScrollPane1 = new JScrollPane();
        resultList = new JList(model);
        jScrollPane2 = new JScrollPane();
        resultArea = new JTextArea();
        
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
       
        setTitle("Search GUI");
        resultList.setCellRenderer(new MyListRenderer());
        jLabel1.setText("Enter Text:");
        nextButton.setEnabled(false);
        previousButton.setEnabled(false);

        searchButton.setText("Find");
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchButtonMouseClicked(evt);
            }
        });
        
        queryText.addKeyListener(new MyKeyListener());

        nextButton.setText("Next");
        nextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextButtonMouseClicked(evt);
            }
        });

        previousButton.setText("Previous");
        previousButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                previousButtonMouseClicked(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(resultList);

        resultArea.setColumns(20);
        resultArea.setRows(5);
        jScrollPane2.setViewportView(resultArea);
        
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultListMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(searchButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(nextButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(26, 26, 26)
                                .add(previousButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                                .add(26, 26, 26)
                                .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(queryText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(queryText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(searchButton)
                    .add(cancelButton)
                    .add(nextButton)
                    .add(previousButton))
                .add(18, 18, 18)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }
 
    private void searchButtonMouseClicked(java.awt.event.MouseEvent evt) {
    	resultArea.setText("");
		resultArea.setCaretPosition(0);
		String folder= queryText.getText();
		try {
			model.removeAllElements();
			SearchFiles.searchQuery(folder);
		}catch(Exception e){};
    }
    
   
    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {
    		this.setVisible(false);
    	
    }

    private void viewFrame()  {
    	this.setVisible(true);
    }
    
    private void nextButtonMouseClicked(java.awt.event.MouseEvent evt) {
    	try {
			resultArea.setText("");
			resultArea.setCaretPosition(0);
			SearchFiles.nextPage(2);
	     }catch(Exception e){};
    }

    private void previousButtonMouseClicked(java.awt.event.MouseEvent evt) {
    	try {
			resultArea.setText("");
			resultArea.setCaretPosition(0);
			SearchFiles.nextPage(1);
		}catch(Exception e){}
    }
    
    private void resultListMouseClicked(java.awt.event.MouseEvent evt) {
    	if (evt.getClickCount() == 2) {
        	JPanel panel = (JPanel) resultList.getSelectedValue();
        	JLabel label = (JLabel) panel.getComponent(0);
            final File file = new File(label.getText());
            
			try {
			Desktop desktop = null;
			//Desktop.Action action;
			desktop = Desktop.getDesktop();
			//action = Desktop.Action.OPEN;
			desktop.open(file);
			}catch(Exception et){System.out.println(et.getMessage());}
		}
    }
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    public static JButton cancelButton;
    public static JButton nextButton;
    public static JButton previousButton;
    public static JTextField queryText;
    public static JTextArea resultArea;
    private JList resultList;
    private JButton searchButton;
    static DefaultListModel model;
    TrayIcon trayIcon;
    SystemTray tray; 
  
    public class MyKeyListener extends KeyAdapter{
        public void keyPressed(KeyEvent ke){
        	if(ke.getKeyCode() == 10) {
        		resultArea.setText("");
        		resultArea.setCaretPosition(0);
        		String folder= queryText.getText();
        		try {
        			model.removeAllElements();
        			SearchFiles.searchQuery(folder);
        		}catch(Exception e){};        			
        	}
        }
      }  
    
    
	 class MyListRenderer implements ListCellRenderer {
		   public Component getListCellRendererComponent(JList jlist,
		                                                 Object value,
		                                                 int cellIndex,
		                                                 boolean isSelected,
		                                                 boolean cellHasFocus)
		   {
		     if (value instanceof JPanel)
		     {
		       Component component = (Component) value;
		       component.setForeground (Color.white);
		       if(isSelected) {
		    	   component.setBackground (Color.lightGray);
		       }
		       else {
		    	   component.setBackground (Color.white);
		       }
		    	   
		       return component;
		     }
		     else
		     {
		       return new JLabel("???");
		     }
		   }
		  }
		   
}

