
package dse;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import java.io.*;
import java.util.Date;
import org.apache.lucene.document.Document;
import org.xml.sax.ContentHandler;
import org.apache.tika.sax.*;
import java.util.*;
import org.apache.tika.parser.*;
import org.apache.tika.parser.txt.*;
import org.apache.tika.metadata.*;
import org.apache.*;
import org.apache.lucene.document.*;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

public class IndexFiles extends SearchGui {
  
  IndexFiles() {}
  //final static Logger log4j = Logger.getLogger(IndexFiles.class);
  static IndexWriter writer;
  static final File INDEX_DIR = new File("index");
  static long percentageIndex;
  static long totalIndex;
  static int numTotalHits;
  static ScoreDoc[] hits;
  static TopScoreDocCollector collector = TopScoreDocCollector.create(5 * 10, false);
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
    totalIndex = docDir.getTotalSpace();
    if (!docDir.exists() || !docDir.canRead()) {
      System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
   
    Date start = new Date();
    try {
    	if (INDEX_DIR.exists()) { 
    		writer = new IndexWriter(FSDirectory.open(INDEX_DIR,new SimpleFSLockFactory()), new StandardAnalyzer(Version.LUCENE_CURRENT),false,IndexWriter.MaxFieldLength.LIMITED);
		}
		else if(!INDEX_DIR.exists()) {  	
			writer = new IndexWriter(FSDirectory.open(INDEX_DIR,new SimpleFSLockFactory()), new StandardAnalyzer(Version.LUCENE_CURRENT),true,IndexWriter.MaxFieldLength.LIMITED);
		}
        System.out.println("Indexing to directory '" +INDEX_DIR+ "'...");
        indexDocs(writer, docDir);
        //log4j.debug("WTF?");
        System.out.println("Optimizing...");
        writer.optimize();
        writer.close();
        Date end = new Date();
        System.out.println(end.getTime() - start.getTime() + " total milliseconds");
        resultArea.append("\n" +(end.getTime() - start.getTime()) + " total milliseconds");
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
		  System.out.println(metadata);
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
    // do not try to index files that cannot be rea
    if (file.canRead()) {
      if (file.isDirectory()) {
    	  String[] files = file.list();
    	  // an IO error could occur
    	  if (files != null) {
    		  for (int i = 0; i < files.length; i++) {
    			  indexDocs(writer, new File(files[i]));
    		  }
    	  }
      	  } else {
      	  System.out.println("adding " + file);
          try {
        	  Document doc = new Document();
        	  BooleanQuery query = new BooleanQuery();
        	  query.add(new TermQuery(new Term("path",file.getCanonicalPath())),BooleanClause.Occur.MUST); 
        	  IndexReader reader = IndexReader.open(FSDirectory.open(new File("index")), true); 
        	  Searcher searcher = new IndexSearcher(reader);
        	  searcher.search(query, collector);
        	  numTotalHits = collector.getTotalHits();
        	  if(numTotalHits== 0) {
        		  System.out.println(file.lastModified());
        		  writer.addDocument(getDocument(file));
        	  }
        	  else {
        		  hits = collector.topDocs().scoreDocs;
        		  doc = searcher.doc(hits[0].doc);
        		  String lastModifiedOld = doc.get("lastModified");
        		  String lastModifiedNew = Long.toString(file.lastModified()); 
        		  if(lastModifiedOld.compareTo(lastModifiedNew)!=0) {
        			  writer.updateDocument(new Term("path",file.getCanonicalPath()), getDocument(file));
        		  }
        	  }
        	  percentageIndex += file.getTotalSpace();
        	  if(percentageIndex/totalIndex >= 0.5) {
        		  writer.commit();
        		  percentageIndex=0;
        	  }
          }
          catch (Exception fnfe) {
          System.out.println(fnfe.getMessage());
        }
      }
    }
  }
  
}