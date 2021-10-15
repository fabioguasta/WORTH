import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import Utility.Esito;
import Utility.Utils;
import Utility.Notification;
import Utility.ChatProcess;
import Exceptions.UserNotFoundException;

//Classe client che legge i comandi e li inoltra al server
public class Client extends UnicastRemoteObject implements NotifyEventInterface{
    
    private static final long serialVersionUID = 5466266430079395311L;
    private final ServerInterface server;
    private String username;
    private String password;
    private boolean logged;
    private final Map<String, ChatProcess> chatList;
    private Map<String, Boolean> users;
    private static String serverAddress= "127.0.0.1";
    private final int BUFFER_DIM=1024;
    private static int RMIport= 5000;
    private static int TCPport=1919;
    private static int CHATport= 2000;
    private final String EXITcmd= "exit";
    private SocketChannel client;
    private boolean exitFlag;

    //crea un nuovo callback client
    public Client(ServerInterface server) throws RemoteException{
        super();
        this.users= new HashMap<>();
        this.chatList= new HashMap<>();
        this.server=server;
    }

    public Esito login(String username, String password) throws IOException, UserNotFoundException, ClassNotFoundException{
        this.username= username;
        this.password=password;
        server.registerForCallback(this, username);
        sendCommand(String.format("login %s %s", username, password));
        Esito response=getResponse();
        if(response.success){
            logged=true;
        } else{
            server.unregisterForCallback(username);
        }
        return response;
    }

    public Esito register(String username, String password) throws UserNotFoundException, IOException, ClassNotFoundException{
        if(logged){
            return new Esito(false, "Utente gia loggato");
        }
        Esito response= server.register(username, password);
        if(response.success)
            login(username, password);
        return response;
    }
    private void sendCommand(String command) throws IOException{
        ByteBuffer readBuff=ByteBuffer.wrap(command.getBytes());    //creo msg da inviare al server
        client.write(readBuff);
        readBuff.clear();
    }

    private Esito getResponse() throws IOException, ClassNotFoundException{
        ByteBuffer reply =ByteBuffer.allocate(BUFFER_DIM);
        client.read(reply);
        reply.flip();
        Esito response= (Esito) Utils.deserialize(reply.array());
        reply.clear();
        return response;
    }

    public void close(){
        try{
            if(logged){
                server.unregisterForCallback(this.username);
            }
        } catch (RemoteException | UserNotFoundException e){
            e.printStackTrace();
        }
        System.exit(1);
    }

    //stampa la lista di tutti gli utenti con relativo stato 
    private synchronized void listUsers(){
        for(Map.Entry<String,Boolean> user : users.entrySet())
            if(user.getValue())
                System.out.println(user.getKey()+" Online");
            else
                System.out.println(user.getKey()+ " Offline");
    }
    
    //stampa la lista degli utenti online
    private synchronized void listOnlineUsers(){
        for(Map.Entry<String, Boolean> user : users.entrySet())
            if(user.getValue())
                System.out.println(user.getKey());
    }
}
