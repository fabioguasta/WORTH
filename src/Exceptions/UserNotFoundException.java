package Exceptions;

public class UserNotFoundException extends Exception{
    
    public UserNotFoundException() {
        super("Utente non trovato");
    }
    public UserNotFoundException(String message) {
        super(message);
    }
}
