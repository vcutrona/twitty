package irproject;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.*;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Document;

public class SearchHelper {
	
    private static IndexSearcher searcher = null;
    private QueryParser parser = null;
    
	public static void main(String args[]) throws IOException, ParseException {
		SearchHelper se = new SearchHelper();
		TopDocs topDocs = se.performSearch("Bikram Lamba", 100); 

		// obtain the ScoreDoc (= documentID, relevanceScore) array from topDocs
		ScoreDoc[] hits = topDocs.scoreDocs;

		// retrieve each matching document from the ScoreDoc arry
		for (int i = 0; i < hits.length; i++) {
		    Document doc = searcher.doc(hits[i].doc);
		    String name = doc.get("screenName");
		    System.out.println(name);
		}
	}
	
    /** Creates a new instance of SearchEngine */
    public SearchHelper() throws IOException {
		Path path = FileSystems.getDefault().getPath("logs", "EvqNjO5OaY1F");

        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(path)));
        parser = new QueryParser("interest", new StandardAnalyzer());
    }

    public TopDocs performSearch(String queryString, int n)
	    throws IOException, ParseException {
	        Query query = parser.parse(queryString);
	        return searcher.search(query, n);
    }

    public Document getDocument(int docId)
    throws IOException {
        return searcher.doc(docId);
    }
}
