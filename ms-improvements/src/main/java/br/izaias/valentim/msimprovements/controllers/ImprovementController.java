package br.izaias.valentim.msimprovements.controllers;

import br.izaias.valentim.msimprovements.entities.Improvement;
import br.izaias.valentim.msimprovements.services.ImprovementService;
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

    public ImprovementController(ImprovementService service) {
        this.service = service;
    }

    @GetMapping(value = "status")
    public String getStatus() {
        return "improvements - ok";
    }

    @PostMapping
    public ResponseEntity<Improvement> createImprovement(@RequestBody Improvement improvementToSave) {
        service.createImprovement(improvementToSave);

        URI readerLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .query("id={id}")
                .buildAndExpand(improvementToSave.getId())
                .toUri();

        return ResponseEntity.created(readerLocation).build();
    }

    @GetMapping(value = "{idImprovement}")
    public ResponseEntity<Improvement> getImprovementById(@PathVariable Long idImprovement) {
        Optional<Improvement> improvement = service.getImprovementsById(idImprovement);
        if (improvement.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(improvement.get());
    }

    @GetMapping
    public ResponseEntity<List<Improvement>> getAllImprovements() {
        return ResponseEntity.ok(service.getAllImprovements());
    }

    @DeleteMapping(value = "{idImprovement}")
    public ResponseEntity deleteImprovement(@PathVariable Long idImprovement) {
        if (service.deleteImprovement(idImprovement))
            return ResponseEntity.noContent().build();
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping(value = "{idImprovement}")
    public ResponseEntity<Improvement> updateImprovement(
            @PathVariable Long idImprovement,
            @RequestParam String newName,
            @RequestParam String newDescription) {
        if (newName == null && newDescription == null) {
            return ResponseEntity.badRequest().build();
        }
        Improvement toUpdate = service.updateImprovement(
                idImprovement, newName, newDescription);
        return ResponseEntity.noContent().build();
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

