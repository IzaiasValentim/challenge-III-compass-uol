package br.izaias.valentim.msimprovements.controllers;

import br.izaias.valentim.msimprovements.entities.Improvement;
import br.izaias.valentim.msimprovements.feignClient.MsEmployeeFeign;
import br.izaias.valentim.msimprovements.services.ImprovementService;
import br.izaias.valentim.msimprovements.services.exceptions.ImprovementNotFoundException;
import br.izaias.valentim.msimprovements.services.exceptions.PersistenceException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "improvements")
public class ImprovementController {
    private final ImprovementService service;
    private final MsEmployeeFeign employeeFeign;

    public ImprovementController(ImprovementService service, MsEmployeeFeign employeeFeign) {
        this.service = service;
        this.employeeFeign = employeeFeign;
    }

    @GetMapping(value = "status")
    public String getStatus() {
        return "improvements - ok";
    }

    @PostMapping
    public ResponseEntity createImprovement(@RequestBody Improvement improvementToSave) {
        try {
            service.createImprovement(improvementToSave);

            URI readerLocation = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .query("id={id}")
                    .buildAndExpand(improvementToSave.getId())
                    .toUri();

            return ResponseEntity.created(readerLocation).build();
        } catch (ResponseStatusException rEx) {
            return ResponseEntity.status(rEx.getStatusCode()).body(rEx.getMessage());
        } catch (RuntimeException runtimeException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "{idImprovement}")
    public ResponseEntity<Improvement> getImprovementById(@PathVariable Long idImprovement) {
        try {
            Improvement improvement = service.getImprovementsById(idImprovement);
            return ResponseEntity.ok(improvement);
        } catch (ImprovementNotFoundException improvementNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Improvement>> getAllImprovements() {
        return ResponseEntity.ok(service.getAllImprovements());
    }

    @GetMapping(value = "employees/{cpf}")
    public ResponseEntity<String> validateEmployeeAndCPF(@PathVariable String cpf) {
        try {
            return employeeFeign.validateEmployeeAndCPF(cpf);
        } catch (FeignException.FeignClientException fEx) {
            if (fEx.status() == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity.status(fEx.status()).body("Employee not alowed to vote");
            } else if (fEx.status() == HttpStatus.BAD_REQUEST.value()) {
                return ResponseEntity.status(fEx.status()).body("INVALID - CPF");
            } else {
                return ResponseEntity.status(fEx.status()).body(fEx.getMessage());
            }
        }


    }

    @DeleteMapping(value = "{idImprovement}")
    public ResponseEntity deleteImprovement(@PathVariable Long idImprovement) {
        try {
            service.deleteImprovement(idImprovement);
            return ResponseEntity.noContent().build();

        } catch (ImprovementNotFoundException improvementNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping(value = "{idImprovement}")
    public ResponseEntity updateImprovement(
            @PathVariable Long idImprovement,
            @RequestParam String newName,
            @RequestParam String newDescription) {
        if (newName == null && newDescription == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Improvement toUpdate = service.updateImprovement(
                    idImprovement, newName, newDescription);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException rEx) {
            return ResponseEntity.status(rEx.getStatusCode()).body(rEx.getMessage());
        } catch (ImprovementNotFoundException improvementNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (PersistenceException persistenceException) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @PostMapping("{idImprovement}")
    public ResponseEntity vouteImprovement(@PathVariable Long idImprovement,
                                           @RequestParam Boolean voute,
                                           @RequestParam String cpf) {
        String vouteSend;
        if (voute) {
            vouteSend = "Approved";
        } else {
            vouteSend = "Rejected";
        }
        try {
            return service.registerVoute(idImprovement, vouteSend, cpf);
        } catch (ResponseStatusException rEx) {
            return ResponseEntity.status(rEx.getStatusCode()).body(rEx.getMessage());
        }
    }


}

