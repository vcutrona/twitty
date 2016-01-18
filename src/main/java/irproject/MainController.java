package irproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import entity.Search;
import entity.UserFields;
import entity.UserModel;
import extractor.UserClassification;
import twitter.TweetExtractor;
import twitter4j.TwitterException;

@Controller//@RestController
public class MainController {
	

    @RequestMapping("/build")
    public String build(Model model) {
    	try {
	    	TweetExtractor stream = new TweetExtractor();
	    	System.out.println("stream");
			ArrayList<UserFields> users = stream.execute();
			System.out.println("stream execute");
			UserClassification cinni = new UserClassification();
			ArrayList<UserFields> uf = cinni.buildUserFieldList(users);
			System.out.println("class");
			IndexCreator.create(uf);
			System.out.println("index created");

			ArrayList<UserFields> u = new ArrayList<UserFields>();
			
			u.addAll(uf);
			
			if (u.size() > 5)
				model.addAttribute("u", u.subList(0, 5));
			else
				model.addAttribute("u", u);
			return "index"; //TODO
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    		System.out.println(e.getStackTrace());
	    	return "error";
	    }
    }
        
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String searchForm(Model model) {
        model.addAttribute("search", new Search());
        return "search";
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
		
		if (list.size() > 0) {
			double maxScore = list.get(0).getScore();

		    model.addAttribute("u", list);
		    model.addAttribute("max_score", maxScore);
			
			return "user";
		} else {
			return "noresults";
		}
    }
    
    
    @RequestMapping(value="/")
    public String index(Model model) {
        return "index";
    }
        
    @RequestMapping(value = "/user", method=RequestMethod.POST)
    public String getdata(@ModelAttribute UserModel user, Model model) {
    	System.out.println(user.screenName);
    	
		return "user";
    }
}
