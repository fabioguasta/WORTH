import Exceptions.UserNotFoundException;
import Utility.Esito;
import java.rmi.*;

public interface ServerInterface extends Remote{

    //registrazione per la callback
    void registerForCallback(NotifyEventInterface ClientInterface, String username) throws RemoteException, UserNotFoundException;

    //cancella registrazione per la callback
    void unregisterForCallback(String username) throws RemoteException, UserNotFoundException;

    Esito registrazione(String username, String psswrd) throws RemoteException;
}
