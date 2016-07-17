package Analysis;


import Entity.User;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public final class CompareUsers {
    
    public static final String newline = System.getProperty("line.separator");
    private static final int TOP_WORD_LIMIT = 5;

    private final HashMap<String, Integer> combinedKeywordMap;
    private final OrderedMapList oMapL;
    private final float charsPerTweetRange;
    
    private User slicedUser;
    private User regularUser;
    
    public CompareUsers(User user1, User user2){
        combinedKeywordMap = new HashMap<>();
        calculateComparisonLimit(user1, user2);
        charsPerTweetRange = calculateCharsPerTweetRange();
        generateKeywordMatches();
        oMapL = new OrderedMapList(combinedKeywordMap, TOP_WORD_LIMIT);
    }
    
    private void calculateComparisonLimit(User user1, User user2){
        if(user1.getJTweetArray().size() > user2.getJTweetArray().size()){
            slicedUser = user1;
            regularUser = user2;
            applyComparisonLimit(user1.getJTweetArray().size() - user2.getJTweetArray().size());
        }
        else{
            slicedUser = user2;
            regularUser = user1;
            applyComparisonLimit(user2.getJTweetArray().size() - user1.getJTweetArray().size());
        }
    }
    
    private void applyComparisonLimit(int comparisonLimit){
        int originalSize = slicedUser.getJTweetArray().size();
        if(comparisonLimit != 0){
            for(int i = slicedUser.getJTweetArray().size()-1; i >= (originalSize - comparisonLimit); i--){
                slicedUser.getJTweetArray().remove(i);
            }
        }

        slicedUser.updateTweetList();
        
        slicedUser.setWordMapper(new WordMapper(slicedUser.getTweetArrayList()));
        slicedUser.setCharacterCalc(new CharacterCalc(slicedUser.getTweetArrayList()));
        slicedUser.setTextVisualizer(new TextVisualizer(slicedUser));
        slicedUser.setUserRating(new UserRating(slicedUser));
//        slicedUser.getWordMapper().updateKeywordMap(slicedUser.getTweetArrayList());
//        slicedUser.getTextVisualizer().updateVisualProperties(slicedUser);
//        slicedUser.getCharacterCalc().updateCharsPerTweet(slicedUser.getTweetArrayList());
    }
    
    private void generateKeywordMatches(){
        
        Iterator slicedIt = slicedUser.getWordMapper().getKeywordMap().entrySet().iterator();
        Iterator regularIt = regularUser.getWordMapper().getKeywordMap().entrySet().iterator();
        
        while(slicedIt.hasNext()){
            Map.Entry pair = (Map.Entry)slicedIt.next();
            applyWordValidation(pair);
        }
        
        while(regularIt.hasNext()){
            Map.Entry pair = (Map.Entry)regularIt.next();
            applyWordValidation(pair);
        }
    }
    
    private void applyWordValidation(Map.Entry pair){
        if(isWordInBothUsers((String)pair.getKey())){
            
            if(!combinedKeywordMap.containsKey(pair.getKey().toString())){
                combinedKeywordMap.put((String)pair.getKey(), ((Integer)pair.getValue()));
            }
            else{
                combinedKeywordMap.put((String)pair.getKey(), combinedKeywordMap.get(pair.getKey().toString()) + (Integer)pair.getValue());
            }
        }
    }
    
    private boolean isWordInBothUsers(String word){
        return regularUser.getWordMapper().getKeywordMap().containsKey(word) && slicedUser.getWordMapper().getKeywordMap().containsKey(word);
    }
    
    private float calculateCharsPerTweetRange(){
        return Math.abs(Float.parseFloat(regularUser.getCharacterCalc().getDecimalFormat().format(
                regularUser.getCharacterCalc().getCharsPerTweet() - 
                        slicedUser.getCharacterCalc().getCharsPerTweet())));
    }
    
    public void printCombinedKeywordMap(){

        Iterator it = combinedKeywordMap.entrySet().iterator();
        
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey());
            it.remove();
        }
    }
    
    public void printComparisonResults(){
        System.out.println("Comparison between: \"" + regularUser.getUsername() + "\" and \"" + slicedUser.getUsername() + "\"");
        System.out.println("===========================================================");
        
        System.out.println("Results are based on their last " + regularUser.getJTweetArray().size() + " tweets.");
        System.out.println("This does NOT include retweets or replies!");
        
        System.out.println(newline + "Top " + oMapL.getList().size() + " word matches: ");
        System.out.println("=================================");
        oMapL.printList();
        
        System.out.println(newline + "Characters Per Tweet (CPT): ");
        System.out.println("=================================");
        System.out.println(regularUser.getUsername() + ": " + regularUser.getCharacterCalc().getCharsPerTweet());
        System.out.println(slicedUser.getUsername() + ": " + slicedUser.getCharacterCalc().getCharsPerTweet());
        System.out.println("CPT Range: " + charsPerTweetRange + newline);

        System.out.println("Popularity:");
        System.out.println("=================================");
        System.out.println(regularUser.getUsername() + ": " + regularUser.getUserRating().getPopularity() + "/10");
        System.out.println(slicedUser.getUsername() + ": " + slicedUser.getUserRating().getPopularity() + "/10");
        System.out.println();
    }
    
    public HashMap getCombinedKeywordMap(){
        return combinedKeywordMap;
    }
    
    public OrderedMapList getOrderedMapList(){
        return oMapL;
    }
    
    public float getCharactersPerTweetDifference(){
        return charsPerTweetRange;
    }
    
    public User getSlicedUser(){
        return slicedUser;
    }
    
    public User getRegularUser(){
        return regularUser;
    }
}
