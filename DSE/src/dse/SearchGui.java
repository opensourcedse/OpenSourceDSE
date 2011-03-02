package dse;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;



public class SearchGui implements ActionListener {
	 
	public final static DefaultListModel model = new DefaultListModel();
	public JButton searchButton = new JButton("Search");
	public JButton searchWRButton = new JButton("Search Within Results");
	public JButton next = new JButton("Next");
	public static JButton previous = new JButton("Previous");
	public JButton jump = new JButton("Jump");
	JFrame frame = new JFrame("Desktop Search Engine");
	public static JTextField queryText = new JTextField(15);
	public static JTextArea resultArea = new JTextArea(8,50);
	public static JList resultList = new JList(model);
	public static JScrollPane scrollerList = new JScrollPane(resultList);
	public static JScrollPane scrollerText = new JScrollPane(resultArea);
	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	public String folder;
	
	
/*	ListSelectionListener listner = new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent listSelectionEvent) {
			final File file = new File(resultList.getSelectedValue().toString());
			try {
			Desktop desktop = null;
			Desktop.Action action;
			desktop = Desktop.getDesktop();
			action = Desktop.Action.OPEN;
			desktop.open(file);
			}catch(Exception e){System.out.println(e.getMessage());}
		}
	};*/
	
	MouseListener mouseListener = new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
	        if (e.getClickCount() == 2) {
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
	};
	public void go() {
		searchButton.addActionListener(this);
		searchWRButton.addActionListener(this);
		next.addActionListener(this);
		previous.addActionListener(this);
		jump.addActionListener(this);
		//resultList.addListSelectionListener(listner);
		resultList.setCellRenderer(new DefaultListCellRenderer());
		resultList.addMouseListener(mouseListener);
		resultList.setVisibleRowCount(15);
		resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultList.setCellRenderer(new MyListRenderer());
		//resultList.setFixedCellWidth(500);
		frame.getContentPane().add(BorderLayout.NORTH,queryText);
		frame.getContentPane().add(BorderLayout.CENTER,panel1);
		frame.getContentPane().add(BorderLayout.SOUTH,panel2);
		//frame.getContentPane().add(BorderLayout.NORTH,searchButton);
		//frame.getContentPane().add(BorderLayout.SOUTH,searchWRButton);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		resultArea.setLineWrap(true);
		scrollerList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollerList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollerText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollerText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel1.add(scrollerList);
		panel1.add(scrollerText);
		panel2.add(searchButton);
		panel2.add(searchWRButton);
		panel2.add(next);
		panel2.add(previous);
		panel2.add(jump);
		frame.setSize(600,500);
		frame.setVisible(true);
		queryText.requestFocus();
		next.setEnabled(true);
		jump.setEnabled(true);
		previous.setEnabled(false);
		
	}
			
	public void actionPerformed(ActionEvent event) {}
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

