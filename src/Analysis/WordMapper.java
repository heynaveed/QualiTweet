package Analysis;


import Utils.WordArrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public final class WordMapper {
    
    private static final int WORD_CHARACTER_LIMIT = 2;
    private static final int RECORD_LIMIT = 100;
    
    private HashMap<String, Integer> keywordMap;
    private OrderedMapList oMapL;
    
    public WordMapper(ArrayList<String> tweetList){
        updateKeywordMap(tweetList);
    }
    
    public WordMapper(String[] unwordedTweet){
        updateKeywordMap(unwordedTweet);
    }
    
    private void trimKeywordMap(){
        for(int i = 0; i < WordArrays.EXEMPT_WORDS.length; i++){
            keywordMap.remove(WordArrays.EXEMPT_WORDS[i]);
        }
    }
    
    private String[] eradicateSpecialCases(String[] text){
        
        for(int i = 0; i < text.length; i++){
            
            if(text[i].contains("http") || text[i].contains("@") || 
                    text[i].contains("#")){
                text[i] = null;
            }
            else{
                text[i] = text[i].replaceAll("[^A-Za-z]", "");
                text[i] = text[i].toLowerCase();
                text[i] = text[i].trim();
            }
            
            if(text[i] != null){
                
                if(text[i].length() <= WORD_CHARACTER_LIMIT){
                    text[i] = null;
                }
            }
        }
        
        return text;
    }
    
    private void populateMap(String[] currentTweet){
        for(int j = 0; j < currentTweet.length; j++){
               
            if(currentTweet[j] != null){
                if(!keywordMap.containsKey(currentTweet[j])){
                    keywordMap.put(currentTweet[j], 0);
                }
                keywordMap.put(currentTweet[j], keywordMap.get(currentTweet[j])+1);
            }
        }
    }
    
    public void updateKeywordMap(ArrayList<String> tweetList){
        keywordMap = new HashMap<>();
        
        for(int i = 0 ; i < tweetList.size(); i++){
            String[] currentTweet = eradicateSpecialCases(tweetList.get(i).split(" "));
            populateMap(currentTweet);
        }
        
        trimKeywordMap();
        this.oMapL = new OrderedMapList(keywordMap, RECORD_LIMIT);
    }
    
    public void updateKeywordMap(String[] unwordedTweet){
        keywordMap = new HashMap<>();
        
        populateMap(unwordedTweet);
        trimKeywordMap();
        this.oMapL = new OrderedMapList(keywordMap, RECORD_LIMIT);
    }
    
    public void printKeywordMap(){

        Iterator it = keywordMap.entrySet().iterator();
        
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove();
        }
    }
    
    public HashMap<String, Integer> getKeywordMap(){
        return keywordMap;
    }
    
    public OrderedMapList getOrderedMapList(){
        return oMapL;
    }
}
