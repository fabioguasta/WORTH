package Exceptions;

public class ProjectNotFoundException {
    
    private static final long serialVersionUID = 1L;

    public ProjectNotFoundException()
    {
        super("Progetto non trovato");
    }
    public ProjectNotFoundException(String message)
    {
        super(message);
    }
    
}
