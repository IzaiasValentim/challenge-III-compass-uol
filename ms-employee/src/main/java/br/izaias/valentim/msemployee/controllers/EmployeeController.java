package br.izaias.valentim.msemployee.controllers;

import br.izaias.valentim.msemployee.entities.Employee;
import br.izaias.valentim.msemployee.services.EmployeeService;
import br.izaias.valentim.msemployee.services.exceptions.EmployeeNotFoundException;
import br.izaias.valentim.msemployee.services.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/employees")
public class EmployeeController {
    private final EmployeeService service;

    @Autowired
    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping(value = "status")
    public String getStatus() {
        return "ms-employee - ok";
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(service.gelAllEmployees());
    }

    @PostMapping
    public ResponseEntity saveEmployee(@RequestBody Employee employeeToSave) {

        try {

            return service.create(employeeToSave);

        } catch (PersistenceException persistenceException) {

            return ResponseEntity.badRequest().body(persistenceException.getMessage());

        }
    }

    @GetMapping(value = "{cpf}")
    public ResponseEntity<String> validateEmployeeAndCPF(@PathVariable String cpf) {
        try {

            service.getByCpf(cpf);
            return ResponseEntity.ok("can vote");

        } catch (EmployeeNotFoundException notFoundException) {

            return ResponseEntity.notFound().build();

        } catch (ResponseStatusException rEx) {

            return ResponseEntity.status(rEx.getStatusCode()).body(rEx.getMessage());

        }
    }

    @PatchMapping(value = "{cpf}")
    public ResponseEntity updateEmployee(
            @PathVariable String cpf,
            @RequestParam String newName,
            @RequestParam String newUserRole) {
        try {
            if (newName.isEmpty() && newUserRole.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            return service.updateEmployee(cpf, newName, newUserRole);

        } catch (ResponseStatusException rEx) {
            return ResponseEntity.status(rEx.getStatusCode()).body(rEx.getMessage());
        } catch (EmployeeNotFoundException employeeNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "{cpf}")
    public ResponseEntity deleteEmployee(@PathVariable String cpf) {
        try {
            return service.deleteEmployee(cpf);
        } catch (ResponseStatusException rEx) {
            return ResponseEntity.status(rEx.getStatusCode()).body(rEx.getMessage());
        }
    }
}
