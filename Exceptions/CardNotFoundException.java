package Exceptions;

public class CardNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CardNotFoundException()
    {
        super("Carta non trovata");
    }
    public CardNotFoundException(String message)
    {
        super(message);
    }
}
