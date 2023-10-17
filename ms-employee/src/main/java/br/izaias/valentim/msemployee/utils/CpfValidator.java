package br.izaias.valentim.msemployee.utils;

import br.com.caelum.stella.validation.CPFValidator;
import br.izaias.valentim.msemployee.services.exceptions.InvalidCpfException;
import org.springframework.stereotype.Component;

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
            throw new InvalidCpfException("INVALID CPF - : " + e.getMessage());
        }
    }
}
