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
    private final Map<SelectionKey, User> userKeys;
    
    UserSet(StorageManager storage) throws IOException{
        this.storage=storage;
        this.users=storage.restoreUsers();
        this.userKeys= new HashMap<>();
    }

    UserSet(ArrayList<User> users, StorageManager storage){
        this.storage=storage;
        this.users= users;
        this.userKeys=new HashMap<>();
    }
}
