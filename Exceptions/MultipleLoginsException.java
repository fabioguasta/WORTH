package Exceptions;

public class MultipleLoginsException extends Exception{
    
    public MultipleLoginsException() {
        super("Sei già loggato con un altro host");
    }
    public MultipleLoginsException(String message) {
        super(message);
    }
}
