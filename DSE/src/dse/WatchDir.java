package dse;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKind.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSLockFactory;
import org.apache.lucene.util.Version;

public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    static int numTotalHits;
    static ScoreDoc[] hits;
    static TopScoreDocCollector collector = TopScoreDocCollector.create(5 * 10, false);
    static final File INDEX_DIR = new File("index");
    IndexWriter writer;
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) {
                try {
                    register(dir);
                } catch (IOException x) {
                    throw new IOError(x);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
    

    private void updateIndex(Path child,int check) {
    	try {
    		if (INDEX_DIR.exists()) { 
    			writer = new IndexWriter(FSDirectory.open(INDEX_DIR,new SimpleFSLockFactory()), new StandardAnalyzer(Version.LUCENE_CURRENT),false,IndexWriter.MaxFieldLength.LIMITED);
    		}
    		else if(!INDEX_DIR.exists()) {  	
    			writer = new IndexWriter(FSDirectory.open(INDEX_DIR,new SimpleFSLockFactory()), new StandardAnalyzer(Version.LUCENE_CURRENT),true,IndexWriter.MaxFieldLength.LIMITED);
    		}
    	}catch(IOException e){System.out.println(e.getMessage());}
    	if(check == 1) {
    	try{
    		System.out.println(child);
    		final File file = new File(child.toString());
    		Document doc = new Document();
      	    BooleanQuery query = new BooleanQuery();
      	    query.add(new TermQuery(new Term("path",file.getCanonicalPath())),BooleanClause.Occur.MUST); 
      	    IndexReader reader = IndexReader.open(FSDirectory.open(new File("index")), true); 
      	    Searcher searcher = new IndexSearcher(reader);
      	    searcher.search(query, collector);
      	    numTotalHits = collector.getTotalHits();
      	    if(numTotalHits== 0) {
      		    System.out.println(file.lastModified());
      		    writer.addDocument(IndexFiles.getDocument(file));
      	    }
      	  else {
      		  hits = collector.topDocs().scoreDocs;
      		  doc = searcher.doc(hits[0].doc);
      		  String lastModifiedOld = doc.get("lastModified");
      		  String lastModifiedNew = Long.toString(file.lastModified()); 
      		  if(lastModifiedOld.compareTo(lastModifiedNew)!=0) {
      			  writer.updateDocument(new Term("path",file.getCanonicalPath()), IndexFiles.getDocument(file));
      		  }
      	  }
    		writer.commit();
    	}catch(Exception e){}
    	}
    	else if(check == 2) {
    		try {
        		System.out.println(child);
        		//BooleanQuery query = new BooleanQuery();
        		//query.add(new TermQuery(new Term("path",child.toString())),BooleanClause.Occur.MUST);
        		writer.deleteDocuments(new Term("path",child.toString()));
        		writer.commit();
        	}catch(Exception e){System.out.println(e.getMessage());}
        }
    	try {
            writer.close();
        }catch(Exception e){System.out.println(e.getMessage());}
    }
    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir(Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.recursive = recursive;

        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }
        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void processEvents() {
    	
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
            	System.out.println(x.getMessage());
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.readAttributes(child, BasicFileAttributes.class,NOFOLLOW_LINKS).isDirectory()) {
                            registerAll(child);
                        }
                        else{
                        	if(!child.toString().endsWith("#"))
                        		register(child);
                        }
                    } catch (IOException x){System.out.println(x.getMessage());} {
                        // ignore to keep sample readbale
                    }
                }
                else if(recursive && (kind==ENTRY_MODIFY)){                	
                		updateIndex(child,1);
                }
                else if(recursive && (kind==ENTRY_DELETE)) {
                	    updateIndex(child,2);
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
        
    }
}
