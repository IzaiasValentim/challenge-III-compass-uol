package br.izaias.valentim.msimprovements.services.exceptions;

public class PersistenceException extends RuntimeException{
    public PersistenceException(String message){
        super(message);
    }
    public PersistenceException(String message, Throwable cause){
        super(message,cause);
    }

}
