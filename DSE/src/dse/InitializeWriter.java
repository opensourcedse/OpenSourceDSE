
package dse;

import java.io.File;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSLockFactory;
import org.apache.lucene.util.Version;


public class InitializeWriter {

	public static IndexWriter writer;
	public static FSDirectory fsdDirIndex;
	public static Analyzer analyzer;
	public static final File INDEX_DIR = new File("index");
	
	public static void main(String[] args) {
		try {
		analyzer=new StandardAnalyzer(Version.LUCENE_30);
    	fsdDirIndex=FSDirectory.open(INDEX_DIR,new SimpleFSLockFactory());
    	if(INDEX_DIR.exists())
    		writer = new IndexWriter(fsdDirIndex, analyzer, false, new KeepOnlyLastCommitDeletionPolicy(),
    				IndexWriter.MaxFieldLength.LIMITED);
    	else
    		writer = new IndexWriter(fsdDirIndex, analyzer, true, new KeepOnlyLastCommitDeletionPolicy(),
    				IndexWriter.MaxFieldLength.LIMITED);
		}catch(Exception e){}
		System.out.println("Writer Initialized!!!!");
		
		
	}

}
