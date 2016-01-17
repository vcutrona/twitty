package twitter;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import util.GoogleMapsLocator;

import java.util.HashSet;

public class TweetExtractor {

	private final Object lock = new Object();
	private final GoogleMapsLocator gml = new GoogleMapsLocator();
	public HashSet<User> execute() throws TwitterException {

		final HashSet<User> users = new HashSet<User>();

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("CONSUMER_KEY")
				.setOAuthConsumerSecret("CONSUMER_KEY_SECRET")
				.setOAuthAccessToken("ACCESS_TOKEN")
				.setOAuthAccessTokenSecret("ACCESS_TOKEN_SECRET");

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

		StatusListener listener = new StatusListener() {

			public void onStatus(Status status) {
				User user = status.getUser();

				// Se abbiamo trovato un utente lo salviamo nell'hashset
				System.out.println("Questo utente: " + user.getLocation() + " " + user.getLang() + " " + user.getStatusesCount());
				String location = user.getLocation();
				//location non nulla ed esiste in google maps
				if (location != null && !location.isEmpty() && (gml.getLocationData(location) != null) && (user.getStatusesCount() > 200) && (user.getLang().equals("en"))) {
					users.add(user);
					System.out.println("Adesso abbiamo " + users.size() + " utenti");
					// System.out.println(user.getName());
				}
				if (users.size() >= 50) { // TODO rimettere a 1000!!!!
					synchronized (lock) {
						lock.notify();
					}
					System.out.println("unlocked");
				}
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			public void onStallWarning(StallWarning sw) {
				System.out.println(sw.getMessage());

			}
		};

		twitterStream.addListener(listener);
		twitterStream.sample();

		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("returning users");
		twitterStream.shutdown();
		return users;
	}
}
