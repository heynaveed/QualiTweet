package Analysis;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public final class OrderedMapList {
    
    private final int limit;
    
    private ArrayList<Map.Entry> orderedList;
    
    public OrderedMapList(HashMap<String, Integer> map, int limit){
        this.limit = limit;
        updateOrderedList(map);
    }
    
    private void updateOrderedList(HashMap<String, Integer> map){
        orderedList = new ArrayList<>();
        
        for (Map.Entry pair : map.entrySet()) {
            if(!orderedList.isEmpty()){
                
                for(int i = 0; i < orderedList.size(); i++){
                    
                    if((Integer)pair.getValue() >= (Integer)orderedList.get(i).getValue()){
                        orderedList.add(i, pair);
                        if(orderedList.size() > limit){
                            orderedList.remove(limit);
                        }
                        break;
                    }
                }
            }
            else{
                orderedList.add(pair);
            }
        }
    }
    
    public void printList(){
        
        if(orderedList.isEmpty()){
            System.out.println("List is empty.");
        }
        
        else{
            for(int i = 0; i < orderedList.size(); i++){
                System.out.println((i+1) + ". " + orderedList.get(i).getKey() + " = " + orderedList.get(i).getValue());
            }
        }
    }
    
    public ArrayList<Map.Entry> getList(){
        return orderedList;
    }
}
