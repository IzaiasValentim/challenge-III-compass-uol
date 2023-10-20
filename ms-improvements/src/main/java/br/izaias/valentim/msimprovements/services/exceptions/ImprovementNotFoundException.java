package br.izaias.valentim.msimprovements.services.exceptions;

public class ImprovementNotFoundException extends RuntimeException{
    public ImprovementNotFoundException(String message){
        super(message);
    }
}
