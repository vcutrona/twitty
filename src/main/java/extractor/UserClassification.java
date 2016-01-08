package extractor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.code.uclassify.client.UClassifyClient;
import com.google.code.uclassify.client.UClassifyClientFactory;
import com.uclassify.api._1.responseschema.Classification;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.api.TimelinesResources;
import twitter4j.conf.ConfigurationBuilder;

import com.uclassify.api._1.responseschema.Class;

public class UserClassification {

	private Twitter twitter;
	private ConfigurationBuilder cb;
	private String textConcatenation;
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

	private String getUserData(String screenName) throws TwitterException {
		Paging paging = new Paging(1, 200);

		List<Status> statuses = this.twitter.getUserTimeline(screenName, paging);

		String string = "";
		for (Status status : statuses) {
			string += status.getText();
		}
		return string;
	}

	public String getGender(String screenName) throws TwitterException {

		Map<String, Classification> classifications = this.client.classify("uClassify", "GenderAnalyzer_v5",
				Arrays.asList(this.getUserData(screenName)));
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

	public String getAge(String screenName) throws TwitterException {

		Map<String, Classification> classifications = this.client.classify("uClassify", "Ageanalyzer",
				Arrays.asList(this.getUserData(screenName)));
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
}
