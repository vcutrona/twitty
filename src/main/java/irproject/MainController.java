package irproject;

import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import entity.Greeting;
import entity.UserFields;
import extractor.UserClassification;
import twitter.TweetExtractor;
import twitter4j.TwitterException;
import twitter4j.User;

@Controller//@RestController
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
    
    /*
    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }
    */
    @RequestMapping(value="/greeting", method=RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Greeting());
        return "greeting";
    }

    @RequestMapping(value="/greeting", method=RequestMethod.POST)
    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
        model.addAttribute("greeting", greeting);
        return "result";
    }
}
