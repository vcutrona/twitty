package main.java.irproject;

import java.util.HashSet;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import main.java.extractor.UserClassification;
import main.java.twitter.TweetExtractor;
import twitter4j.User;
import main.java.util.UserFields;

@RestController
public class MainController {

    @RequestMapping("/greeting")
    public UserFields greeting(@RequestParam(value="name", defaultValue="World") String name) {
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
		        u.setAge(age);
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
