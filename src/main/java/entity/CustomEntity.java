package entity;

public class CustomEntity implements Comparable<CustomEntity>{
	double confidenceScore;
	String matchedText;
	
	public CustomEntity(double confidenceScore, String matchedText) {
		super();
		this.confidenceScore = confidenceScore;
		this.matchedText = matchedText;
	}
	public double getConfidenceScore() {
		return confidenceScore;
	}
	public void setConfidenceScore(double confidenceScore) {
		this.confidenceScore = confidenceScore;
	}
	public String getMatchedText() {
		return matchedText;
	}
	public void setMatchedText(String matchedText) {
		this.matchedText = matchedText;
	}
	public int compareTo(CustomEntity compareEntity) {
		return (int)(compareEntity.getConfidenceScore() - this.getConfidenceScore()); //decrescent
	}
	public String toString(){
		return "Text: " + this.matchedText + " Confidence: " + this.confidenceScore;
	}
}
