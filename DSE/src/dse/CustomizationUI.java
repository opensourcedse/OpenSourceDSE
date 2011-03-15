package dse;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.util.*;
import org.apache.lucene.index.Term;
import java.nio.file.WatchKey;

public class CustomizationUI extends javax.swing.JFrame {
    
	
	public CustomizationUI() {
        initComponents();
        jCheckBoxHotKey.setSelected(ReadCustomizationFile.hotKey);
        jComboBoxInterval.setSelectedItem(ReadCustomizationFile.indexInterval);
        Iterator<String> it = ReadCustomizationFile.criticalDirectory.iterator();
        while (it.hasNext()) 
            modelCritical.addElement(it.next());
        it = ReadCustomizationFile.notIndexDirectory.iterator();
        while (it.hasNext()) 
            modelNotIndex.addElement(it.next());
        jButtonSave.setEnabled(false);
    }
    
    private void initComponents() {

    	modelCritical = new DefaultListModel<String>();
        modelNotIndex = new DefaultListModel<String>();
        jFileChooser  = new JFileChooser();
        jLabelCritical = new JLabel();
        jScrollPane1 = new JScrollPane();
        jListCritical = new JList<String>(modelCritical);
        jButtonCritical = new JButton();
        jScrollPane2 = new JScrollPane();
        jListNotIndex = new JList<String>(modelNotIndex);
        jButtonNotIndex = new JButton();
        jLabelNotIndex = new JLabel();
        jLabelInterval = new JLabel();
        jComboBoxInterval = new JComboBox();
        jButtonReIndex = new JButton();
        jCheckBoxHotKey = new JCheckBox();
        jButtonSave = new JButton();
        jButtonCancel = new JButton();
        jLabelInfo = new JLabel("Double Click on Directory name to Delete from the List");
        
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle("Customization Window");
        
        jButtonCritical.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonCriticalMouseClicked(evt);
            }
        });
        jButtonNotIndex.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonNotIndexMouseClicked(evt);
            }
        });
        jLabelCritical.setText("Critical Directories:");

        jScrollPane1.setViewportView(jListCritical);

        jButtonCritical.setText("Add Critcal Directory");

        jScrollPane2.setViewportView(jListNotIndex);

        jButtonNotIndex.setText("Add  Directory");

        jLabelNotIndex.setText("Directories Not to be Indexed:");

        jLabelInterval.setText("Re-Indexing Interval:");

        jComboBoxInterval.setModel(new DefaultComboBoxModel(new String[] { "1 Week", "2 Week", "3 Week", "4 Week" }));

        jButtonReIndex.setText("Re-Index Now");

        jCheckBoxHotKey.setText("  Enable HotKey");
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        jButtonSave.setText("Save");
        jButtonSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonSaveMouseClicked(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonCancelMouseClicked(evt);
            }
        });
        jListCritical.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	jListCriticalMouseClicked(evt);
            }
        });
        jListNotIndex.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	jListNotIndexMouseClicked(evt);
            }
        });
        jComboBoxInterval.addActionListener(jComboBoxListener);
           
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 361, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                            .add(jLabelInterval)
                            .add(18, 18, 18)
                            .add(jComboBoxInterval, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(18, 18, 18)
                            .add(jButtonReIndex, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                            .add(jLabelCritical)
                            .add(47, 47, 47)
                            .add(jButtonCritical))))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(56, 56, 56)
                        .add(jCheckBoxHotKey, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 196, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jLabelNotIndex)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonNotIndex, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 360, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
            	.addContainerGap()
            	.add(jLabelInfo)
                .addContainerGap(100, Short.MAX_VALUE)
                .add(jButtonSave)
                .add(18, 18, 18)
                .add(jButtonCancel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelCritical)
                    .add(jButtonCritical)
                    .add(jLabelNotIndex)
                    .add(jButtonNotIndex))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane2)
                    .add(jScrollPane1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelInterval, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBoxInterval, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonReIndex)
                    .add(jCheckBoxHotKey))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonCancel)
                    .add(jButtonSave)
                    .add(jLabelInfo))                    
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomizationUI().setVisible(true);
            }
        });
    }
   
    ActionListener jComboBoxListener = new ActionListener() {  
	    public void actionPerformed(ActionEvent e) {
	    	jButtonSave.setEnabled(true);
	    }  	   
	};
    private void jButtonCriticalMouseClicked(java.awt.event.MouseEvent evt) {
        int check = jFileChooser.showOpenDialog(this);
		if(check == JFileChooser.APPROVE_OPTION) {
			String file = jFileChooser.getSelectedFile().toString();
			if(!modelNotIndex.contains(file))  {
				if(!modelCritical.contains(file))
					modelCritical.addElement(file);
				else 
					JOptionPane.showMessageDialog(this, "The Directory already exists");
			}
			else 
				JOptionPane.showMessageDialog(this, "The Directory already exists in the other list");
			
		}
		jButtonSave.setEnabled(true);
    }
    private void jButtonNotIndexMouseClicked(java.awt.event.MouseEvent evt) {
        int check = jFileChooser.showOpenDialog(this);
		if(check == JFileChooser.APPROVE_OPTION) {
			String file = jFileChooser.getSelectedFile().toString();
			if(!modelCritical.contains(file))  {
				if(!modelNotIndex.contains(file))
					modelNotIndex.addElement(file);
				else 
					JOptionPane.showMessageDialog(this, "The Directory already exists");
			}
			else 
				JOptionPane.showMessageDialog(this, "The Directory already exists in the other list");	
		}
		jButtonSave.setEnabled(true);
    }
    
    private void jListCriticalMouseClicked(java.awt.event.MouseEvent evt) {
    	if (evt.getClickCount() == 2) {
    		modelCritical.removeElementAt(jListCritical.getSelectedIndex());
    	}
    	jButtonSave.setEnabled(true);
        
    }
    private void jListNotIndexMouseClicked(java.awt.event.MouseEvent evt) {
    	if (evt.getClickCount() == 2) {
    		modelNotIndex.removeElementAt(jListNotIndex.getSelectedIndex());
    	}
    	jButtonSave.setEnabled(true);        
    }
    private void jButtonSaveMouseClicked(java.awt.event.MouseEvent evt) {
    	try {
         createXML();
         updateDirectoryList();
    	}catch(Exception e){System.out.println(e.getMessage());}
    	jButtonSave.setEnabled(false);
    	
    }
    private void updateDirectoryList() throws Exception{
    	Set<String> oldCriticalDirectory = new HashSet<String>();
    	Set<String> oldNotIndexDirectory = new HashSet<String>();
    	Iterator<String> it = ReadCustomizationFile.criticalDirectory.iterator();
    	while(it.hasNext()) 
    		oldCriticalDirectory.add(it.next());
    	it = ReadCustomizationFile.notIndexDirectory.iterator();
    	while(it.hasNext()) 
    		oldNotIndexDirectory.add(it.next());
    	ReadCustomizationFile.main(null);
    	synchronized(InitializeWriter.writer) {
    		it = ReadCustomizationFile.notIndexDirectory.iterator();
            while (it.hasNext()) {
            	String temp = it.next();
            	if(!oldNotIndexDirectory.contains(temp)) {
            		updateIndex(temp.toString(),2);
            	}
            	
            }
            it = oldNotIndexDirectory.iterator();
            while (it.hasNext()) {
            	String temp = it.next();
            	if(!ReadCustomizationFile.notIndexDirectory.contains(temp)) {
            		updateIndex(temp.toString(),1);
            	}
            	
            }
            synchronized(WatchDir.watcher) {
            	synchronized(WatchDir.keys) {
                	it = ReadCustomizationFile.criticalDirectory.iterator();
                    while (it.hasNext()) {
                    	String temp = it.next();
                    	if(!oldCriticalDirectory.contains(temp)) {
                    		Path dir =	Paths.get(temp);
                        	if(Files.readAttributes(dir, BasicFileAttributes.class,NOFOLLOW_LINKS).isDirectory())
                        		WatchDir.registerAll(dir);
                        	else 
                        		WatchDir.register(dir);
                    	}
                    	
                    }
                    it = oldCriticalDirectory.iterator();
                    while (it.hasNext()) {
                    	String temp = it.next();
                    	if(!ReadCustomizationFile.criticalDirectory.contains(temp)) {
                    		Files.walkFileTree(Paths.get(temp), new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) {
                                    try {
                                    	Set<WatchKey> keys=WatchDir.keys.keySet();
                                    	Iterator<WatchKey> it1=keys.iterator();
                                    	while(it1.hasNext()){
                                    		WatchKey key = it1.next();
                                    		Path path = WatchDir.keys.get(key);
                                    		if(path.equals(dir)){
                                    			key.cancel();
                                    			WatchDir.keys.remove(key);
                                    			break;
                                    		}
                                    	}
                                    } catch (Exception x) {}
                                    return FileVisitResult.CONTINUE;            
                                }
                            });
                    	}
                    	
                    }
                }
            }
            
    	}
    	
    }
    private void updateIndex(String path,int opt){
    	try{
    		if(opt==1){
    			File file=new File(path);
            	if (file.isDirectory()) {
            		String[] files = file.list();
            		if (files != null) {
            			for (int i = 0; i < files.length; i++) {
            				updateIndex(files[i],2);
            		    }
            		}
            	}
            	else{
            		InitializeWriter.writer.addDocument(IndexFiles.getDocument(file));
            	}
    		}
    		else if(opt==2){
    			File file=new File(path);
            	if (file.isDirectory()) {
            		String[] files = file.list();
            		if (files != null) {
            			for (int i = 0; i < files.length; i++) {
            				updateIndex(files[i],2);
            		    }
            		}
            	}
            	else{
            		Term term=new Term("path",file.getCanonicalPath());
                	InitializeWriter.writer.deleteDocuments(term);
                	InitializeWriter.writer.commit();
                	InitializeWriter.writer.optimize();
            	}
    		}
    	}
    	catch(Exception e){}
    }
    
    
    private void jButtonCancelMouseClicked(java.awt.event.MouseEvent evt) {
    	this.setVisible(false);
    }
    
    private void createXML() throws Exception{
    	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    	Document document = documentBuilder.newDocument();
    	Element rootElement = document.createElement("optionFile");
    	document.appendChild(rootElement);
    	Element em = document.createElement("reIndexInterval");
        em.appendChild(document.createTextNode(jComboBoxInterval.getSelectedItem().toString()));
        rootElement.appendChild(em);
        em = document.createElement("hotKey");
        if(jCheckBoxHotKey.isSelected()) {
        	em.appendChild(document.createTextNode("true"));
        }
        else{
        	em.appendChild(document.createTextNode("false"));
        }
        rootElement.appendChild(em);
        em = document.createElement("criticalDirectory");
        rootElement.appendChild(em);
        for(int i=0;i<modelCritical.getSize();i++) {
        	Element em1 = document.createElement("directory");
        	em1.appendChild(document.createTextNode(modelCritical.elementAt(i).toString()));
        	em.appendChild(em1);
        }
        em = document.createElement("notIndexedDirectory");
        rootElement.appendChild(em);
        for(int i=0;i<modelNotIndex.getSize();i++) {
        	Element em1 = document.createElement("directory");
        	em1.appendChild(document.createTextNode(modelNotIndex.elementAt(i).toString()));
        	em.appendChild(em1);
        }
        
        StringWriter sw = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result =  new StreamResult(sw);
        transformer.transform(source, result);
        FileWriter fWriter = new FileWriter("optionFile.xml");
		BufferedWriter bufferWriter = new BufferedWriter(fWriter);
		bufferWriter.write(sw.toString());
		bufferWriter.close();		
    }

    private JFileChooser jFileChooser;
    DefaultListModel<String> modelCritical;
    DefaultListModel<String> modelNotIndex;
    private JButton jButtonCritical;
    private JButton jButtonNotIndex;
    private JButton jButtonReIndex;
    private JCheckBox jCheckBoxHotKey;
    private JComboBox jComboBoxInterval;
    private JLabel jLabelCritical;
    private JLabel jLabelNotIndex;
    private JLabel jLabelInterval;
    private JList<String> jListCritical;
    private JList<String> jListNotIndex;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JButton jButtonCancel;
    private JButton jButtonSave;
    private JLabel jLabelInfo;
    static final long serialVersionUID = 1;
}
