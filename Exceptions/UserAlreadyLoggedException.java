package Exceptions;

public class UserAlreadyLoggedException {

    public UserAlreadyLoggedException() {
        super("Utente già loggato");
    }

    public UserAlreadyLoggedException(String message) {
        super(message);
    }
}
