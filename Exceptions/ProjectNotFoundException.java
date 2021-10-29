package Exceptions;

public class ProjectNotFoundException extends RuntimeException{
    
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
