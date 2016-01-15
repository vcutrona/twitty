package extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.code.uclassify.client.UClassifyClient;
import com.google.code.uclassify.client.UClassifyClientFactory;
import com.uclassify.api._1.responseschema.Classification;

import entity.Locator;
import entity.UserFields;
import twitter4j.EntitySupport;
import twitter4j.HashtagEntity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.api.TimelinesResources;
import twitter4j.conf.ConfigurationBuilder;
import util.GoogleMapsLocator;

import com.uclassify.api._1.responseschema.Class;

public class UserClassification {

	private Twitter twitter;
	private ConfigurationBuilder cb;
	final UClassifyClientFactory factory;
	final UClassifyClient client;
	
	public UserClassification() {
		this.cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("CONSUMER_KEY")
				.setOAuthConsumerSecret("CONSUMER_KEY_SECRET")
				.setOAuthAccessToken("ACCESS_TOKEN")
				.setOAuthAccessTokenSecret("ACCESS_TOKEN_SECRET");
		this.twitter = new TwitterFactory(cb.build()).getInstance();

		this.factory = UClassifyClientFactory.newInstance("YOUR_KEY", null);
		this.client = factory.createUClassifyClient();
	}
	
	private List<Status>  getStatuses(String screenName) throws TwitterException {
		Paging paging = new Paging(1, 200);

		List<Status> statuses = this.twitter.getUserTimeline(screenName, paging);

		return statuses;
	}

	private String getUserHashtag(List<Status> statuses) throws TwitterException {

		HashSet <HashtagEntity> hashtags = new HashSet<HashtagEntity>();
		for (Status status : statuses) {
			
			hashtags.addAll(Arrays.asList(status.getHashtagEntities()));
		}
		String hashy = "";
        for (HashtagEntity hashtagEntity : hashtags) {
        	hashy += hashtagEntity.getText() + " ";
		}
		return hashy;
	}
	
	public String getGender(String screenName, String tweetData) throws TwitterException {

		Map<String, Classification> classifications = this.client.classify("uClassify", "GenderAnalyzer_v5",
				Arrays.asList(tweetData));
		System.out.println("================ Classifications ==================");
		String returnClass = "None";
		//un for, ma con una sola classificazione? non so come toglierlo....
		for (String text : classifications.keySet()) {

			Classification classification = classifications.get(text);
			System.out.println("====================");
			double pointClass = -1;
			for (Class clazz : classification.getClazz()) {
				String currentClass = clazz.getClassName(); // prendo il
															// risultato
															// della
															// classificazione
				double currentPoint = clazz.getP(); // prendo lo score di questa
													// classe e vedo se è più
													// grande
													// id un possibile
													// precedente
				System.out.println(currentClass + ":" + currentPoint);
				if (clazz.getP() > pointClass) {
					returnClass = currentClass;
					pointClass = currentPoint;
				}
			}
		}
		return returnClass;
	}

	public String getAge(String screenName, String tweetData) throws TwitterException {

		Map<String, Classification> classifications = this.client.classify("uClassify", "Ageanalyzer",
				Arrays.asList(tweetData));
		System.out.println("================ Classifications ==================");
		String returnClass = "None";

		for (String text : classifications.keySet()) {

			Classification classification = classifications.get(text);
			System.out.println("====================");
			double pointClass = -1;
			for (Class clazz : classification.getClazz()) {
				String currentClass = clazz.getClassName(); // prendo il
															// risultato
															// della
															// classificazione
				double currentPoint = clazz.getP(); // prendo lo score di questa
													// classe e vedo se è più
													// grande
													// di un possibile
													// precedente
				System.out.println(currentClass + ":" + currentPoint);
				if (clazz.getP() > pointClass) {
					returnClass = currentClass;
					pointClass = currentPoint;
				}
			}
		}
		return returnClass;
	}
	public ArrayList <UserFields> buildUserFieldList (HashSet<User> users) throws Exception {
		ArrayList<UserFields> uf = new ArrayList<UserFields>();
		for (User user : users) {
			String screenName = user.getScreenName();
			List <Status> statuses = this.getStatuses(screenName);
			ArrayList <String> tweets = new ArrayList<String>();
			String tweetData = "";

			for (Status status : statuses) {
				String temp = status.getText();
				tweets.add(temp);
				tweetData +=  " " + temp;
			}
			
			String age = this.getAge(screenName, tweetData);
			String gender = this.getGender(screenName, tweetData);
			String location = user.getLocation();
			System.out.println("User " + user.getScreenName() + "is a " + gender + " of " + age + " years");
			UserFields u = new UserFields();
			//Compute complete location
			GoogleMapsLocator gp = new GoogleMapsLocator();
			
			Locator loc = gp.getLocationData(location);
			
			u.city = loc.getLocality();
			u.address = loc.getAddress();
			u.latitude = loc.getLatitude();
			u.longitude = loc.getLongitude();
			u.country = loc.getCountry();
			
	        u.gender = gender;
	        u.screenName = screenName;
	        u.profileBigImage = user.getOriginalProfileImageURL();
	        u.follower = user.getFollowersCount();
	        u.age = age;
	        u.tweet = tweets;
	        uf.add(u);
		}
		return uf;
		
	}
	
}
