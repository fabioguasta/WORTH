package Utility;

import java.util.ArrayList;
import java.util.List;

public class MessageQueue {
    
    private final List<String> queue;

    public MessageQueue(){
        queue=new ArrayList<>();
    }

    public synchronized void put(String s){
        queue.add(s);
    }

    public synchronized List<String> getAndClear(){
        List<String> old= new ArrayList<>(queue);
        queue.clear();
        return old;
    }
    
}
