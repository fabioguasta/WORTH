import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import Utility.Utils;

public class StorageManager {
    
    private final ObjectMapper mapper;
    private final String storageDirectory;
    private final String userFilePath;
    private final String projectsDirectory;
    private final String membersFile="member.json";

    StorageManager(String storageDir, String filePath, String projectsDir){
        this.storageDirectory=storageDir;
        this.userFilePath=filePath;
        this.projectsDirectory=projectsDir;
        this.mapper=new ObjectMapper();
    }

    public ArrayList<User> restorUsers() throws IOException{
        File file= new File(userFilePath);
        if(file.createNewFile()){
            mapper.writeValue(file, new ArrayList<User>());
        }

        return new ArrayList<>(Arrays.asList(mapper.readValue(file, User[].class)));
    }

    public ArrayList<Project> restoreProjects() throws IOException{
        ArrayList<Project> projectSet= new ArrayList<>();
        File dir= new File(projectsDirectory);

        if(!dir.exists())
            dir.mkdir();
        for(File projectDir:dir.listFiles()){
            if(projectDir.isDirectory()){
                Project project= new Project(projectDir.getName());
                for(File cardFile: projectDir.listFiles()){
                    if(cardFile.getName().equals(membersFile)){
                        project.setMembers(new ArrayList<>(Arrays.asList(mapper.readValue(cardFile , String[].class))));
                        continue;
                    }
                    project.addCard(mapper.readValue(cardFile, Card.class));
                }
                projectSet.add(project);
            }
        }
        
        return projectSet;
    }

}
