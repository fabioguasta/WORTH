import Utility.Notification;
import java.rmi.*;

public interface NotifyEventInterface extends Remote{
    
    //Metodo utilizzato dal server per notificare un evento al client
    void notifyEvent(Notification notification) throws RemoteException;
}
