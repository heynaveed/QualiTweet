package Utils;


public enum StatKeys {
    
    original(0), cleanUp(1), hyperlinks(0), totalWords(1), spamWords(2), capitals(3),
    numbers(4), hashtags(5), mentions(6), htmlTags(7),
    repeatedWords(8);
    
    private final int key;
    
    private StatKeys(int key){
        this.key = key;
    }
    
    public boolean equalsKey(int otherKey){
        return key == otherKey;
    }
    
    public int getKey(){
        return key;
    }
}
