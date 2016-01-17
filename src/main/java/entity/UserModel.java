package entity;


public class UserModel extends UserFields {
	private int numberOfTweets;
	private double score;
	private String name;
	private String profileURL;
	private String imageURL;
	
	public int getNumberOfTweets() {
		return numberOfTweets;
	}
	public void setNumberOfTweets(int numberOfTweets) {
		this.numberOfTweets = numberOfTweets;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProfileURL() {
		return profileURL;
	}
	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}	
}
