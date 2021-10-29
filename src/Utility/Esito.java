package Utility;
import java.io.Serializable;

public class Esito implements Serializable{
    
    final public boolean success;
    final public String msg;
    public String[] list;

    public Esito(boolean response){
        this.success=response;
        this.msg= "";
    }

    public Esito(boolean response, String message){
        this.success=response;
        this.msg=message;
    }

    public Esito(boolean response, String message, String[] list){
        this.success= response;
        this.msg=message;
        this.list=list;
    }
    
}
