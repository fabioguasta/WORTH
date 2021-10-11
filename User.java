import Utility.Notification;
import Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.rmi.RemoteException;
import java.security.SecureRandom;

//Rappresenta un utente e contiene username, password, stato ed interfaccia remota del client
@JsonPropertyOrder({ "username", "password" })
public class User {
    private String username;
    private String password;
    private String saltKey;
    private NotifyEventInterface clientInterface;
    private boolean on; //rappresenta lo stato dell'utente

    User(){}

    User(String username, String password)
}
