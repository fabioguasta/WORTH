import Utils.Utils;

import java.Utils.ArrayList;
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
    private final String IPMulticast;

    public Project(String Name){
        this.name=Name;
        this.cards=new ArrayList<>();
        this.cardsTodo= new ArrayList<>();
        this.cardsInprogress= new ArrayList<>();
        this.cardsToberevised= new ArrayList<>();
        this.cardsDone= new ArrayList<>();
        this.IPMulticast= Utils.randomMulticastIpv4();
        this.members= new ArrayList<>();
    }

    public String getName(){
        return this.name;
    }
    
    //Aggiunge un membro al progetto
    public void addMember(String utente){
        if(this.member.contains(utente))
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
        switch(Card.getcurrentState()){
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
    public void createCard(String Name, String Description){
        for(Card c : cards) {
            if(c.getName().equals(Name)) throw new IllegalArgumentException();
        }
        addCard(new Card(Name, Description));
    }

    //Restituisce una Card a partire dal nome (univoco)
    public Card getCard (String Name) throws CardNotFoundException(){
        for(Card card : cards){
            if(card.getName().equals(Name))
            return card;
        }
        
        throw new CardNotFoundException();
    }

    //Cambia gli stati di una Card
    public void changeCardState(String Name, String oldStatus, String newStatus){
        Card card=getCard(Name);

        if(!card.getcurrentState().equals(oldStatus.toUpperCase())) throw new IllegalArgumentException("Lo stato di partenza non e' quello indicato");

        switch(oldStatus.toUpperCase()){
            case "TODO":
                if(!newStatus.equalsIgnoreCase("INPROGRESS")){throw new IllegalChangeStateException(oldStatus,newStatus);
                } 
                else{
                    card.changeState("INPROGRESS");
                    cardsTodo.remove(Name);
                    cardsInprogress.add(Name);
                    break;
                }
            case "INPROGRESS":
                if(!newStatus.equalsIgnoreCase("DONE") && !newStatus.equalsIgnoreCase("TOBEREVISED")){
                    throw new IllegalStateException(oldStatus, newStatus);
                } else {
                    card.changeState(newStatus.toUpperCase());
                    if(newStatus.equalsIgnoreCase("TOBEREVISED")){
                        cardsInprogress.remove(Name);
                        cardsToberevised.add(Name);
                    } else{
                        cardsInprogress.remove(Name);
                        cardsDone.add(Name);
                    }
                    break;
                }
            case "TOBEREVISED":
                if(!newStatus.equalsIgnoreCase("DONE")) && !newStatus.equalsIgnoreCase("INPROGRESS"){
                    throw new IllegalChangeStateException(oldStatus, newStatus);
                } else{
                    card.changeState(newStatus.toUpperCase());
                    if(newStatus.equalsIgnoreCase("INPROGRESS")){
                        cardsToberevised.remove(Name);
                        cardsInprogress.add(Name);
                    } else{
                        cardsToberevised.remove(Name);
                        cardsDone.add(Name);
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
        return getCard(Name);
    }

    public List<String> getCardInformation(String Name){
        return getCard(Name).getInformation();
    }

    //Restituisce una lista contenente i nomi di tutte le Card
    public List<String> getCardStringList(){
        List<String> l =new ArrayList<>();
        for(Card card :cards){
            l.add(card.getName()+ " " +card.getcurrentState());
        }
        return l;
    }
    //Restituisce TRUE se il progetto e' stato completato, FALSE altrimenti
    public boolean isDone(){
        return cardsDone.size() == cards.size();
    }
}
