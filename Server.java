import Exceptions.*;
import Utility.Esito;
import Utility.Utils;
import Utility.Notification;
import Utility.ChatProcess;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.*;

//Classe server che esegue le richieste effettuate dai client
public class Server extends RemoteObject implements ServerInterface{

    private static final long serialVersionUID=150859790584022983L;
    private final UserSet users;
    private final ProjectSet projects;

    //dimensione del buffer di lettura
    private final int BUFFER_DIM=1024;

    //porta su cui aprire il listening socket
    private static int RMIport=5000;
    private static int TCPport=1919;

    //informazioni relative allo storage
    private final String storageDir= "./storage";
    private final String userFilePath= storageDir + "/user.json";
    private final String projectsDir= storageDir +"/projects";

    private final StorageManager storage;
    private final ObjectMapper mapper;

    //comando usato dal client per comunicare la fine della comunicazione
    private final String EXIT_CMD="exit";

    private static int CHAT_PORT=2000;

    //crea server
    public Server() throws IOException{
        super();
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        storage= new StorageManager(storageDir, userFilePath, projectsDir);
        users= new UserSet(storage);
        projects= new ProjectSet(storage);
    }

    public synchronized void registerForCallback(NotifyEventInterface clientInterface, String username)throws RemoteException, UserNotFoundException{
        users.setClient(username,clientInterface);
    }
    
    //annulla registrazione per il callback
    public synchronized void unregisterForCallback(String username)throws RemoteException,UserNotFoundException{
        users.setClient(username, null);
    }

    public void notifyUsers(){
        users.notifyAll(projects);
    }

    @Override
    public Esito register(String username, String password) throws RemoteException{
        System.out.println("Username: %s \n Password: %s \n", username, password);

        try{
            boolean flag= users.register(username, password);
            if(flag){
                notifyUsers();
                return new Esito(true, "Registrazione avvenuta con successo");
            } else{
                return new Esito(false, "Utente gia presente");
            } 
        } catch (IOException e){
            e.printStackTrace();
            return new Esito(false, e.toString());
        }
    }

    //avvia esecuzione del server
    public void start(){
        try(ServerSocketChannel sChannel= ServerSocketChannel.open()){
            
        }
    }
}
