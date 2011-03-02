package dse;

import java.awt.FlowLayout;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import java.awt.Image;
import java.io.File;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.FuzzyTermEnum;
import org.apache.lucene.index.Term;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

	
public class SearchFiles {
	 static ScoreDoc[] hits; 
	 static int start = 0;
	 static int end = 0;
	 static int numTotalHits;
	 static Searcher searcher;
	 static int hitsPerPage = 10;
	 static TopScoreDocCollector collector;
	 static  Query query;
	 static QueryParser parser;
	 
  private SearchFiles() {}
 
   
  public static int searchQuery(String line) throws Exception {
    
    String index = "index";
    String field = "contents";
    int flag=0;
    IndexReader reader = IndexReader.open(FSDirectory.open(new File(index)), true); // only searching, so read-only=true
    
     if (line == null || line.length() == -1)
    	 return 0;
     
      line = line.trim();
      if (line.length() == 0)
        return 0;

      if(line.contains(":")) {
    	  int position;
    	  position=line.indexOf(":");
    	  String abd = new String();
    	  field=line.substring(0,position);
    	  line=line.substring(position+1);
    	  line = line.toLowerCase();
      }
      searcher = new IndexSearcher(reader);
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
      parser = new QueryParser(Version.LUCENE_CURRENT, field, analyzer);
      
      SearchGui.resultArea.append("Searching for: " + line+"\n");

      if(line.contains(" ")) {
        	PhraseQuery phraseQuery = new PhraseQuery();
        	int start=0,end;
        	String key1 = line;
        	key1 = key1.toLowerCase();
        	end=key1.indexOf(" ");
        	do {
        		phraseQuery.add(new Term(field,key1.substring(start,end)));
        		start = end+1;
        		key1 = key1.substring(start);
        		key1=key1.toLowerCase();
        		end = key1.indexOf(" ");
        		start = 0;
        	}while(end != -1);
        	phraseQuery.add(new Term(field,key1));
        	query = phraseQuery;
        	flag=doPagingSearch();
        }
        if (flag==0) {
        	query = parser.parse(line);
        	flag=doPagingSearch();
        }
        if(flag==0) {
        	String keyword = line;
        	keyword = keyword.concat("*");
        	keyword = "*".concat(keyword);
        	query = new WildcardQuery(new Term(field,keyword));
        	flag = doPagingSearch();
        }
      
        if(flag==0) {
        	Term term = new Term(field,line);
        	FuzzyTermEnum fuzzyTermEnum = new FuzzyTermEnum(reader,term);
        	if(fuzzyTermEnum.term() != null) {
        		String newKey = fuzzyTermEnum.term().text();
        		query = parser.parse(newKey);
        		flag=doPagingSearch();
        		if(flag != 0) {
        			dse.SearchGui.resultArea.append("\nNo Matching documents found for query :\""+ line+"\"");
        			dse.SearchGui.resultArea.append("\nDid you mean : \""+newKey+"\"");
        			dse.SearchGui.resultArea.append("\n"+ numTotalHits+"total matching documents");
        		}
        		else {
            		dse.SearchGui.resultArea.append("\n No matching documents found for query: \""+line+"\"");
            	}
        	}
        	fuzzyTermEnum.close(); 
        }
    reader.close();
    return flag;
  }
  
  public static int doPagingSearch() throws IOException {
 
    // Collect enough docs to show 5 pages
    collector = TopScoreDocCollector.create(5 * hitsPerPage, false);
    searcher.search(query, collector);
    hits = collector.topDocs().scoreDocs;
    numTotalHits = collector.getTotalHits();
    SearchGui.resultArea.append("\n"+numTotalHits + " total matching documents");

    start = 0;
    end = Math.min(numTotalHits, hitsPerPage);   
    nextPage(0);
    if(numTotalHits==0) 
    	return 0;
    else 
    	return 1;
  }

  public static void nextPage(int opt) throws IOException {
	  if (opt == -1) {
	    start = Math.max(0, start - hitsPerPage);
	  } else if (opt == 1) { 
		  if(start + hitsPerPage < numTotalHits) {
	       start+=hitsPerPage;
	     } 
	  }
	  else if(opt == 2) {
		 if(((Integer.parseInt(dse.SearchGui.queryText.getText()) - 1) * hitsPerPage) <= numTotalHits ) {
			 start = hitsPerPage * (Integer.parseInt(dse.SearchGui.queryText.getText()) - 1) ;
		 }
		 else {
			 dse.SearchGui.resultArea.append("No such Page exists"); 
		 }		
	  }
	  if(opt !=0) {
		  SearchGui.model.removeAllElements();
	  }
		  
	  end = Math.min(numTotalHits, start + hitsPerPage);
		 if (end > hits.length) {
	       	collector = TopScoreDocCollector.create(numTotalHits, false);
	     	searcher.search(query, collector);
	     	hits = collector.topDocs().scoreDocs;
	     }
		 end = Math.min(hits.length, start + hitsPerPage);
	  for (int i = start; i < end; i++) {
	        Document doc = searcher.doc(hits[i].doc);
	        String path = doc.get("path");
	        String filePath = new String();
	  	    int position = path.lastIndexOf(".");
	  	    String fileType = path.substring(position);
            File file = File.createTempFile("tempFile", fileType);
            fileType = doc.get("Content-Type");
            FileSystemView view = FileSystemView.getFileSystemView();
	        if (path != null) {
	        	if(fileType.contains("image")) {
	        		filePath = path;
	        		ImageIcon icon = new ImageIcon(filePath);
	        	    Image im = icon.getImage();
	        		im = im.getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH);
	        		icon = new ImageIcon(im);
	        		JLabel label = new JLabel(path,icon,JLabel.LEFT);
	        		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	        		panel.add(label);
	        		SearchGui.model.addElement(panel);
	        	}
	        	else {
	        		try {
	        		    ImageIcon icon = (ImageIcon) view.getSystemIcon(file);
	            		JLabel label = new JLabel(path,icon,JLabel.LEFT);
	            		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	            		panel.add(label);
	            		SearchGui.model.addElement(panel);
	            		file.delete();
	        		}catch(Exception e){System.out.println(e.getMessage());}
	        	}
	          String title = doc.get("title");
	          if (title != null) {
	            SearchGui.resultArea.append("\n   Title: " + doc.get("title"));
	          }
	        } else {
	        	SearchGui.resultArea.append("\n"+(i+1) + ". " + "No path for this document");
	        }
	        if (numTotalHits >= end) {
		          if (start - hitsPerPage >= 0) {
		              dse.SearchGui.previous.setEnabled(true);
		          }
		          if (start + hitsPerPage < numTotalHits) {
		        	  dse.SearchGui.previous.setEnabled(true);
		          }
		      } 
	      }
	  dse.SearchGui.resultArea.append("\n\nEnter the page number and Press next to jump");
  }
  
}