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
public class ServerMain extends RemoteObject implements ServerInterface{

    private static final long serialVersionUID=158859790584022983L;
    private final UserSet users;
    private final ProjectSet projects;

    //dimensione del buffer di lettura
    private final int BUFFER_DIM=1024;

    //porta su cui aprire il listening socket
    private static int RMIport=5000;
    private static int TCPport=1919;

    //informazioni relative allo storage
    private final String storageDir= "./storage";
    private final String userFilePath= storageDir + "/users.json";
    private final String projectsDir= storageDir +"/projects";

    private final StorageManager storage;
    private final ObjectMapper mapper;

    //comando usato dal client per comunicare la fine della comunicazione
    private final String EXITcmd="exit";

    private static int CHATport=2000;

    //crea server
    public ServerMain() throws IOException{
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
        System.out.printf("Username: %s \nPassword: %s \n", username, password);

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
            sChannel.socket().bind(new InetSocketAddress(TCPport));
            sChannel.configureBlocking(false);
            Selector sel=Selector.open();
            sChannel.register(sel, SelectionKey.OP_ACCEPT);
            System.out.printf("Server in attesa di connessioni sulla porta %d\n", TCPport);
            while(true){
                iterateKeys(sel);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void iterateKeys(Selector sel) throws IOException{
        if(sel.select()==0)
            return;
        Set<SelectionKey> selKeys= sel.selectedKeys(); //insieme delle chiavi dei canali pronti
        Iterator<SelectionKey> iter=selKeys.iterator(); //iteratore insieme selKeys
        while(iter.hasNext()){
            SelectionKey k=iter.next();
            iter.remove();
            if(k.isAcceptable()){   //caso: ACCETTABLE
                //accetta la nuova connesione creando un SocketChannel per la comunicazione con il client che l'ha richiesta
                ServerSocketChannel server=(ServerSocketChannel) k.channel();
                SocketChannel cChannel= server.accept(); //canale comunicazione
                cChannel.configureBlocking(false);
                System.out.println("Server: accettata nuova connessione dal client "+ cChannel.getRemoteAddress());
                this.registerRead(sel, cChannel);
            } else if(k.isWritable()){   //caso: WRITABLE
                this.answerClient(sel,k);
            } else if(k.isReadable()){   //caso: READABLE
                String cmd="";
                try{
                    cmd=this.readClientMsg(k);
                    executeCommand(cmd, k);
                }catch(IOException e){
                    System.out.println("Disconnessione utente");
                    cancelKey(k);
                    continue;
                }catch (ArrayIndexOutOfBoundsException e){
                    k.attach(new Esito(false, "Missing arguments"));
                    k.interestOps(SelectionKey.OP_WRITE);
                }catch (UserNotFoundException e){
                    k.attach(new Esito(false, "Utente non trovato"));
                }catch (ProjectNotFoundException e){
                    k.attach(new Esito(false, "Progetto non trovato"));
                }catch(IllegalArgumentException | MultipleLoginsException | UserAlreadyLoggedException e){
                    k.attach(new Esito(false, e.getMessage()));
                }catch(IllegalChangeStateException e){
                    k.attach(new Esito(false, "Cambiamento di stato non permesso"));
                }catch(CardNotFoundException e){
                    k.attach(new Esito(false, ("Card non trovata")));
                }
                if(!cmd.equals(EXITcmd))
                    k.interestOps(SelectionKey.OP_WRITE);
            }
        }
    }

    //richiede la cancellazione della registrazione della key dal canale e relativo selettore dopo aver chiuso il canale
    private void cancelKey(SelectionKey key) throws IOException{
        users.logout(key);
        key.channel().close();
        key.cancel();
    }

    /** 
    * registra interesse all'operazione di READ sul selettore
    * @param sel      selettore usato dal server
    * @param cChannel socket channel relativo al client
    */
    private void registerRead(Selector sel, SocketChannel cChannel) throws IOException{

        ByteBuffer buff=ByteBuffer.allocate(BUFFER_DIM); //creazione buffer
        //aggiunge il canale del client al selector con operazione OP_READ
        //e aggiunge l'array di bytebuffer[lenght, mesage] come attachment
        cChannel.register(sel, SelectionKey.OP_READ, buff);
    }

    //scrive il buffer sul canale del client
    private void answerClient(Selector sel, SelectionKey key)throws IOException{
        SocketChannel cChannel= (SocketChannel) key.channel();
        Esito response= (Esito) key.attachment();
        byte[] res= Utils.serialize(response);
        ByteBuffer bbEchoAnswer=ByteBuffer.wrap(res);
        cChannel.write(bbEchoAnswer);
        System.out.println("Server: "+ response.msg + " inviato al client: " + cChannel.getRemoteAddress());
        if(!bbEchoAnswer.hasRemaining()){
            bbEchoAnswer.clear();
            this.registerRead(sel, cChannel);
        }
    }

    private String readClientMsg(SelectionKey key) throws IOException{
        //accetta una nuova connessione creando un socket channel per la comunicazione con il client che la richiede
        SocketChannel cChannel= (SocketChannel) key.channel();
        //recupera l'array di bytebuffer
        ByteBuffer bb= (ByteBuffer) key.attachment();
        cChannel.read(bb);
        bb.flip();
        return new String(bb.array()).trim();
    }

    //ESECUZIONE DEI COMANDI
    private void executeCommand(String command, SelectionKey key) throws IOException,ArrayIndexOutOfBoundsException, UserNotFoundException,
    MultipleLoginsException, UserAlreadyLoggedException, ProjectNotFoundException, IllegalChangeStateException, IllegalArgumentException{
        String[] splittedCmd= command.split(" ");
        System.out.println("Comando richiesto: " + command);

        switch (splittedCmd[0].toLowerCase()){

            case "login":
                boolean flag=users.login(splittedCmd[1], splittedCmd[2], key); //indica se il login e' avvenuto con successo
                if(flag){
                    notifyUsers();
                    key.attach(new Esito(true, "Login avvenuto con successo"));
                } else{
                    key.attach(new Esito(false, "Password NON corretta"));
                }
                break;

            case "listprojects":
                if(users.isLogged(key)){
                    key.attach(new Esito(true, "Lista dei progetti:", projects.listProject(users.getUsernameByKey(key)).toArray(new String[0])));
                } else
                    key.attach(new Esito(false, "Utente non loggato"));
                break;

            case "createproject":
                if(users.isLogged(key)){
                    projects.addProject(splittedCmd[1]);
                    projects.addMember(splittedCmd[1], users.getUsernameByKey(key));
                    notifyUsers();
                    key.attach(new Esito(true, "Creato progetto: "+splittedCmd[1]));
                } else
                    key.attach(new Esito(false, "utente non loggato"));
                break;

            case "showmembers":
                if(users.isLogged(key)){
                    Project p=projects.getProjectByName(splittedCmd[1]);
                    if(!p.isMember(users.getUsernameByKey(key))){
                        key.attach(new Esito(false, "Non fai parte dei membri del progetto"));
                    } else{
                        String[] members= p.getMembers().toArray(new String[0]);
                        key.attach(new Esito(true, "Membri del progetto:", members));
                    }
                } else
                    key.attach(new Esito(false, "Utente non loggato"));
                break;

            case "addmember":
                if(users.isLogged(key)){
                    Project p= projects.getProjectByName(splittedCmd[1]);
                    if(!p.isMember(users.getUsernameByKey(key))){
                        key.attach(new Esito(false, "Non fai parte dei membri del progetto"));
                    } else{
                        User newMemb= users.getByUsername(splittedCmd[2]);
                        p.addMember(newMemb.getUsername());
                        projects.updateProjects();
                        key.attach(new Esito(true, "Aggiunto utente "+splittedCmd[2]+ " al progetto "+splittedCmd[1]));
                        //Notifica l'utente interessato
                        newMemb.notify(new Notification(users.getUsersList(), projects.getChatList(newMemb.getUsername())));
                    }
                } else
                    key.attach(new Esito(false, "Utente non loggato"));
                break;

            case "showcards":
                if((users.isLogged(key))){
                    Project p=projects.getProjectByName(splittedCmd[1]);
                    if(!p.isMember(users.getUsernameByKey(key))){
                        key.attach(new Esito(false, "Non fai parte dei membri del progetto"));
                    } else{
                        key.attach(new Esito(true, "Card del progetto: ", p.getCardStringList().toArray(new String[0])));
                    }
                } else
                    key.attach(new Esito(false, "Utente non loggato"));
                break;

            case "addcard":
                if(users.isLogged(key)){
                    Project p= projects.getProjectByName(splittedCmd[1]);
                    if(!p.isMember(users.getUsernameByKey(key))){
                        key.attach(new Esito(false, "Non fai parte dei membri del progetto"));
                    } else{
                        p.createCard(splittedCmd[2], splittedCmd[3]);
                        projects.updateProjects();
                        key.attach(new Esito(true, "Card "+splittedCmd[2]+ " creata e aggiunta al progetto: "+splittedCmd[1]));
                    }
                } else 
                    key.attach(new Esito(false, "Utente non loggato"));
                break;

            case "movecard":
                if(users.isLogged(key)){
                    Project p= projects.getProjectByName(splittedCmd[1]);
                    if(!p.isMember(users.getUsernameByKey(key))){
                        key.attach(new Esito(false, "Non fai parte dei membri del progetto"));
                    } else{
                       p.changeCardState(splittedCmd[2], splittedCmd[3], splittedCmd[4]);
                       projects.updateProjects();
                       key.attach(new Esito(true, "Cambio di stato della Card "+ splittedCmd[2]+" avvenuto con successo"));
                       ChatProcess.sendMsg(p.getIPMulticast(), CHATport, String.format("%s ha cambiato lo stato di %s da %s a %s", users.getUsernameByKey(key),splittedCmd[2],splittedCmd[3],splittedCmd[4]));
                    }
                } else
                    key.attach(new Esito(false, "Utente non loggato"));
                break;

            case "getcardhistory":
                if(users.isLogged(key)){
                    Project p= projects.getProjectByName(splittedCmd[1]);
                    if(!p.isMember(users.getUsernameByKey(key))){
                        key.attach(new Esito(false, "Non fai parte dei membri del progetto"));
                    } else{
                        key.attach(new Esito(true, "Cronologia card: ", p.getCardHistory(splittedCmd[2]).toArray(new String[0])));
                    }
                } else
                    key.attach(new Esito(false, "Utente non loggato"));
                break;

            case "cancelproject":
                if(users.isLogged(key)){
                    Project p= projects.getProjectByName(splittedCmd[1]);
                    if(!p.isMember(users.getUsernameByKey(key))){
                        key.attach(new Esito(false, "Non fai parte dei membri del progetto"));
                    } 
                    if(!p.isDone()){
                        key.attach(new Esito(false, "Non e' possibile eliminare un progetto non completato"));
                    } else{
                        projects.deleteProject(p);
                        notifyUsers();
                        key.attach(new Esito(true, "E' stato eliminato il progetto "+ splittedCmd[1]));
                    }
                } else
                    key.attach(new Esito(false, "Utente non loggato"));
                break;

            case EXITcmd:
                String utentelogout;
                utentelogout=users.getUsernameByKey(key);
                cancelKey(key);
                notifyUsers();
                System.out.println("Utente "+ utentelogout +" disconnesso.");
                return;

            case "":
                key.attach(new Esito(true));


            default:
                key.attach(new Esito(false, "Comando non trovato"));
                break;
        }
    }

    public static void main(String[] args){

        try{
            if (args.length == 1){
                TCPport=Integer.parseInt(args[0]);
            }
            if(args.length==2){
                TCPport = Integer.parseInt(args[0]);
                RMIport = Integer.parseInt(args[1]);
            }
            if(args.length==3){
                TCPport = Integer.parseInt(args[0]);
                RMIport = Integer.parseInt(args[1]);
                CHATport= Integer.parseInt(args[2]);
            }
        } catch (RuntimeException exc){
            exc.printStackTrace();
        }

        try{
            ServerMain server=new ServerMain();
            ServerInterface stub= (ServerInterface) UnicastRemoteObject.exportObject(server, RMIport);
            String name= "Server";
            LocateRegistry.createRegistry(RMIport);
            Registry reg=LocateRegistry.getRegistry(RMIport);
            reg.bind(name, stub);
            server.start();
        } catch (Exception e){
            e.printStackTrace();
        }
}
}
