package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.textrazor.AnalysisException;
import com.textrazor.NetworkException;
import com.textrazor.TextRazor;
import com.textrazor.annotations.AnalyzedText;
import com.textrazor.annotations.Entity;
import com.textrazor.annotations.Response;
import com.textrazor.annotations.Topic;

import entity.CustomEntity;
import entity.CustomTopic;

public class TextRazorUtil {
	
	TextRazor client = new TextRazor("YOUR_KEY");
	
	public TextRazorUtil(){
		client.addExtractor("topics");
		client.addExtractor("entities");
	}
	
	private Response analyze(String input) {
		AnalyzedText response = null;
		try {
			response = client.analyze(input);
			return response.getResponse();
		} catch (NetworkException | AnalysisException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	public List<CustomEntity> getEntities(String input) {
		Response response = this.analyze(input);
		if (response == null)
			return null;
		
		List<Entity> entities = response.getEntities();
		ArrayList<CustomEntity> returnEntities = new ArrayList<CustomEntity>();
		for (Entity e : entities) {
			CustomEntity me = new CustomEntity(e.getConfidenceScore(), e.getMatchedText());
			returnEntities.add(me);
		}
		Collections.sort(returnEntities);
		return returnEntities;
	}
	
	public List<CustomEntity> getEntities(String input, int n) {
		List<CustomEntity> returnEntities = this.getEntities(input);
		return returnEntities.subList(0, n);
	}
		
	public List<CustomTopic> getTopics(String input) {
		Response response = this.analyze(input);
		if (response == null)
			return null;
		
		List<Topic> topics = response.getTopics();
		ArrayList<CustomTopic> returnTopics = new ArrayList<CustomTopic>();
		for (Topic t : topics) {
			CustomTopic mt = new CustomTopic(t.getScore(), t.getLabel());
			returnTopics.add(mt);
		}
		Collections.sort(returnTopics);
		return returnTopics;
	}
	
	public List<CustomTopic> getTopics(String input, int n) {
		List<CustomTopic> returnTopics = this.getTopics(input);
		return returnTopics.subList(0, n);
	}
}
