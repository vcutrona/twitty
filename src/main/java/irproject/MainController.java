package irproject;

import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import entity.UserFields;
import extractor.UserClassification;
import twitter.TweetExtractor;
import twitter4j.TwitterException;
import twitter4j.User;

@RestController
public class MainController {

    @RequestMapping("/build")
    public ArrayList <UserFields> build() throws Exception {
    	try {
	    	TweetExtractor stream = new TweetExtractor();
			HashSet<User> users = stream.execute();
			UserClassification cinni = new UserClassification();
			ArrayList<UserFields> list = cinni.buildUserFieldList(users);
			IndexCreator.create(list);
			return list;
    	} catch(TwitterException e) {
    		System.out.println(e.getMessage());
    		System.out.println(e.getStackTrace());
	    	return null;
	    }
    }
}
