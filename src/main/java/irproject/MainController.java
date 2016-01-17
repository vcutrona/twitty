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
import org.springframework.web.servlet.ModelAndView;

import entity.Greeting;
import entity.UserFields;
import extractor.UserClassification;
import twitter.TweetExtractor;
import twitter4j.TwitterException;
import twitter4j.User;

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
    @RequestMapping(value="/greeting", method=RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Greeting());
        return "old";
    }

    @RequestMapping(value="/greeting", method=RequestMethod.POST)
    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
        model.addAttribute("greeting", greeting);
        return "result";
    }
    
    @RequestMapping(value="/")
    public String index(Model model) {
        return "index";
    }
    
    @RequestMapping(value = "/demo")
    public String getdata(Model model) {

	    ArrayList<UserFields> list = new ArrayList<UserFields>();
	    UserFields a = new UserFields();
	    UserFields b = new UserFields();
	    a.screenName = "io";
	    b.screenName = "tu";
	    list.add(a);
	    list.add(b);
	    model.addAttribute("u", list);
	
		return "endindex";
    }
}
