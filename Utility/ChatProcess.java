package Utility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.List;

//Utilizzata dal client per creare thread che rimangano in ascolto di messaggi sulla chat
public class ChatProcess extends Thread{
    private final MessageQueue queue;
    private final int port;
    private final MulticastSocket multicast;
    private final InetAddress group;
    private final static int BUFFER_SIZE=4096;

    public ChatProcess(String groupAdd, int port){
        this.port=port;
        queue=new MessageQueue();
        this.multicast=new MulticastSocket(port);
        this.group=InetAddress.getByName(groupAdd);
    }
    
    public List<String> readMsg(){
        return queue.getAndClear();
    }

    public void sendMsg(String msg) throws NullPointerException, IOException{
        if(msg==null) throw new NullPointerException();
        
    }

    
}
