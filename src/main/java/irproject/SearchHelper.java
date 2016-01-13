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
import java.util.HashMap;
import java.util.Map;

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
	Map<String, String> dictionary;

	/**
	 * Crea un search helper per poter effetture le ricerce
	 * @param ht HashMap con all'interno i campi 
	 * con le rispettive direttive (es: devono essere in and o or i field?)
	 * @throws IOException
	 */
	public SearchHelper(HashMap<String, String> ht) throws IOException {
		Path path = FileSystems.getDefault().getPath("logs", "index");
		dictionary  = ht;
		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(path)));
		// parser = new QueryParser("interest", new EnglishAnalyzer());
	}

	public static void main(String args[]) throws IOException, ParseException {

		HashMap<String, String> ht = new HashMap<String, String>();
		ht.put("tweet", "AND");
		ht.put("gender", "OR");

		SearchHelper se = new SearchHelper(ht);
		TopDocs topDocs = se.performSearch("lucky", "female", 100);
		System.out.println("sto cercando");
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



	public TopDocs performSearch(String interest,  String gender, int n)
			throws IOException, ParseException {

		
		Query boostQuery = new FunctionQuery(new LongFieldSource("follower"));
		// city query
		@SuppressWarnings("deprecation")
		//Query booleana
		BooleanQuery booleanQuery = new BooleanQuery();
		Query query1 = new TermQuery(new Term("tweet", interest));
		Query query2 = new TermQuery(new Term("gender", gender));

		//Le query sono in and o in or?
		BooleanClause.Occur shouldTweet;
		BooleanClause.Occur shouldGender;

		if(this.dictionary.get("tweet") == "AND") {
			shouldTweet = BooleanClause.Occur.MUST;
		}
		else {
			shouldTweet = BooleanClause.Occur.SHOULD;
		}
		if(this.dictionary.get("gender") == "AND") {
			shouldGender = BooleanClause.Occur.MUST;
		}
		else {
			shouldGender = BooleanClause.Occur.SHOULD;
		}		
		
		booleanQuery.add(query1, shouldTweet);
		booleanQuery.add(query2, shouldGender);
		Query q = new CustomScoreQuery(booleanQuery, (FunctionQuery) boostQuery);

		return searcher.search(q, n);
	}

	public Document getDocument(int docId) throws IOException {
		return searcher.doc(docId);
	}
}
