package br.izaias.valentim.msemployee.utils;

import br.com.caelum.stella.validation.CPFValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CpfValidator {
    CPFValidator cpfValidator = new CPFValidator();

    public CpfValidator() {
    }

    public Boolean validateCpf(String cpf) {
        try {
            cpfValidator.assertValid(cpf);
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
}
