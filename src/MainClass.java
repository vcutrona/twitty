import java.util.HashSet;

import extractor.UserClassification;
import twitter.TweetExtractor;
import twitter4j.TwitterException;
import twitter4j.User;

public class MainClass {
	public static void main(String args[]) throws TwitterException {

		TweetExtractor stream = new TweetExtractor();
		HashSet<User> users = stream.execute();
		UserClassification classy = new UserClassification();
		for (User user : users) {
			String screenName = user.getScreenName();
			String age = classy.getAge(screenName);
			String gender = classy.getGender(screenName);
			System.out.println("User " + user.getScreenName() + "is a " + gender + " of " + age + " years");
			break;
		}

	}
}
