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
}
