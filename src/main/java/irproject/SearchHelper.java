package irproject;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.valuesource.LongFieldSource;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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
		TopDocs topDocs = se.performSearch("word", "", 100);

		// obtain the ScoreDoc (= documentID, relevanceScore) array from topDocs
		ScoreDoc[] hits = topDocs.scoreDocs;

		// retrieve each matching document from the ScoreDoc arry
		for (int i = 0; i < hits.length; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String name = doc.get("screenName");
			String follower = doc.get("follower");
			System.out.print(name + " ");
			System.out.println(follower);
		}
	}

	/** Creates a new instance of SearchEngine */
	public SearchHelper() throws IOException {
		Path path = FileSystems.getDefault().getPath("logs", "index");

		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(path)));
		// parser = new QueryParser("interest", new EnglishAnalyzer());
	}

	public TopDocs performSearch(String interest, String hashtag, int n)
			throws IOException, ParseException {
		//Query baseQuery = new TermQuery(new Term("interest", "heart"));
		Query boostQuery = new FunctionQuery(new LongFieldSource("follower"));
		//Query q = new CustomScoreQuery(baseQuery, (FunctionQuery) boostQuery);


		EnglishAnalyzer analyzer = new EnglishAnalyzer();
		// city query
		@SuppressWarnings("deprecation")
		BooleanQuery booleanQuery = new BooleanQuery();
		Query query1 = new TermQuery(new Term("interest", interest));
		Query query2 = new TermQuery(new Term("hashtag", hashtag));
		booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
		booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
		Query q = new CustomScoreQuery(booleanQuery, (FunctionQuery) boostQuery);

		return searcher.search(q, n);
	}

	public Document getDocument(int docId) throws IOException {
		return searcher.doc(docId);
	}
}
