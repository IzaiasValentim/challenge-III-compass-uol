package br.izaias.valentim.msemployee.services.exceptions;

public class InvalidCpfException extends RuntimeException{
    public InvalidCpfException(String message){
        super(message);
    }
    public InvalidCpfException(String message, Throwable cause){
        super(message,cause);
    }
}
