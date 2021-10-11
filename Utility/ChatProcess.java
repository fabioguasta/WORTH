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

    public void sendMessage(String msg) throws NullPointerException, IOException{
        if(msg==null) throw new NullPointerException();
        byte[] buffer=msg.getBytes();
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, this.group, this.port);
        multicast.send(datagram);
    }

    public void receive() throws IOException{
        byte[] msg= new byte[BUFFER_SIZE];
        DatagramPacket PACK= new DatagramPacket(msg, msg.length, this.group, this.port);
        multicast.receive(PACK);
        String message= new String(PACK.getData(),0, PACK.getLength(), StandardCharsets.UTF_8);

        addMsg(message);
    }

    public void addMsg(String message){
        queue.put(message);
    }

    public static void sendMsg(String groupAddress, int port, String message) throws IOException{
        InetAddress group=InetAddress.getByName(groupAddress);
        MulticastSocket multicast= new MulticastSocket(port);
        byte[] buffer= message.getBytes();
        DatagramPacket datagram= new DatagramPacket(buffer, buffer.length, group, port);
        multicast.send(datagram);
    }

    @Override
    public void run() {
        try {
            multicast.joinGroup(group);
            while (!Thread.interrupted()) {
                recive();
            }
            multicast.leaveGroup(group);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            multicast.close();
        }
    }
}
