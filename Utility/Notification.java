package Utility;

import java.io.Serializable;
import java.util.Map;

//Classe che viene utilizzata per le RMI callback
public class Notification implements Serializable{
    final public Map<String, Boolean> utenti;
    final public Map<String, String> projectChatIPs;
    public String[] lst;

    public Notification (Map<String, Boolean> utenti, Map<String,String> projectChatIPs){
        this.utenti=utenti;
        this.projectChatIPs=projectChatIPs;
    }
    
}
