package br.izaias.valentim.msemployee.controllers;

import br.izaias.valentim.msemployee.entities.Employee;
import br.izaias.valentim.msemployee.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "employees")
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
        return service.create(employeeToSave);
    }

    @GetMapping(value = "{cpf}")
    public ResponseEntity<String> validateEmployeeAndCPF(@PathVariable String cpf) {
        Employee employeeFinds = service.getByCpf(cpf);

        if (employeeFinds == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("can vote");
    }

    @PatchMapping(value = "{cpf}")
    public ResponseEntity updateEmployee(
            @PathVariable String cpf,
            @RequestParam String newName,
            @RequestParam String newUserRole) {
        if (newName.isEmpty() || newUserRole.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return service.updateEmployee(cpf, newName, newUserRole);
    }

    @DeleteMapping(value = "{cpf}")
    public ResponseEntity deleteEmployee(@PathVariable String cpf) {
        if (service.deleteEmployee(cpf)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
