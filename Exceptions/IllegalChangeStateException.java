package Exceptions;

public class IllegalMoveException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public IllegalMoveException()
    {
        super("Movimento della carta Illegale");
    }
    public IllegalMoveException(String old,String NEW)
    {
        super("Movimento della carta Illegale: "+old+" -> "+NEW);
    }
}
