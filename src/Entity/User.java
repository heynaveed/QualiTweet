package Entity;


import Analysis.CharacterCalc;
import Analysis.TextVisualizer;
import Analysis.TweetStatistics;
import Analysis.UserRating;
import Analysis.WordMapper;
import Http.ConnectionHandler;
import Utils.BearerToken;
import Utils.JSONKeys;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public final class User {
    
    private final JSONObject profileInfo;
    private final JSONArray jTweetArray;
    
    private boolean isValidUsername;
    private String username;
    private WordMapper wMapper;
    private CharacterCalc cCalc;
    private TextVisualizer tVis;
    private UserRating userR;
    private ArrayList<String> tweetArrayList;
    private TweetStatistics tS;
    
    public User(String username, BearerToken bToken) throws IOException{
        profileInfo = requestProfileInfo(new ConnectionHandler("https://api.twitter.com/1.1/users/show.json?screen_name=" + username + "&user_id=2868022389", bToken));
        jTweetArray = requestTweets(new ConnectionHandler("https://api.twitter.com/1.1/statuses/user_timeline.json?count=200&include_rts=false&exclude_replies=true&user_id=2868022389&screen_name=" + username, bToken));
        this.username = "";
        
        if(isValidUsername){
            this.username = profileInfo.get(JSONKeys.username.toString()).toString();
            updateTweetList();
            tS = new TweetStatistics(tweetArrayList, false);
            wMapper = new WordMapper(tweetArrayList);
            cCalc = new CharacterCalc(tweetArrayList);
            tVis = new TextVisualizer(this);
            userR = new UserRating(this);
        }
    }
    
    private JSONObject requestProfileInfo(ConnectionHandler con) throws ProtocolException, IOException{
        if(con.getResponse().isEmpty()){
            isValidUsername = false;
        }
        else{
            isValidUsername = true;
        }
        return (JSONObject)JSONValue.parse(con.getResponse());
    }
    
    private JSONArray requestTweets(ConnectionHandler con) throws ProtocolException, IOException{
        return (JSONArray)JSONValue.parse(con.getResponse());
    }
    
    public void updateTweetList(){
        tweetArrayList = new ArrayList<>();
        for(int i = 0; i < jTweetArray.size(); i++){
            tweetArrayList.add(((JSONObject)jTweetArray.get(i)).get(JSONKeys.tweetText.toString()).toString());
        }
    }

    public void printProfileInfo(){
        if(profileInfo != null){
            System.out.println("Username: " + profileInfo.get(JSONKeys.username.toString()));
            System.out.println("Tweets: " + profileInfo.get(JSONKeys.tweetCount.toString()));
            System.out.println("Following: " + profileInfo.get(JSONKeys.followingCount.toString()));
            System.out.println("Followers: " + profileInfo.get(JSONKeys.followersCount.toString()));
            System.out.println("Likes: " + profileInfo.get(JSONKeys.likes.toString()));
            System.out.println("Description: " + profileInfo.get(JSONKeys.description.toString()));
            System.out.println("Location: " + profileInfo.get(JSONKeys.location.toString()));
            System.out.println("Date Created: " + profileInfo.get(JSONKeys.dateCreated.toString()));
            System.out.println("Characters Per Tweet: " + cCalc.getCharsPerTweet());
            System.out.println("Popularity: " + userR.getPopularity() + "/10");
            //System.out.println("Default profile photo: " + profileInfo.get(DataKey.defaultProfileImage.toString()));
        }
    }
    
    public void printTweets(){
        //System.out.println(username + "'s last " + tweetArray.size() + " tweets.\nNo replies or RTS included!\n");
        for(int i = 0; i < tweetArrayList.size(); i++){
            System.out.println(tweetArrayList.get(i));
        }
    }
    
    public void setWordMapper(WordMapper wMapper){
        this.wMapper = wMapper;
    }
    
    public void setCharacterCalc(CharacterCalc cCalc){
        this.cCalc = cCalc;
    }
    
    public void setTextVisualizer(TextVisualizer tVis){
        this.tVis = tVis;
    }
    
    public void setUserRating(UserRating userR){
        this.userR = userR;
    }
    
    public JSONObject getProfileInfo(){
        return profileInfo;
    }
    
    public JSONArray getJTweetArray(){
        return jTweetArray;
    }
    
    public String getUsername(){
        return username;
    }
    
    public WordMapper getWordMapper(){
        return wMapper;
    }
    
    public CharacterCalc getCharacterCalc(){
        return cCalc;
    }
    
    public TextVisualizer getTextVisualizer(){
        return tVis;
    }
    
    public UserRating getUserRating(){
        return userR;
    }
    
    public ArrayList<String> getTweetArrayList(){
        return tweetArrayList;
    }
    
    public boolean getIsValidUsername(){
        return isValidUsername;
    }
    
    public TweetStatistics getTweetStatistics(){
        return tS;
    }
}
