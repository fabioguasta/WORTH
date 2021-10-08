import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/* Le Card rappresentano i compiti da svolgere per portare a termine un determinato progetto univoco.
 Queste sono identificate da nome, descrizione, stato attuale e cronologia di tutti gli stati. */
 
public class Card{
    public enum cardStatus {TODO, INPROGRESS, TOBEREVISED, DONE}
    private String name;
    private String description;
    private cardStatus currenStatus;
    private List<cardStatus> cardHistory;

    public Card (String name, String description){
        this.name=name;
        this.description=description;
        this.currentState=cardStatus.TODO;
        this.cardHistory=add(cardStatus.TODO);
    }

    public Card(){}

    //restituiscono e settano la cronologia degli stati della Card
    public List<String> getCardHistory(){
        List<String> history= new ArrayList<>();
        for (cardStatus status : cardHistory)
            history.add(status.name());

        return history;
    }

    public void setCardHistory(List<cardStatus> history){
        cardHistory=history;
    }

    //restituiscono e settano il nome della Card
    public String getName(){
        return this.name;
    }

    public void setName(String Name){
        this.name=Name;
    }

    //restituiscono e settano lo stato attuale della Card
    public String getcurrentState()
        return this.currentState.name();
    }

    public void setcurrentState(String currentState){
        this.currentState=cardStatus.valueOf(currentState.toUpperCase());
    }

    //restituiscono e settano la descrizione della Card
    public String getDescription(){
        return this.description;
    }

    public void setDescription(String desc){
        this.description=desc;
    }

    //restituisce tutte le informazioni relative alla Card
    @JsonIgnore 
    public List<String> getInformation(){ 
        List<String> info= new ArrayList<>();
        info.add(this.name);
        info.add(this.description);
        info.add(this.currentState);

        return info;
    }

    //cambia lo stato attuale della Card e lo aggiunge alla cronologia degli stati
    @JsonIgnore
    public void changeState(String status){
        this.currentState= cardStatus.valueOf(status.toUpperCase());
        this.cardHistory.add(currentState);
    }
}