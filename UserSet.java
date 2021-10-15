import Exceptions.MultipleLoginsException;
import Exceptions.UserAlreadyLoggedException;
import Exceptions.UserNotFoundException;
import Utility.Notification;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Contiene la lista di tutti gli utenti e permette la gestione di essi (login, logout etc)
public class UserSet {

    private final ArrayList<User> users;
    private final StorageManager storage;
    private final Map<SelectionKey, User> userKeySet;
    
    UserSet(StorageManager storage) throws IOException{
        this.storage=storage;
        this.users=storage.restoreUsers();
        this.userKeySet= new HashMap<>();
    }

    UserSet(ArrayList<User> users, StorageManager storage){
        this.storage=storage;
        this.users= users;
        this.userKeySet=new HashMap<>();
    }
    
    //restituisce un utente a partire dall'username
    public synchronized User getByUsername(String username) throws UserNotFoundException{
        for (User usr :users){
            if(usr.getUsername().equals(username)){
                return usr;
            }
        }
        throw new UserNotFoundException("Nessun utente con questo username trovato:"+ username);
    }

    //registra un nuovo utente nel sistema e restituisce TRUE se la registrazione avviene con successo, FALSE altrimenti
    public synchronized boolean register(String username, String psw) throws IOException{
        for(User usr: users){
            System.out.println(usr.getUsername());
            if(usr.getUsername().equals(username)){ return false; }
        }
        users.add(new User(username, psw));
        storage.updateUsers(users);
        return true;
    }

    //restituisce la lista di tutti gli utenti con relativo stato
    public Map<String, Boolean> getUsersList() {
        Map<String, Boolean> usersList = new HashMap<>();
        for (User usr : users) {
            usersList.put(usr.getUsername(), usr.isOnline());
        }
        return usersList;
    }

    //notifica tutti gli utenti 
    public synchronized void notifyAll(ProjectSet projects){
        for(User usr:users){
            try{
                usr.notify(new Notification(getUsersList(),projects.getChatList(usr.getUsername())));
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }

    //effettua login
    public boolean login(String username, String password, SelectionKey userKey) throws UserNotFoundException, UserAlreadyLoggedException, MultipleLoginsException{
        User usr=getByUsername(username);
        if(userKeySet.containsKey(userKey)) throw new UserAlreadyLoggedException();
        if(usr.isOnline()) throw new MultipleLoginsException();
        if(usr.login(password)){
            userKeySet.put(userKey,usr);
            return true;
        }

        return false;
    }

    //effettua logout
    public void logout(SelectionKey key){
        if(!userKeySet.containsKey(key))
            return;
        User usr=userKeySet.get(key);
        usr.setOnline(false);
        usr.setClient(null);
        userKeySet.remove(key);
    }

    public void setClient(String username, NotifyEventInterface clientInterface) throws UserNotFoundException{
        User user= getByUsername(username);
        user.setClient(clientInterface);
    }

    public boolean isLogged(SelectionKey userKey){
        return userKeySet.containsKey(userKey);
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public String getUsernameByKey(SelectionKey key){
        return userKeySet.get(key).getUsername();
    }

    public String[] getOnlineUsersList() {
        String[] usersList = new String[userKeySet.size()];
        for (int i = 0; i < users.size(); i++) {
            User usr = users.get(i);
            if(usr.isOnline())
                usersList[i] = usr.getUsername();
        }
        return usersList;
    }
}
