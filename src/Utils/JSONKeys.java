package Utils;


public enum JSONKeys {
    
    username ("screen_name"), tweetCount ("statuses_count"),
    followingCount ("friends_count"), followersCount ("followers_count"),
    likes ("favourites_count"), description ("description"),
    location ("location"), dateCreated ("created_at"),
    tweetText ("text"), defaultProfileImage("default_profile_image"),
    statuses ("statuses"), retweetCount("retweet_count"), 
    favouritesCount("favourites_count");
    
    private final String key;
    
    private JSONKeys(String str){
        key = str;
    }
    
    public boolean equalsKey(String otherKey){
        return (otherKey == null) ? false  : key.equals(otherKey);
    }
    
    @Override
    public String toString(){
        return key;
    }
}
