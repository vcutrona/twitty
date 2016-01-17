package irproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import entity.Greeting;
import entity.Search;
import entity.UserFields;
import entity.UserModel;
import extractor.UserClassification;
import twitter.TweetExtractor;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

@Controller//@RestController
public class MainController {
	

    @RequestMapping("/build")
    public String build(Model model) {
    	try {
	    	TweetExtractor stream = new TweetExtractor();
			HashSet<User> users = stream.execute();
			UserClassification cinni = new UserClassification();
			ArrayList<UserFields> uf = cinni.buildUserFieldList(users);
			//Users u = new Users();
			IndexCreator.create(uf);
			//u.users = list;
			if (uf.size() > 5)
				model.addAttribute("u", uf.subList(0, 5));
			else
				model.addAttribute("u", uf);
			return "endindex";
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    		System.out.println(e.getStackTrace());
	    	return "error";
	    }
    }
    
    /*
    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
    */

    /*
    @RequestMapping(value="/greeting", method=RequestMethod.POST)
    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
        model.addAttribute("greeting", greeting);
        return "result";
    }
    */
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("search", new Search());
        return "greeting";
    }
    @RequestMapping(value="/search", method=RequestMethod.POST)
    public String search(@ModelAttribute Search search,
    		Model model) throws IOException, ParseException, TwitterException {
    	
		HashMap<String, String> ht = new HashMap<String, String>();
		ht.put("tweet", "OR");
		ht.put("gender", "OR");
		ht.put("age", "OR");
		ht.put("geolocation", "OR");

		SearchHelper se = new SearchHelper(ht); 
    	ArrayList<UserModel> list = se.search(
    			search.getInterest(), 
    			search.getGender(), 
    			search.getAge(),
    			search.getLongitude(), 
    			search.getLatitude(), 
    			search.getRadius(), 
    			search.getNumber());
		
		double maxScore = list.get(0).getScore();
	    model.addAttribute("u", list);
	    model.addAttribute("max_score", maxScore);
		
		return "endindex";
    }
    
    
    @RequestMapping(value="/")
    public String index(Model model) {
        return "index";
    }
    
    @RequestMapping(value="/user")
    public String showUser(@RequestParam(value="screen_name") String screenName, Model model) {
    	ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("2PexuEeruTahis27ZG3QxrEYh")
				.setOAuthConsumerSecret("dfxizJjS5w9FqYDGQYgKI42DKXsw1wAcnIQTJU616pBIxrXKJh")
				.setOAuthAccessToken("2332157006-BXKX6flDvtsaGCD4NBj8IzL5xl8DUyaL952aow2")
				.setOAuthAccessTokenSecret("6ErzotI2jxbVYeeIbstUuQBp3FRvXwy25yKwbZTM9XQhy");
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
		User user = null;
		try {
			user = twitter.showUser(screenName);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		if(user==null) {
			return "error";
		}
		
		int friends = user.getFriendsCount();
		
		
        return "show_user";
    }
    
    @RequestMapping(value = "/demo")
    public String getdata(Model model) throws TwitterException {

    	ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("2PexuEeruTahis27ZG3QxrEYh")
				.setOAuthConsumerSecret("dfxizJjS5w9FqYDGQYgKI42DKXsw1wAcnIQTJU616pBIxrXKJh")
				.setOAuthAccessToken("2332157006-BXKX6flDvtsaGCD4NBj8IzL5xl8DUyaL952aow2")
				.setOAuthAccessTokenSecret("6ErzotI2jxbVYeeIbstUuQBp3FRvXwy25yKwbZTM9XQhy");
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
    	
				
	    ArrayList<UserFields> list = new ArrayList<UserFields>();
	    UserFields a = new UserFields();
	    UserModel b = new UserModel();

	    a.screenName = "fb_vinid";
		User user = twitter.showUser(a.screenName);
		a.profileImageURL = user.getOriginalProfileImageURL();
		a.coverImageURL = (user.getProfileBannerURL() != null ? user.getProfileBannerURL() : user.getProfileBackgroundImageURL());
		a.follower = user.getFollowersCount();
		a.description = user.getDescription();
	    a.numberOfTweets = user.getStatusesCount();
	    a.name = user.getName();
		list.add(a);
		list.add(a);
		list.add(a);
		
	    model.addAttribute("u", list);
	
		return "endindex";
    }
}
