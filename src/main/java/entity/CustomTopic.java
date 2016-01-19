package entity;

public class CustomTopic implements Comparable<CustomTopic>{
	double score;
	String label;
		
	public CustomTopic(double score, String label) {
		super();
		this.score = score;
		this.label = label;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int compareTo(CustomTopic compareTopic) {
		return (int)(compareTopic.getScore() - this.getScore()); //decrescent
	}
	public String toString(){
		return "Label: " + this.label + " Score: " + this.score;
	}
}
