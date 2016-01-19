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
	
	@RequestMapping(value="/")
    public String index(Model model) {
        return "index";
    }

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
			return "index";
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
    		Model model) {
    	
		HashMap<String, String> ht = new HashMap<String, String>();
		ht.put("tweet", (search.isInterestDic() ? "AND" : "OR"));
		ht.put("gender", (search.isGenderDic() ? "AND" : "OR"));
		ht.put("age", (search.isAgeDic() ? "AND" : "OR"));
		ht.put("geolocation", (search.isGeoDic() ? "AND" : "OR"));
				
		SearchHelper se;
		try {
			se = new SearchHelper(ht);
			ArrayList<UserModel> list = se.search(
	    			search.getInterest().toLowerCase().trim(), 
	    			search.getGender().toLowerCase().trim(), 
	    			(search.getAge() != null && !search.getAge().isEmpty() ? Integer.parseInt(search.getAge()) : 0),
	    			(search.getLongitude() != null && !search.getLongitude().isEmpty() ? Double.parseDouble(search.getLongitude()) : 0),
	    			(search.getLatitude() != null && !search.getLatitude().isEmpty() ? Double.parseDouble(search.getLatitude()) : 0),
	    			(search.getRadius() != null && !search.getRadius().isEmpty() ? Integer.parseInt(search.getRadius()) : 0),
	    			(search.getNumber() != null && !search.getNumber().isEmpty() ? Integer.parseInt(search.getNumber()) : 0),
	    			(search.isBoost()));
	    	
			if (list.size() > 0) {
				double maxScore = list.get(0).getScore();

			    model.addAttribute("u", list);
			    model.addAttribute("max_score", maxScore);
				return "showresults";
			} else {
				return "noresults";
			}
		} catch (IOException | NumberFormatException | ParseException | TwitterException e) {
			e.printStackTrace();
			return "error";
		}
    }
}
