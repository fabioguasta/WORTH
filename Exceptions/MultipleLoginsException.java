package Exceptions;

public class MultipleLoginsException {
    
    public MultipleLoginsException() {
        super("Sei già loggato con un altro host");
    }
    public MultipleLoginsException(String message) {
        super(message);
    }
}
