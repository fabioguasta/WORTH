import Exceptions.CardNotFoundException;
import Exceptions.IllegalChangeStateException;
import Exceptions.ProjectNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*ProjectSet contiene la lista di tutti i progetti e consente di creare e/o eliminare progetti e restituire tutti i progetti
di cui fa parte un determinato utente */
public class ProjectSet {
    
    private final ArrayList<Project> projects;
    private final StorageManager storage;

    ProjectSet (StorageManager storage) throws IOException{
        this.storage= storage;
        this.projects= storage.restoreProjects();
    }

    //restituisce il progetto a partire dal nome
    public Project getProjectByName(String name) throws ProjectNotFoundException{
        for(Project p :projects){
            if(p.getName().equals(name)){
                return p;
            }
        }
        throw new ProjectNotFoundException();
    }

    //aggiunge un progetto 
    public void addProject(String name) throws IOException, IllegalArgumentException{

        for(Project p: projects){
            if(p.getName().equals(name)){
                throw new IllegalArgumentException("Progetto con nome scelto gia presente");
            }
        }
        Project newProj=new Project(name, projects);
        projects.add(newProj);
        storage.updateProjects(projects);
        
    }

    //elimina un progetto a partire dal nome
    public void deleteProject(String name) throws IOException{
        Project p=getProjectByName(name);
        projects.remove(p);
        storage.updateProjects(projects);
    }

    //elimina un progetto prendendo come argomento un oggetto di tipo Project
    public void deleteProject(Project project)throws IOException{
        projects.remove(project);
        storage.updateProjects(projects);
    }

    public void createCard(String pName, String cName, String desc)throws IOException{
        getProjectByName(pName).createCard(cName, desc);
    }
    
    public List<Card> getCards(String pName) {//restituisce card di un progetto
        return getProjectByName(pName).getAllCards();
    }

    public boolean isDone(String pName) {
        return getProjectByName(pName).isDone();
    }

    public List<String> getCardHistory(String pName, String cardName) {
        return getProjectByName(pName).getCardHistory(cardName);
    }

    public List<String> getCardInfo(String pName, String cardName) {
        return getProjectByName(pName).getCardInformation(cardName);
    }

    //aggiunge un membro ad un progetto
    public void addMember(String pName, String user) throws IOException{
        Project project=getProjectByName(pName);
        project.addMember(user);
        storage.updateProjects(projects);
    }

    public boolean isMember(String pName, String user){
        return getProjectByName(pName).isMember(user);
    }
    
    //cambia gli stati di una card
    public void changeCardState(String pName, String cName, String oldStatus, String newStatus) throws CardNotFoundException, IllegalArgumentException,IllegalChangeStateException,IOException{
        getProjectByName(pName).changeCardState(cName, oldStatus, newStatus);
        storage.updateProjects(projects);
    }

    //restituisce la lista di tutte le card di un progetto
    public List<String> getCardList(String pName){
        return getProjectByName(pName).getCardStringList();
    }

    //restituisce la lista di tutti i progetti di cui fa parte un utente
    public List<String> listProject(String user){
        List<String> pList= new ArrayList<>();
        for(Project p:projects){
            if(p.isMember(user)){
                pList.add(p.getName());
            }
        }
        return pList;
    }

    public void updateProjects() throws IOException {
        storage.updateProjects(projects);
    }

    public Map<String,String> getChatList(String user){
        Map<String,String> chatList= new HashMap<>();
        for (Project p: projects){
            if(p.isMember(user)){
                chatList.put(p.getName(), p.getIPMulticast());
            }
        }
        return chatList;
    }
}

