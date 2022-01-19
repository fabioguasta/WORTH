package Utility;

import java.util.ArrayList;
import java.util.List;

public class MessageQueue {
    
    private final List<String> queue;

    public MessageQueue(){
        queue=new ArrayList<>();
    }

    public synchronized void put(String message){
        queue.add(message);
    }

    public synchronized List<String> getAndClear(){
        List<String> old= new ArrayList<>(queue);
        queue.clear();
        return old;
    }
    
}
