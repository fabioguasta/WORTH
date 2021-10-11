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
            mapper.writeValue(file, new ArrayList<User>();
        }

        return new ArrayList<>(Arrays.asList(mapper.readValue(file, User[].class)));
    }

}
