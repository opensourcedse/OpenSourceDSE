package dse;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import java.io.*;
import java.util.*;
import org.apache.tika.metadata.*;
import org.apache.tika.parser.*;
import org.xml.sax.ContentHandler;
import org.apache.tika.sax.*;
import org.apache.lucene.document.*;
import org.apache.tika.config.TikaConfig;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import org.apache.commons.io.FileUtils;


public class IndexFiles {
  
  IndexFiles() {}
  public static boolean flag = false;
  static double percentageIndex = 0;
  static double totalIndex;
  static double percentage = 1;
  static Set<String> textualMetadataFields = new HashSet<String>();
  static {
	  textualMetadataFields.add(Metadata.TITLE);
	  textualMetadataFields.add(Metadata.AUTHOR);
	  textualMetadataFields.add(Metadata.COMMENTS);
	  textualMetadataFields.add(Metadata.KEYWORDS);
	  textualMetadataFields.add(Metadata.DESCRIPTION);
	  textualMetadataFields.add(Metadata.SUBJECT);
	  textualMetadataFields.add(Metadata.RESOURCE_NAME_KEY);
  }

  public static void indexer(String folder) {
    final File docDir = new File(folder);
    if (!docDir.exists() || !docDir.canRead()) {
      System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
   
    Date start = new Date();
    try {
    	
    	InitializeWriter.writer.setMergeFactor(2);
        System.out.println("Indexing to directory '" +InitializeWriter.INDEX_DIR+ "'...");
        if(docDir.isDirectory())
        	totalIndex = FileUtils.sizeOfDirectory(docDir);
        indexDocs(InitializeWriter.writer, docDir);
        System.out.println("Optimizing...");
        InitializeWriter.writer.optimize();
        InitializeWriter.writer.close();
        Date end = new Date();
        System.out.println(end.getTime() - start.getTime() + " total milliseconds");
    } catch (IOException e) {
        System.out.println(" caught a " + e.getClass() +
        "\n with message: " + e.getMessage());
    }
  }

  public static Document getDocument(File f) throws Exception {
	  Metadata metadata = new Metadata();
	  metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
	  InputStream is = new FileInputStream(f);
	  Parser parser = new AutoDetectParser();
	  ContentHandler handler = new BodyContentHandler();
	  ParseContext context = new ParseContext();
	  context.set(Parser.class, parser);
	  try {
		  parser.parse(is, handler, metadata,context);
	  } finally {
		  is.close();
	  }
	  Document doc = new Document();
	  doc.add(new Field("contents", handler.toString(),Field.Store.NO,Field.Index.ANALYZED,Field.TermVector.WITH_POSITIONS_OFFSETS));
	  for(String name : metadata.names()) {
		  name.toLowerCase();
		  String value = metadata.get(name).toLowerCase();
		  if (textualMetadataFields.contains(name)) 
			  doc.add(new Field("contents", value,Field.Store.NO,Field.Index.ANALYZED,Field.TermVector.WITH_POSITIONS_OFFSETS));
		  doc.add(new Field(name, value,Field.Store.YES, Field.Index.NOT_ANALYZED));
	  }
	  if(f.getName().endsWith(".srt")) {
		  String dirTemp = f.getCanonicalFile().toString();
		  int position = dirTemp.lastIndexOf("/");
		  File fileTemp = new File(dirTemp.substring(0,position));
		  dirTemp = dirTemp.substring(position+1,dirTemp.lastIndexOf("."));
		  String[] fileList = fileTemp.list();
		  for(int i=0;i<fileList.length;i++) {
			  int pos = fileList[i].lastIndexOf(".");
			  String temp = fileList[i].substring(0,pos);
			  if(temp.compareTo(dirTemp) ==0 ) {
				  File fileTmp = new File(fileList[i]);
				  doc.add(new Field("path", fileTmp.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				  break;
			  }
		  }
	  }
	  else {
		  doc.add(new Field("path", f.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
	  }

	  doc.add(new Field("lastModified", Long.toString(f.lastModified()),Field.Store.YES, Field.Index.NOT_ANALYZED));
	return doc;
}
 
  public static void indexDocs(IndexWriter writer, File file)
    throws IOException {
	  int numTotalHits;
	  TopScoreDocCollector collector = TopScoreDocCollector.create(1, false);
    if (file.canRead()) {
    	if(!ReadCustomizationFile.criticalDirectory.contains(file.getCanonicalPath())) {
    		if (file.isDirectory()) {
    	    	  
    	    	  String[] files = file.list();
    	    	  if (files != null) {
    	    		  for (int i = 0; i < files.length; i++) {
    	    			  indexDocs(writer, new File(file, files[i]));
    	    		  }
    	    	  }
    	      	  } else {
    	      	  System.out.println("adding " + file);
    	          try {
    	        	  Document doc = new Document();
    	        	  IndexReader reader = IndexReader.open(InitializeWriter.fsdDirIndex, true); 
    	        	  IndexSearcher searcher = new IndexSearcher(reader);
    	        	  
    	        	  BooleanQuery query = new BooleanQuery();
    	        	  query.add(new TermQuery(new Term("path",file.getCanonicalPath())),BooleanClause.Occur.MUST); 
    	        	  searcher.search(query, collector);
    	        	  numTotalHits = collector.getTotalHits();
    	        	  synchronized(writer){
    	        		  if(numTotalHits== 0) {
    	        			  writer.addDocument(getDocument(file));
    	        		  }
    	        		  else {
    	        			  doc = searcher.doc(0);
    	        			  String lastModifiedOld = doc.get("lastModified");
    	        			  String lastModifiedNew = Long.toString(file.lastModified()); 
    	        			  if(lastModifiedOld.compareTo(lastModifiedNew)!=0) {
    	        				  writer.updateDocument(new Term("path",file.getCanonicalPath()), getDocument(file));
    	        			  }
    	        		  }
    	        	  }
    	          }
    	          catch (Exception fnfe) {
    	          System.out.println(fnfe.getMessage());
    	         } 
    	    	  percentageIndex+=file.length();
    	    	  if((percentageIndex/totalIndex) >= 0.01) {
    	    		  writer.optimize();
    	    		  writer.commit();
    	    		  percentageIndex=0;
    	    	  }
    	      }
    	    }
    	}
    }
}
