package irproject;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.valuesource.LongFieldSource;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import analyzer.TweetAnalyzer;
import entity.UserModel;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class SearchHelper {

	private static IndexSearcher searcher = null;
	Map<String, String> dictionary;
	private Highlighter tweetHighlighter;
	private String[] uClassifyRanges = {"13-17", "18-25", "26-35", "36-50", "51-65", "65-100"};

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
	}

	public ArrayList<UserModel> search(String tweet,  String gender, int age, double longitude, double latitude, int d, int n) throws IOException, ParseException, TwitterException {
		
		TopDocs topDocs = this.performSearch(tweet, gender, age, longitude, latitude, d, 100);
		System.out.println("sto cercando");
		ScoreDoc[] hits = topDocs.scoreDocs;
		
		// retrieve each matching document from the ScoreDoc array
		ArrayList<UserModel> uml = new ArrayList<UserModel>();
		for (int i = 0; i < hits.length; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String name = doc.get("screenName");
			System.out.println(name + " punteggio:  " + hits[i].score + " ");
		    
			UserModel um = new UserModel();
			um.screenName = name;
			um.profileImageURL = doc.get("profileImageURL");
			um.coverImageURL = doc.get("coverImageURL");
			um.follower = Integer.valueOf(doc.get("follower"));
			um.description = doc.get("description");
			um.numberOfTweets = Integer.valueOf(doc.get("numberOfTweets"));
			um.name = doc.get("name");
			um.setScore(hits[i].score);
			uml.add(um);
		}
		return uml;
	}

	public TopDocs performSearch(String tweet,  String gender, int age, double longitude, double latitude, double radius, int n)
			throws IOException, ParseException {
		
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		
		//Query sui tweet
		if (!tweet.isEmpty()){
			QueryParser parser = new QueryParser("tweet", new TweetAnalyzer());
			if (tweet.indexOf("\"") == 0 && tweet.lastIndexOf("\"") == tweet.length()-1){
				parser.setDefaultOperator(Operator.AND);
			}
			Query queryTweet = parser.parse(tweet);
			
			QueryScorer queryScorerTweet = new QueryScorer(queryTweet, "tweet");
	        Fragmenter fragment = new SimpleSpanFragmenter(queryScorerTweet, 200);

	        tweetHighlighter = new Highlighter(queryScorerTweet);
	        tweetHighlighter.setTextFragmenter(fragment);

	        booleanQuery.add(queryTweet, this.getBoolClause("tweet"));
		}
		
		//Query sul genere
		if (!gender.isEmpty()){
			Query queryGender = new TermQuery(new Term("gender", gender));
			booleanQuery.add(queryGender, this.getBoolClause("gender"));
		}
		
		//Query sull'età
		String ageToSearch = "";
		for (int i = 0; i < uClassifyRanges.length; ++i) {
			String ageRange = uClassifyRanges[i];
			String[] part = ageRange.split("-");
			if (age >= Integer.valueOf(part[0]) && (age <= Integer.valueOf(part[1]))){ //TODO controlla condizioni
				ageToSearch = uClassifyRanges[i];
				break;
			}
		}
		Query queryAge = new TermQuery(new Term("age", ageToSearch));
		
		booleanQuery.add(queryAge, this.getBoolClause("age"));
		
		//query sulla geolocalizzazione
		if (radius == 0)
			radius = 1;
		GeoPointDistanceQuery queryGeolocation = new GeoPointDistanceQuery("geolocation", longitude, latitude, radius);
		booleanQuery.add(queryGeolocation, this.getBoolClause("geolocation"));

		
		//Query sui follower (boost)
		Query q = new CustomScoreQuery(booleanQuery.build(), new FunctionQuery(new LongFieldSource("follower")));
		//searcher.setSimilarity(new LMDirichletSimilarity()); 
		return searcher.search(q, n);
	}

	public Document getDocument(int docId) throws IOException {
		return searcher.doc(docId);
	}
	
	private Occur getBoolClause(String field) {
		return (this.dictionary.get(field).equals("AND") ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD);
	}
	
	public static void xmain(String[] args) throws IOException, ParseException, TwitterException {
		HashMap<String, String> ht = new HashMap<String, String>();
		ht.put("tweet", "OR");
		ht.put("gender", "OR");
		ht.put("age", "OR");
		ht.put("geolocation", "OR");

		SearchHelper se = new SearchHelper(ht); 
    	ArrayList<UserModel> list = se.search("", "male", 0, 0, 0, 1, 0);
		
    	for (UserModel u : list) {
    		System.out.println(u.screenName);
    	}		
	}
}
