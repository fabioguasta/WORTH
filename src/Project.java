import Utility.Utils;
import java.util.ArrayList;
import java.util.List;
import Exceptions.CardNotFoundException;
import Exceptions.IllegalChangeStateException;
/* I Project rappresentano i singoli progetti e contiene una lista di oggetti Card. 
Fornisce i metodi per la gestione delle Card, aggiunta di membri al progetto e partecipazione chat */

public class Project {
    private final String name;
    private final List<Card> cards;

    private final List<String> cardsTodo;
    private final List<String> cardsInprogress;
    private final List<String> cardsToberevised;
    private final List<String> cardsDone;

    private List<String> members;
    private String IPMulticast;

    
    public Project(String Name){
        this.name=Name;
        this.cards=new ArrayList<>();
        this.cardsTodo= new ArrayList<>();
        this.cardsInprogress= new ArrayList<>();
        this.cardsToberevised= new ArrayList<>();
        this.cardsDone= new ArrayList<>();
        this.IPMulticast= Utils.randomMulticastipv4();
        this.members= new ArrayList<>();
    }

    public Project(String Name, ArrayList<Project> projects){
        this.name=Name;
        this.cards=new ArrayList<>();
        this.cardsTodo= new ArrayList<>();
        this.cardsInprogress= new ArrayList<>();
        this.cardsToberevised= new ArrayList<>();
        this.cardsDone= new ArrayList<>();
        this.members= new ArrayList<>();

        boolean flag=true;
        String ip;
        while(flag){
            ip=Utils.randomMulticastipv4();
            boolean flag2=true;
            while(flag2==true){
                for(Project p : projects){
                    ip=Utils.randomMulticastipv4();
                    if (ip.equals(p.getIPMulticast()))
                        flag2=false;
                }
                //se nessun progetto ha quell'indirizzo IP allora lo assegno, altrimenti ne creo uno nuovo e riprovo
                if(flag2==true){
                    this.IPMulticast=ip;
                    flag=false;
                }
            }
        }
    }

    public String getName(){
        return this.name;
    }
    
    //Aggiunge un membro al progetto
    public void addMember(String utente){
        if(this.members.contains(utente))
            throw new IllegalArgumentException("Utente gia presente");
        members.add(utente);
    }

    public boolean isMember(String utente){
        return members.contains(utente);
    }

    public List<String> getMembers(){
        return this.members;
    }

    public void setMembers(List<String> memb){
        this.members=memb;
    }

    //Restituisce Ip Multicast
    public String getIPMulticast(){
        return IPMulticast;
    }

    //Aggiunge una Card al progetto
    public void addCard(Card card){
        for(Card c:cards){
            if(c.getName().equals(card.getName())) throw new IllegalArgumentException("Nome della card gia esistente");
        }

        cards.add(card);
        switch(card.getCurrentState()){
            case "TODO":
                cardsTodo.add(card.getName());
                break;
            case "INPROGRESS":
                cardsInprogress.add(card.getName());
                break;
            case "TOBEREVISED":
                cardsToberevised.add(card.getName());
                break;
            case "DONE":
                cardsDone.add(card.getName());
                break;
        }
    }

    //Crea una nuova Card e la aggiunge al progetto
    public void createCard(String name, String description){
        for(Card c : cards) {
            if(c.getName().equals(name)) throw new IllegalArgumentException();
        }
        addCard(new Card(name, description));
    }

    //Restituisce una Card a partire dal nome (univoco)
    public Card getCard (String name) throws CardNotFoundException{
        for(Card card : cards){
            if(card.getName().equals(name))
            return card;
        }
        
        throw new CardNotFoundException();
    }

    //Cambia gli stati di una Card
    public void changeCardState(String name, String oldStatus, String newStatus){
        Card card=getCard(name);

        if(!card.getCurrentState().equals(oldStatus.toUpperCase())) throw new IllegalArgumentException("Lo stato di partenza non e' quello indicato");

        switch(oldStatus.toUpperCase()){
            case "TODO":
                if(!newStatus.equalsIgnoreCase("INPROGRESS")){throw new IllegalChangeStateException(oldStatus,newStatus);
                } 
                else{
                    card.changeState("INPROGRESS");
                    cardsTodo.remove(name);
                    cardsInprogress.add(name);
                    break;
                }
            case "INPROGRESS":
                if(!newStatus.equalsIgnoreCase("DONE") && !newStatus.equalsIgnoreCase("TOBEREVISED")){
                    throw new IllegalChangeStateException(oldStatus, newStatus);
                } else {
                    card.changeState(newStatus.toUpperCase());
                    if(newStatus.equalsIgnoreCase("TOBEREVISED")){
                        cardsInprogress.remove(name);
                        cardsToberevised.add(name);
                    } else{
                        cardsInprogress.remove(name);
                        cardsDone.add(name);
                    }
                    break;
                }
            case "TOBEREVISED":
                if(!newStatus.equalsIgnoreCase("DONE") && !newStatus.equalsIgnoreCase("INPROGRESS")){
                    throw new IllegalChangeStateException(oldStatus, newStatus);
                } else{
                    card.changeState(newStatus.toUpperCase());
                    if(newStatus.equalsIgnoreCase("INPROGRESS")){
                        cardsToberevised.remove(name);
                        cardsInprogress.add(name);
                    } else{
                        cardsToberevised.remove(name);
                        cardsDone.add(name);
                    }
                    break;
                }
            default: throw new IllegalArgumentException("Cambio di stato non riconosciuto");
        }
    }

    public List<Card> getAllCards(){
        return cards;
    }

    public List<String> getCardHistory(String Name){
        return getCard(Name).getCardHistory();
    }

    public List<String> getCardInformation(String Name){
        return getCard(Name).getInformation();
    }

    //Restituisce una lista contenente i nomi di tutte le Card
    public List<String> getCardStringList(){
        List<String> l =new ArrayList<>();
        for(Card card :cards){
            l.add(card.getName()+ " " +card.getCurrentState());
        }
        return l;
    }
    //Restituisce TRUE se il progetto e' stato completato, FALSE altrimenti
    public boolean isDone(){
        return cardsDone.size() == cards.size();
    }
}
