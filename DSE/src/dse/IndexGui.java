package dse;

import javax.swing.*;

import java.awt.*;

public class IndexGui extends javax.swing.JFrame {
    
    public IndexGui() {
        initComponents();
    }
    
    private void initComponents() {
    	jFileChooser  = new JFileChooser();
    	jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        indexText = new JTextField();
        indexButton = new JButton();
        cancelButton = new JButton();
        browseButton = new JButton();
       
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Index");

        indexButton.setText("Index");

        cancelButton.setText("Cancel");

        browseButton.setText("Browse");
        
        browseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	browseButtonMouseClicked(evt);
            }
        });
        indexButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                indexButtonMouseClicked(evt);
            }
        });
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(indexButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(indexText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 375, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(indexText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseButton))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(indexButton))
                .addContainerGap())
        );

        pack();
    }
  
    private void browseButtonMouseClicked(java.awt.event.MouseEvent evt) {
    	int check = jFileChooser.showOpenDialog(this);
		if(check == jFileChooser.APPROVE_OPTION) {
			String file = jFileChooser.getSelectedFile().toString();
			indexText.setText(file);
		}
    }

    private void indexButtonMouseClicked(java.awt.event.MouseEvent evt) {
    	IndexFiles.indexer(indexText.getText());
    }

    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {
    	try {
    		InitializeWriter.writer.close();
    	}catch(Exception e){}
    	System.exit(0);
    	
    }
    private JFileChooser jFileChooser;
    private JButton browseButton;
    private JButton cancelButton;
    private JButton indexButton;
    private JTextField indexText;
        
}
