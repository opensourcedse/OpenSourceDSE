package dse;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKind.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;

public class WatchDir {

    public static WatchService watcher;
    public static Map<WatchKey,Path> keys;
    private static boolean trace = true;

    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    public static void register(Path dir) throws IOException {
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

    public static void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) {
                try {
                    register(dir);
                } catch (IOException x) {
                    throw new IOError(x);
                }
                return FileVisitResult.CONTINUE;            }
        });
    }
    

    private void updateIndex(Path child,int check) {
        int numTotalHits=0;
        TopScoreDocCollector collector = TopScoreDocCollector.create(1, false);
    	try {
    	synchronized(InitializeWriter.writer) {
    		if(check == 1) {
    			try{
    				System.out.println(child);
    				final File file = new File(child.toString());
    				Document doc = new Document();
    	        	  IndexReader reader = IndexReader.open(InitializeWriter.fsdDirIndex, true); 
    	        	  IndexSearcher searcher = new IndexSearcher(reader);
    	        	  
    	        	  BooleanQuery query = new BooleanQuery();
    	        	  query.add(new TermQuery(new Term("path",file.getCanonicalPath())),BooleanClause.Occur.MUST); 
    	        	  searcher.search(query, collector);
    	        	  numTotalHits = collector.getTotalHits();
    				if(numTotalHits== 0) {
    					System.out.println(file.lastModified());
    					InitializeWriter.writer.addDocument(IndexFiles.getDocument(file));
    				}
    				else {
    					doc = searcher.doc(0);
    					String lastModifiedOld = doc.get("lastModified");
    					String lastModifiedNew = Long.toString(file.lastModified()); 
    					if(lastModifiedOld.compareTo(lastModifiedNew)!=0) {
    						InitializeWriter.writer.updateDocument(new Term("path",file.getCanonicalPath()), IndexFiles.getDocument(file));
    					}
    				}
    				InitializeWriter.writer.commit();
    				InitializeWriter.writer.optimize();
    			}catch(Exception e){}
    		}
    		else if(check == 2) {
    			try {
    				System.out.println(child);
    				InitializeWriter.writer.deleteDocuments(new Term("path",child.toString()));
    				InitializeWriter.writer.commit();
    				InitializeWriter.writer.optimize();
    			}catch(Exception e){System.out.println(e.getMessage());}
    		}
    	}
    	}catch(Exception e){}
    }
    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir() throws IOException {
    	watcher = FileSystems.getDefault().newWatchService();
        keys = new HashMap<WatchKey,Path>();
        
        Iterator<String> it = ReadCustomizationFile.criticalDirectory.iterator();
        while (it.hasNext()) {
        	Path dir =	Paths.get(it.next().toString());
        	if(Files.readAttributes(dir, BasicFileAttributes.class,NOFOLLOW_LINKS).isDirectory())
        		registerAll(dir);
        	else 
        		register(dir);
        }
        System.out.println("Done.");
        trace = true;
    }

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
                if ((kind == ENTRY_CREATE)) {
                    try {
                        if (Files.readAttributes(child, BasicFileAttributes.class,NOFOLLOW_LINKS).isDirectory()) {
                            registerAll(child);
                        }
                        else{
                        		updateIndex(child,1);
                        		register(child);
                        }
                    } catch (IOException x){System.out.println(x.getMessage());} {
                        // ignore to keep sample readbale
                    }
                }
                else if((kind==ENTRY_MODIFY)){
                	try {
                		if (!Files.readAttributes(child, BasicFileAttributes.class,NOFOLLOW_LINKS).isDirectory()) {
                				updateIndex(child,1);
                		}
                	}catch(Exception e){}
                }
                else if((kind==ENTRY_DELETE)) {
                	try {
                		updateIndex(child,2);
                	}catch(Exception e){System.out.println(e.getMessage());}
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
            }
        }
        
    }
}
