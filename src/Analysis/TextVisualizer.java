package Analysis;

import Entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public final class TextVisualizer {
    
    private static final int FONT_SIZE = 20;
    private static final int FONT_MULTIPLYER = FONT_SIZE*20;
    private static final int RECORDS_TO_VISUALIZE = 10;
    
    private HashMap<String, Integer> visualMap;
    
    public TextVisualizer(User user){
        updateVisualProperties(user);
    }
    
    public void updateVisualProperties(User user){
        this.visualMap = new HashMap<>();
        ArrayList<Map.Entry> list = user.getWordMapper().getOrderedMapList().getList();

        for(int i = list.size(); i > RECORDS_TO_VISUALIZE; i--){
            list.remove(i-1);
        }
        
        float total = 0;
        for(int i = 0; i < list.size(); i++){
            total += (Integer)list.get(i).getValue();
        }
        
        for(int i = 0; i < list.size(); i++){
            visualMap.put((String)list.get(i).getKey(), Math.round(((Integer)list.get(i).getValue()/total)*FONT_MULTIPLYER));
        }
    }
    
    public void printVisualProperties(){
        for (Map.Entry pair : visualMap.entrySet()) {
            System.out.println(pair.getKey() + " = " + pair.getValue());
        }
    }
    
    public HashMap<String, Integer> getVisualMap(){
        return visualMap;
    }
}
