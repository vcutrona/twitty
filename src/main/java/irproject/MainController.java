package irproject;

import java.util.HashSet;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import extractor.UserClassification;
import twitter.TweetExtractor;
import twitter4j.User;
import util.UserFields;

@RestController
public class MainController {

    @RequestMapping("/search")
    public UserFields search(
    		@RequestParam(value="location") String loc) {
    	try {
	    	TweetExtractor stream = new TweetExtractor();
			HashSet<User> users = stream.execute();
			UserClassification classy = new UserClassification();
			for (User user : users) {
				String screenName = user.getScreenName();
				String age = classy.getAge(screenName);
				String gender = classy.getGender(screenName);
				String location = user.getLocation();
				System.out.println("User " + user.getScreenName() + "is a " + gender + " of " + age + " years");
				UserFields u = new UserFields();
		        u.setAgeMin(Integer.parseInt(age)); //TODO split
		        u.setGender(gender);
		        u.setLocation(location);
		    	return u;
			}
    	} catch(Exception e) {
    		//TODO
	    	return new UserFields();
	    }
    	return null;
    }
}
