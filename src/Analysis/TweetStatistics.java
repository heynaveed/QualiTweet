package Analysis;

import Utils.StatKeys;
import Utils.WordArrays;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TweetStatistics {
    
    private static final String FIND_STR = "http";
    private static final String LEFT_HTML_TAG = "&lt;";
    private static final String RIGHT_HTML_TAG = "&gt;";
    private static final int HASHTAG_SIZE = 1;
    private static final int MENTIONS_SIZE = 1;
    private static final int MIN_WORD_LENGTH = 3;
    private static final int STRING_LIST_SIZE = 2;
    private static final int INTEGER_LIST_SIZE = 9;
    
    private final ArrayList<ArrayList<String>> inputLists;
    private final ArrayList<ArrayList<Integer>> resultsLists;
    private final ArrayList<String> tweetArrayList;
    private final ArrayList<Float> averagesList;
    private final boolean isFromFile;
    
    private FileInputStream fIn;
    private BufferedReader br;
    
    public TweetStatistics(String filePath, boolean isFromFile) throws IOException{
        this.isFromFile = isFromFile;
        tweetArrayList = new ArrayList<>();
        this.inputLists = new ArrayList<>();
        for(int i = 0; i < STRING_LIST_SIZE; i++){
            inputLists.add(new ArrayList<>());
        }
        
        this.resultsLists = new ArrayList<>();
        for(int i = 0; i < INTEGER_LIST_SIZE; i++){
            resultsLists.add(new ArrayList<>());
        }
        
        this.averagesList = new ArrayList<>();
        
        fIn = new FileInputStream(filePath);
        br = new BufferedReader(new InputStreamReader(fIn));
        
        cleanUpData();
        applyCleanDataAnalysis();
        applyOriginalDataAnalysis();
        calculateAverages();
    }
    
    public TweetStatistics(ArrayList<String> tweetArrayList, boolean isFromFile) throws IOException{
        this.isFromFile = isFromFile;
        this.inputLists = new ArrayList<>();
        for(int i = 0; i < STRING_LIST_SIZE; i++){
            inputLists.add(new ArrayList<>());
        }
        
        this.resultsLists = new ArrayList<>();
        for(int i = 0; i < INTEGER_LIST_SIZE; i++){
            resultsLists.add(new ArrayList<>());
        }
        this.tweetArrayList = tweetArrayList;
        this.averagesList = new ArrayList<>();
        
        cleanUpData();    
        applyCleanDataAnalysis();
        applyOriginalDataAnalysis();
        calculateAverages();
    }
    
    private void resetBuffer(){
        try {
            fIn.getChannel().position(0);
            br = new BufferedReader (new InputStreamReader(fIn));
        } catch (IOException ex) {
            Logger.getLogger(TweetStatistics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void cleanUpData(){
        
        try{
            int count = 0;
            String line = null;
            if(isFromFile){
                line = br.readLine();
            }
            else if(!tweetArrayList.isEmpty()){
                line = tweetArrayList.get(count);
            }
            else{
                line = null;
            }
            
            while(line != null){
                
                populateOriginal(line);
                
                String[] splitLine = line.split(" ");
                
                for(int i = 0; i < splitLine.length; i++){
                    if(splitLine[i].contains("http") || splitLine[i].contains("www")){
                        splitLine[i] = "";
                    }
                    
                    else{
                        splitLine[i] = splitLine[i].replaceAll("[^A-Za-z]", "");
                        splitLine[i] = splitLine[i].toLowerCase();
                    }
                    
                    splitLine[i] = splitLine[i].trim();
                }
                
                String newString = "";
                if(splitLine.length > 0){
                    newString = splitLine[0] + " ";

                    for(int i = 1; i < splitLine.length; i++){
                        if(splitLine[i] != null){
                            newString = newString.concat(splitLine[i] + " ");
                        }
                    }
                }
                
                //System.out.println(newString);
                inputLists.get(StatKeys.cleanUp.getKey()).add(newString.trim());
                
                if(isFromFile){
                    line = br.readLine();
                }
                else{
                    
                    if(count < tweetArrayList.size()-1){
                        count++;
                        line = tweetArrayList.get(count);
                    }
                    else{
                        line = null;
                    }
                }
            }
            
            if(isFromFile)
                resetBuffer();
        }
        catch(IOException e){
        }
    }
    
    private void applyCleanDataAnalysis(){
        for(int i = 0; i < inputLists.get(StatKeys.cleanUp.getKey()).size(); i++){
            String[] currentLine = inputLists.get(StatKeys.cleanUp.getKey()).get(i).split(" ");
            calculateTotalWords(currentLine);
            calculateSpamWords(currentLine);
            calculateRepeatedWords(currentLine);
        }
    }
    
    private void applyOriginalDataAnalysis() throws IOException{
        try{
            int count = 0;
            String line = null;
            
            if(isFromFile){
                line = br.readLine();
            }
            else if(!tweetArrayList.isEmpty()){
                line = tweetArrayList.get(count);
            }
            else{
                line = null;
            }
            
            while(line != null){
                String[] splitLine = line.split(" ");
                calculateHyperlinks(line);
                calculateCapitals(splitLine);
                calculateNumbers(splitLine);
                calculateHashtags(splitLine);
                calculateMentions(splitLine);
                calculateHTMLTags(line);
                
                if(isFromFile){
                    line = br.readLine();
                }
                else{
                    
                    if(count < tweetArrayList.size()-1){
                        count++;
                        line = tweetArrayList.get(count);
                    }
                    else{
                        line = null;
                    }
                }
            }
            
            if(isFromFile)
            resetBuffer();
        }
        catch(IOException e){
        }
    }
    
    private void populateOriginal(String line){
        inputLists.get(StatKeys.original.getKey()).add(line);
    }
    
    private void calculateHyperlinks(String line){
        int lastIndex = 0;
        int count = 0;
                
        while(lastIndex != -1){
            lastIndex = line.indexOf(FIND_STR, lastIndex);
                    
            if(lastIndex != -1){
                count++;
                lastIndex += FIND_STR.length();
            }
        }
                
        resultsLists.get(StatKeys.hyperlinks.getKey()).add(count);   
    }
    
    private void calculateTotalWords(String[] currentLine){      
        int whiteSpaceSub = 0;
        for(int j = 0; j < currentLine.length; j++){
            if(currentLine[j].trim().length() == 0){
                whiteSpaceSub++;
            }
        }
        resultsLists.get(StatKeys.totalWords.getKey()).add(currentLine.length - whiteSpaceSub);
    }
       
    private void calculateSpamWords(String[] currentLine){
        int count = 0;    
        for(int j = 0; j < currentLine.length; j++){
            for(int k = 0; k < WordArrays.SPAM_WORDS.length; k++){
                if(currentLine[j].equals(WordArrays.SPAM_WORDS[k])){
                    count++;
                }
            }
        }     
        resultsLists.get(StatKeys.spamWords.getKey()).add(count);
    }
    
    private void calculateCapitals(String[] currentLine){
        int count = 0;

        for(int i = 0; i < currentLine.length; i++){
            if(currentLine[i].contains("www") || currentLine[i].contains("http")){
                currentLine[i] = "";
            }
                    
            else{
                for(int j = 0; j < currentLine[i].length(); j++){
                    if(Character.isUpperCase(currentLine[i].charAt(j))){
                        count++;
                    }
                }
            }
        }  
        resultsLists.get(StatKeys.capitals.getKey()).add(count);
    }
    
    private void calculateNumbers(String[] currentLine){
        int count = 0;

        for(int i = 0; i < currentLine.length; i++){
            if(currentLine[i].contains("www") || currentLine[i].contains("http")){
                currentLine[i] = "";
            }
                    
            else{
                for(int j = 0; j < currentLine[i].length(); j++){
                    if(Character.isDigit(currentLine[i].charAt(j))){
                        count++;
                    }
                }
            }
        }
                
        resultsLists.get(StatKeys.numbers.getKey()).add(count);
    }
    
    private void calculateHashtags(String[] currentLine){
        int count = 0;
                
        for(int i = 0; i < currentLine.length; i++){
            if(currentLine[i].startsWith("#") && currentLine[i].length() > HASHTAG_SIZE
                && Character.isAlphabetic(currentLine[i].charAt(HASHTAG_SIZE))){
                    count++;
            }
        }         
        resultsLists.get(StatKeys.hashtags.getKey()).add(count);
    }
    
    private void calculateMentions(String[] currentLine){
        int count = 0;
                
        for(int i = 0; i < currentLine.length; i++){
            if(currentLine[i].startsWith("@") && currentLine[i].length() > MENTIONS_SIZE
                && Character.isAlphabetic(currentLine[i].charAt(MENTIONS_SIZE))){
                    count++;
            }
        }
        resultsLists.get(StatKeys.mentions.getKey()).add(count);
    }
    
    private void calculateHTMLTags(String line){
        int lastIndex1 = 0;
        int lastIndex2 = 0;
        int count = 0;
                
        while(lastIndex1 != -1){
            lastIndex1 = line.indexOf(LEFT_HTML_TAG, lastIndex1);
                    
            if(lastIndex1 != -1){
                count++;
                lastIndex1 += LEFT_HTML_TAG.length();
            }
        }
                
        while(lastIndex2 != -1){
            lastIndex2 = line.indexOf(RIGHT_HTML_TAG, lastIndex2);
                    
            if(lastIndex2 != -1){
                count++;
                lastIndex2 += RIGHT_HTML_TAG.length();
            }
        }
        resultsLists.get(StatKeys.htmlTags.getKey()).add(count);
    }
    
    private void calculateRepeatedWords(String[] currentLine){
        WordMapper tM = new WordMapper(removeExemptWords(removeShortWords(currentLine)));
        resultsLists.get(StatKeys.repeatedWords.getKey()).add(checkForRepeats(tM.getKeywordMap()));
    }
    
    private int checkForRepeats(HashMap map){
        Iterator it = map.entrySet().iterator();
        int count = 0;
        
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            
            if((Integer)pair.getValue() > 1){
                count++;
            }
            
            it.remove();
        }
        return count;
    }
    
    private String[] removeShortWords(String[] arr){
        for(int i = 0; i < arr.length; i++){
            if(arr[i].length() <= MIN_WORD_LENGTH){
                arr[i] = null;
            }
        }
        return arr;
    }
    
    private String[] removeExemptWords(String[] arr){
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < WordArrays.EXEMPT_WORDS.length; j++){
                if(arr[i] != null)
                {
                    if(arr[i].equals(WordArrays.EXEMPT_WORDS[j])){
                        arr[i] = "";
                        break;
                    }
                }
            }
        }
        
        return arr;
    }
    
    public void printStats(){
        for(int i = 0; i < inputLists.get(StatKeys.cleanUp.getKey()).size(); i++){
            System.out.println(resultsLists.get(StatKeys.hyperlinks.getKey()).get(i) + "\t" + 
                              resultsLists.get(StatKeys.totalWords.getKey()).get(i) + "\t" + 
                              resultsLists.get(StatKeys.spamWords.getKey()).get(i) + "\t" + 
                              resultsLists.get(StatKeys.capitals.getKey()).get(i) + "\t" + 
                              resultsLists.get(StatKeys.numbers.getKey()).get(i) + "\t" + 
                              resultsLists.get(StatKeys.hashtags.getKey()).get(i) + "\t" + 
                              resultsLists.get(StatKeys.mentions.getKey()).get(i) + "\t" + 
                              resultsLists.get(StatKeys.htmlTags.getKey()).get(i) + "\t" + 
                              resultsLists.get(StatKeys.repeatedWords.getKey()).get(i));
        }
    }
    
    private void calculateAverages(){
        for(int i = 0; i < resultsLists.size(); i++){
            float total = 0;
            for(int j = 0; j < resultsLists.get(i).size(); j++){
                total += resultsLists.get(i).get(j);
            }
            averagesList.add(total/resultsLists.get(i).size());
        }
    }
    
    public void printAverages(){
        for(int i = 0; i < averagesList.size(); i++){
            System.out.println(averagesList);
        }
    }
    
    public ArrayList<Float> getAveragesList(){
        return averagesList;
    }
}
