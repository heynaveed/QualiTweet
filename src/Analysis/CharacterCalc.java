package Analysis;

import java.text.DecimalFormat;
import java.util.ArrayList;


public final class CharacterCalc {
    
    private static final int CHARS_PER_TWEET_DECIMAL_LIMIT = 1;
    private static final DecimalFormat dF = new DecimalFormat();
    
    private float charsPerTweet;
    
    public CharacterCalc(ArrayList<String> arrayTweets){
        dF.setMaximumFractionDigits(CHARS_PER_TWEET_DECIMAL_LIMIT);
        updateCharsPerTweet(arrayTweets);
    }
    
    public CharacterCalc(String tweet){
        dF.setMaximumFractionDigits(CHARS_PER_TWEET_DECIMAL_LIMIT);
        updateCharsPerTweet(tweet);
    }
    
    public void updateCharsPerTweet(ArrayList<String> arrayTweets){
        float totalCharacters = 0;
        for(int i = 0; i < arrayTweets.size(); i++){
            totalCharacters += arrayTweets.get(i).length();
        }
        
        if(arrayTweets.size() != 0){
            charsPerTweet = Float.parseFloat(dF.format(totalCharacters/arrayTweets.size()));
        }
        else{
            charsPerTweet = 0;
        }
    }
    
    public void updateCharsPerTweet(String tweet){
        charsPerTweet = (float)tweet.length();
    }
    
    public DecimalFormat getDecimalFormat(){
        return dF;
    }
    
    public float getCharsPerTweet(){
        return charsPerTweet;
    }
}
