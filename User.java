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

    User(String username, String password){
        this.username=username;
        on=false;
        SecureRandom rnd= new SecureRandom();
        byte[] salt= new byte[16];
        rnd.nextBytes(salt);

        this.saltKey=Utils.byteToBase64(salt);
        this.password=Utils.sha512(password, this.saltKey);
    }

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

    @JsonIgnore
    public boolean isOnline(){
        return on;
    }

    @JsonIgnore
    public void setOnline(boolean online){
        this.on=online;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public String getSaltKey(){
        return saltKey;
    }

    public void setPassword(String psw){
        this.password=psw;
    }

    public void setSaltKey(String saltKey){
        this.saltKey=saltKey;
    } 
}
