package Exceptions;

public class UserNotFoundException {
    
    public UserNotFoundException() {
        super("Utente non trovato");
    }
    public UserNotFoundException(String message) {
        super(message);
    }
}
