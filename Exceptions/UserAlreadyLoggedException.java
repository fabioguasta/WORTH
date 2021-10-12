package Exceptions;

public class UserAlreadyLoggedException extends Exception{

    public UserAlreadyLoggedException() {
        super("Utente già loggato");
    }

    public UserAlreadyLoggedException(String message) {
        super(message);
    }
}
