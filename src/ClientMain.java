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
public class ClientMain extends UnicastRemoteObject implements NotifyEventInterface{
    
    private static final long serialVersionUID = 467266430079395311L;
    private final ServerInterface server;
    private String username;
    private String password;
    private boolean logged;
    private final Map<String, ChatProcess> chatList;
    private Map<String, Boolean> users;
    private static String serverAdd= "127.0.0.1";
    private final int BUFFER_DIM=1024;
    private static int RMIport= 5000;
    private static int TCPport=1919;
    private static int CHATport= 2000;
    private final String EXITcmd= "exit";
    private SocketChannel client;
    private boolean exitFlag;

    //crea un nuovo callback client
    public ClientMain(ServerInterface server) throws RemoteException{
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

    //riceve esito dell'operazione eseguita dal server
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

    public void start() throws IOException{

        try{
            client= SocketChannel.open(new InetSocketAddress(serverAdd, TCPport));
            BufferedReader consoleReader= new BufferedReader(new InputStreamReader(System.in));
            System.out.println("CLIENT CONNESSO");
            System.out.println("------------------------------------------------------------");
            System.out.println("Digitare help per mostrare la lista dei comandi disponibili");
            System.out.println("Digitare "+ this.EXITcmd + " per uscire");

            while(!this.exitFlag){
                System.out.print("> ");
                String message= consoleReader.readLine().trim();
                try{
                    executeCommand(message);
                } catch(ArrayIndexOutOfBoundsException e){
                    System.out.println("Missing argument");
                } catch(UserNotFoundException e){   
                    System.out.println(e.getMessage());
                } catch( ConnectException e){
                    System.out.println("server non raggiungibile, digita " + this.EXITcmd +"per uscire");
                }
            }
            System.out.println("CLIENT CHIUSO");
            close();

        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        } finally{
            client.close();
        }
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

    private synchronized void readChat(String chat){
        if(!chatList.containsKey(chat)){
            System.out.println("Chat non trovata");
            return;
        }
        for(String text: chatList.get(chat).readMsg())
            System.out.println(text);
    }

    private synchronized void sendChatMsg(String chat, String msg) throws IOException{
        if(!chatList.containsKey(chat)){
            System.out.println("Chat non trovata");
            return;
        }
        chatList.get(chat).sendMessage(this.username+ ": "+ msg);
        System.out.println("Messaggio inviato");
    }

    private void help(){
        System.out.println("-------------------- POSSIBILI COMANDI --------------------");
        System.out.println("register [username] [password]      registra un nuovo utente");
        System.out.println("login [username] [password]         effettua login");
        System.out.printf("%s                                  effettua logout \n", this.EXITcmd);
        System.out.println("createproject [project name]        crea un nuovo progetto");
        System.out.println("cancelproject [project name]        cancella un progetto (tutte le card devono essere DONE");
        System.out.println("listprojects                        mostra lista dei progetti di cui l'utente fa parte");
        System.out.println("showmembers [project name]          mostra membri di un progetto");
        System.out.println("addmember [project name] [username] aggiunge un nuovo membro al progetto");
        System.out.println("showcards [project name]            mostra le card relative ad un progetto");
        System.out.println("showcard [project name] [card name] mostra le informazioni relative ad una card");
        System.out.println("addcard [project name] [card name] [card description] aggiunge una nuova card al progetto");
        System.out.println("movecard [project name] [card name] [old state] [new state] cambia lo stato di una card");
        System.out.println("getcardhistory [project name] [card name] mostra la cronologia di una card");
        System.out.println("listusers                           mostra la lista di tutti gli utenti registrati");
        System.out.println("listonlineusers                     mostra tutti gli utenti attualmente online");
        System.out.println("readchat [project name]             mostra i messaggi della chat di un progetto");
        System.out.println("sendchatmsg [project name] \"message\" manda un messaggio nella chat in un progetto (il messaggio deve esser compreso tra \")");

        
    }

    private void executeCommand(String command) throws IOException,ClassNotFoundException,UserNotFoundException,ArrayIndexOutOfBoundsException{
        String[] splittedCmd = command.split(" ");
        Esito response;
        switch(splittedCmd[0].toLowerCase()){
            case "help":
                help();
                break;
            
            case "listusers":
              listUsers();
              break;
            
            case "listonlineusers":
                listOnlineUsers();
                break;

            case "readchat":
                readChat(splittedCmd[1]);
                break;
            
            case "sendchatmsg":
                String msg=command.split("\"")[1];
                sendChatMsg(splittedCmd[1], msg);
                break;
            
            case "login":
                response=login(splittedCmd[1], splittedCmd[2]);
                System.out.printf("< %s\n", response.msg);
                break;

            case "register":
                response=register(splittedCmd[1], splittedCmd[2]);
                System.out.printf("< %s\n", response.msg);
                break;

            case "listprojects":

            case "showmembers":

            case "showcards":

            case "showcard":

            case "getcardhistory":
                sendCommand(command);
                response=getResponse();
                System.out.printf("< %s\n", response.msg);
                if(response.success)
                    for(String text: response.list){
                        System.out.println(text);}
                break;

            case "":
                break;
            
            case EXITcmd:
                this.exitFlag=true;
                sendCommand(command);
                break;
            
            default:
                sendCommand(command);
                response=getResponse();
                System.out.printf("< %s\n", response.msg);
                break;
        }
    }


    private void updateChats(Map<String, String> projectChatIPs) throws IOException{
       
        //crea chat di eventuali nuovi progetti
        for(Map.Entry<String, String> chat: projectChatIPs.entrySet()){
            String project= chat.getKey();
            String address= chat.getValue();
            if(!chatList.containsKey(project)){
                ChatProcess chatThread= new ChatProcess(address, CHATport);
                chatList.put(project, chatThread);
                chatThread.start();
            }
        }

        //elimina chat di progetti eliminati
        Iterator<Entry<String, ChatProcess>> iterator= chatList.entrySet().iterator();
        while(iterator.hasNext()){
            Entry<String, ChatProcess> chat= iterator.next();
            if(!projectChatIPs.containsKey(chat.getKey())){
                chat.getValue().interrupt();
                iterator.remove();
            }
        }
    }

    // metodo che puo esser chiamato dal server per notificare il login di un utente
    // o un aggiornamento della lista dei progetti di cui l'utente fa parte
    public synchronized void notifyEvent(Notification notification)throws RemoteException{
        this.users=notification.utenti;
        try{
            updateChats(notification.projectChatIPs);
        } catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws RemoteException {
        ClientMain clientMain;

        try {
            if (args.length == 1) {
                serverAdd = args[0];
            }

            if(args.length == 2) {
                serverAdd = args[0];
                TCPport = Integer.parseInt(args[1]);
            }

            if(args.length == 3) {
                serverAdd = args[0];
                TCPport = Integer.parseInt(args[1]);
                RMIport = Integer.parseInt(args[2]);
            }

            if(args.length == 4) {
                serverAdd = args[0];
                TCPport = Integer.parseInt(args[1]);
                RMIport = Integer.parseInt(args[2]);
                CHATport = Integer.parseInt(args[3]);
            }

        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }


        try {
            Registry registry = LocateRegistry.getRegistry(RMIport);
            String name = "Server";
            ServerInterface server = (ServerInterface) registry.lookup(name);
            clientMain = new ClientMain(server);
            clientMain.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
