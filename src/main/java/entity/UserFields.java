package entity;

import java.util.ArrayList;

/**
 * Class used for the creation of the index in Lucene. Used with reflection.
 *
 */
public class UserFields {
	
	public static final String MALE = "male";
	public static final String FEMALE = "female";
	
	public int follower;
	public String screenName;
	public String city;
	public String country;
	public double latitude;
	public double longitude;
	public String address;
	public String profileBigImage;
	public String gender;
	public String age;
	public ArrayList<String> tweet = new ArrayList<String>();
}
