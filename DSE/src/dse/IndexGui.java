package dse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.awt.*;

public class IndexGui implements ActionListener {
	public JButton indexButton = new JButton("Index");
	public JButton browseButton = new JButton("Browse");
	public JFrame frame = new JFrame("Desktop Search Engine");
	public JTextField indexText = new JTextField(35);
	public JFileChooser fileChooser = new JFileChooser();
	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	
	public void go() {
		indexButton.addActionListener(this);
		browseButton.addActionListener(this);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		frame.getContentPane().add(BorderLayout.NORTH,panel1);
		frame.getContentPane().add(BorderLayout.SOUTH,panel2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel1.add(indexText);
		panel1.add(browseButton);
		panel2.add(indexButton);
		frame.setSize(500,200);
		frame.setVisible(true);
		indexText.requestFocus();
	}
	public void actionPerformed(ActionEvent event) {}

}
