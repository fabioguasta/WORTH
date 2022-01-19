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
    private NotifyEventInterface clientInterface; //interfaccia remota del client
    private boolean on; //rappresenta lo stato dell'utente

    User(){}

    User(String username, String password){
        this.username=username;
        on=false;
        SecureRandom rnd= new SecureRandom();
        byte[] salt= new byte[16];
        rnd.nextBytes(salt);

        this.saltKey=Utils.byteToBase64(salt);
        this.password=Utils.sha512(password, this.saltKey);
    }

    //utilizza l’interfaccia remota del client per mandare una notifica tramite RMI callback
    @JsonIgnore
    public synchronized void notify(Notification notification) throws RemoteException{
        if(this.clientInterface==null) return;
        this.clientInterface.notifyEvent(notification);
    }

    @JsonIgnore
    public synchronized void setClient(NotifyEventInterface clientInterface){
        this.clientInterface=clientInterface;
    }

    @JsonIgnore
    public boolean login(String password){
        String hashPass= Utils.sha512(password, this.saltKey);
        if(this.password.equals(hashPass)){
            on=true;
            return true;
        }

        return false;
    }

    //restituiscono e settano lo stato di un utente
    @JsonIgnore
    public boolean isOnline(){
        return on;
    }

    @JsonIgnore
    public void setOnline(boolean online){
        this.on=online;
    }

    //restituiscono e settano l'username di un utente
    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username=username;
    }

    //restituiscono e settano la password di un utente
    public String getPassword(){
        return password;
    }

    public void setPassword(String psw){
        this.password=psw;
    }

    //restituiscono e settano la Key di un utente
    public String getSaltKey(){
        return saltKey;
    }

    public void setSaltKey(String saltKey){
        this.saltKey=saltKey;
    } 

}
