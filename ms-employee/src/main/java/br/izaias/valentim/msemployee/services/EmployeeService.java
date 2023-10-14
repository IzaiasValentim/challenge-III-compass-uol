package br.izaias.valentim.msemployee.services;

import br.izaias.valentim.msemployee.entities.Employee;
import br.izaias.valentim.msemployee.repositories.EmployeeJpaRepository;
import br.izaias.valentim.msemployee.utils.CpfValidator;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.xml.crypto.Data;
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
            repository.save(employeeToSave);
            URI readerLocation = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .query("cpf={cpf}")
                    .buildAndExpand(employeeToSave.getCpf())
                    .toUri();
            return ResponseEntity.created(readerLocation).build();

        } catch (ResponseStatusException rEx) {
            return ResponseEntity.status(rEx.getStatusCode()).body(rEx.getMessage());
        }catch (DataAccessException exData){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("cpf already registered");
        }
    }

    public List<Employee> gelAllEmployees() {
        return repository.findAll();
    }

    public Employee getByCpf(String cpf) {
        if (cpfValidator.validateCpf(cpf))
            return repository.getEmployeeByCpf(cpf);
        return null;
    }
    @Transactional
    public ResponseEntity updateEmployee(String cpf, String newName, String newUserRole) {

        if (cpfValidator.validateCpf(cpf)) {
            Employee employeeOnDB = repository.getEmployeeByCpf(cpf);

            if (employeeOnDB == null) {
                return ResponseEntity.notFound().build();
            }
            employeeOnDB.setName(newName);
            employeeOnDB.setUserRole(newUserRole);
            repository.save(employeeOnDB);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID CPF");
    }

    public Boolean deleteEmployee(String cpf) {
        if (cpfValidator.validateCpf(cpf)) {
            Employee consultEmployee = repository.getEmployeeByCpf(cpf);
            if (consultEmployee != null) {
                repository.delete(consultEmployee);
                return true;
            }
        }
        return false;
    }

}
