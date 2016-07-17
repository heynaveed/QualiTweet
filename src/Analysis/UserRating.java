package Analysis;

import Entity.User;
import Utils.JSONKeys;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class UserRating {
    
    private final JSONObject profileInfo;
    private final JSONArray lastRelevantTweets;
    private final int popularity;
    private final String username;
    private final ArrayList<Float> resultsList;
    
    public UserRating(User user){
        resultsList = new ArrayList<>();
        profileInfo = user.getProfileInfo();
        username = profileInfo.get(JSONKeys.username.toString()).toString();
        lastRelevantTweets = user.getJTweetArray();
        popularity = determineUserPopularity(calculateRawScore());
    }
    
    private double calculateRawScore(){
        return performZeroDivisionAnalysis(extractFollowerCount(), extractFollowingCount())*extractAVGRetweetPerTweetCount();
    }
    
    private int determineUserPopularity(double rawScore){
//        System.out.println(username + ": " + rawScore);
        if(rawScore <= 0){
            return 1;
        }
        else if(rawScore > 0 && rawScore <= 1){
            return 2;
        }
        else if(rawScore > 1 && rawScore <= 10){
            return 3;
        }
        else if(rawScore > 10 && rawScore <= 100){
            return 4;
        }
        else if(rawScore > 100 && rawScore <= 1000){
            return 5;
        }
        else if(rawScore > 1000 && rawScore <= 10000){
            return 6;
        }
        else if(rawScore > 10000 && rawScore <= 100000){
            return 7;
        }
        else if(rawScore > 100000 && rawScore <= 1000000){
            return 8;
        }
        else if(rawScore > 1000000 && rawScore <= 10000000){
            return 9;
        }
        else{
            return 10;
        }
    }
    
    private double extractFollowerCount(){
        return Float.parseFloat(profileInfo.get(JSONKeys.followersCount.toString()).toString());
    }
    
    private double extractFollowingCount(){
        return Float.parseFloat(profileInfo.get(JSONKeys.followingCount.toString()).toString());
    }
    
    private double extractAVGRetweetPerTweetCount(){
        float total = 0;
        
        for(int i = 0; i < lastRelevantTweets.size(); i++){
            total += Float.parseFloat(((JSONObject)lastRelevantTweets.get(i)).get(JSONKeys.retweetCount.toString()).toString());
        }
        
        return total/lastRelevantTweets.size();
    }
    
    private double performZeroDivisionAnalysis(double numerator, double denominator){
        if(denominator != 0){
            return (numerator/denominator);
        }
        else{
            return numerator;
        }
    }
    
    public void printList(){
        for(int i = 0; i < resultsList.size(); i++){
            System.out.println(resultsList.get(i));
        }
    }
    
    public int getPopularity(){
        return popularity;
    }
    
    public ArrayList<Float> getList(){
        return resultsList;
    }
    
    public String getUsername(){
        return username;
    }
}
