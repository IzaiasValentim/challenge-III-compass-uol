package br.izaias.valentim.msemployee.services;

import br.izaias.valentim.msemployee.entities.Employee;
import br.izaias.valentim.msemployee.repositories.EmployeeJpaRepository;
import br.izaias.valentim.msemployee.services.exceptions.EmployeeNotFoundException;
import br.izaias.valentim.msemployee.services.exceptions.InvalidCpfException;
import br.izaias.valentim.msemployee.services.exceptions.PersistenceException;
import br.izaias.valentim.msemployee.utils.CpfValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeJpaRepository repository;
    private final CpfValidator cpfValidator;

    @Autowired
    public EmployeeService(EmployeeJpaRepository repository, CpfValidator cpfValidator) {
        this.repository = repository;
        this.cpfValidator = cpfValidator;
    }

    @Transactional
    public ResponseEntity create(Employee employeeToSave) {
        try {
            cpfValidator.validateCpf(employeeToSave.getCpf());

            if (repository.getEmployeeByCpf(employeeToSave.getCpf()) != null) {
                throw new PersistenceException("CPF ALREADY REGISTERED IN THE SYSTEM");
            }
            repository.save(employeeToSave);
            URI readerLocation = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .query("cpf={cpf}")
                    .buildAndExpand(employeeToSave.getCpf())
                    .toUri();
            return ResponseEntity.created(readerLocation).build();

        } catch (InvalidCpfException invalidCpf) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidCpf.getMessage());
        } catch (DataAccessException exData) {
            throw new PersistenceException("ERROR WHEN CREATING EMPLOYEE: " + exData.getMessage());
        }
    }

    public List<Employee> gelAllEmployees() {
        return repository.findAll();
    }

    public ResponseEntity getByCpf(String cpf) {
        try {
            cpfValidator.validateCpf(cpf);

            Employee employeeFinds = repository.getEmployeeByCpf(cpf);

            if (employeeFinds == null)
                throw new EmployeeNotFoundException("EMPLOYEE NOT FOUND");

            return ResponseEntity.ok(employeeFinds);

        } catch (InvalidCpfException invalidCpf) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalidCpf.getMessage());
        }
    }

    @Transactional
    public ResponseEntity updateEmployee(String cpf, String newName, String newUserRole) {

        try {
            cpfValidator.validateCpf(cpf);

            Employee employeeOnDB = repository.getEmployeeByCpf(cpf);
            if (employeeOnDB == null)
                throw new EmployeeNotFoundException("EMPLOYEE NOT FOUND");

            if (!newName.isEmpty())
                employeeOnDB.setName(newName);

            if (!newUserRole.isEmpty())
                employeeOnDB.setUserRole(newUserRole);

            repository.save(employeeOnDB);

            return ResponseEntity.noContent().build();

        } catch (InvalidCpfException invalidCpf) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalidCpf.getMessage());

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    dataIntegrityViolationException.getMessage());
        }

    }

    public ResponseEntity deleteEmployee(String cpf) {
        try {
            cpfValidator.validateCpf(cpf);

            Employee consultEmployee = repository.getEmployeeByCpf(cpf);

            if (consultEmployee != null) {
                repository.delete(consultEmployee);
                return ResponseEntity.ok().build();
            }

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "EMPLOYEE NOT FOUND");

        } catch (InvalidCpfException invalidCpf) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalidCpf.getMessage());
        }
    }

}
