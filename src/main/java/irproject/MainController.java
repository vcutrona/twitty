package irproject;

import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import extractor.UserClassification;
import twitter.TweetExtractor;
import twitter4j.TwitterException;
import twitter4j.User;
import util.UserFields;

@RestController
public class MainController {

    @RequestMapping("/build")
    public ArrayList <UserFields> build() {
    	try {
	    	TweetExtractor stream = new TweetExtractor();
			HashSet<User> users = stream.execute();
			UserClassification cinni = new UserClassification();
			IndexCreator ic = new IndexCreator();
			ArrayList<UserFields> list = cinni.buildUserFieldList(users);
			ic.create(list);
			return list;
    	} catch(TwitterException e) {
    		System.out.println(e.getMessage());
    		System.out.println(e.getStackTrace());
	    	return null;
	    }
    }
}
