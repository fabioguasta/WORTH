import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import Utility.Utils;

public class StorageManager {
    
    private final ObjectMapper mapper;
    private final String storageDirectory;  //path cartella storage
    private final String userFilePath;      //path del file contenente la lista di tutti gli utenti dell'applicazione
    private final String projectsDirectory; //path cartella contenente i progetti
    private final String membersFile="member.json";

    StorageManager(String storageDir, String filePath, String projectsDir){
        this.storageDirectory=storageDir;
        this.userFilePath=filePath;
        this.projectsDirectory=projectsDir;
        this.mapper=new ObjectMapper();
        
        //aggiunto rispetto alla consegna vecchio progetto (non permetteva avvio server se la cartella storage non era già esistente)
        File storage = new File(storageDir);
        if (!storage.exists()) {
            storage.mkdir();
        }
    }

    //ricrea l'ArrayList contenente tutti gli utenti presenti in memoria
    public ArrayList<User> restoreUsers() throws IOException{
        File file= new File(userFilePath);
        if(file.createNewFile()){
            mapper.writeValue(file, new ArrayList<User>());
        }

        return new ArrayList<>(Arrays.asList(mapper.readValue(file, User[].class)));
    }

    //ricrea l'ArrayList contenente tutti i progetti presenti in memoria
    public ArrayList<Project> restoreProjects() throws IOException{
        ArrayList<Project> projectSet= new ArrayList<>();
        File dir= new File(projectsDirectory);  

        if(!dir.exists())   //controllo che la cartella esista altrimenti la creo
            dir.mkdir();

        for(File projectDir:dir.listFiles()){
            if(projectDir.isDirectory()){
                Project project= new Project(projectDir.getName()); //creo nuovo progetto contenente le card presenti nella cartella
                for(File cardFile: projectDir.listFiles()){
                    if(cardFile.getName().equals(membersFile)){ //prende i membri dal file json e li aggiunge al progetto
                        project.setMembers(new ArrayList<>(Arrays.asList(mapper.readValue(cardFile, String[].class))));
                        continue;
                    }
                    project.addCard(mapper.readValue(cardFile, Card.class));
                }
                projectSet.add(project);
            }
        }
        
        return projectSet;
    }

    //aggiorna la cartella dei progetti (solitamente usato quando ne vengono aggiunti o eliminati)
    public void updateProjects(ArrayList<Project> projects) throws IOException{
        //elimina la cartella e la ricrea vuota
        File projectsDir= new File(projectsDirectory);
        Utils.deleteDir(projectsDir);
        projectsDir.mkdir();

        //salva i progetti con le relative card nella nuova cartella
        for(Project project:projects){
            String projectPath= projectsDirectory + "/" + project.getName();
            File projectDir= new File(projectPath);
            if(!projectDir.exists()){
                projectDir.mkdir();
            }
            for(Card card: project.getAllCards()){
                File cardFile= new File(projectPath + "/" + card.getName() + "-card.json");
                cardFile.createNewFile();
                mapper.writeValue(cardFile, card);
            }

            File members = new File(projectPath + "/" + membersFile);
            members.createNewFile();
            mapper.writeValue(members, new ArrayList<>(project.getMembers()));
        }
    }

    //aggiorna il file contenente gli utenti (solitamente usato quando ne vengono aggiunti o eliminati)
    public void updateUsers(ArrayList<User> users) throws IOException{
        File file= new File(userFilePath);
        file.createNewFile();
        mapper.writeValue(file, users);
    }

}
