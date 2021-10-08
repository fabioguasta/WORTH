package Exceptions;

public class IllegalChangeStateException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public IllegalChangeStateException()
    {
        super("Movimento della carta Illegale");
    }
    public IllegalChangeStateException(String old,String NEW)
    {
        super("Movimento della carta Illegale: "+old+" -> "+NEW);
    }
}
