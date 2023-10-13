package br.izaias.valentim.msimprovements.services;

import br.izaias.valentim.msimprovements.entities.Improvement;
import br.izaias.valentim.msimprovements.entities.Voute;
import br.izaias.valentim.msimprovements.repositories.ImprovementRepository;
import br.izaias.valentim.msimprovements.services.exceptions.PersistenceException;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ImprovementService {
    private final ImprovementRepository repository;
    private final VouteService vouteService;

    @Autowired
    public ImprovementService(ImprovementRepository repository, VouteService vouteService) {
        this.repository = repository;
        this.vouteService = vouteService;
    }

    @Transactional
    public Improvement createImprovement(Improvement improvementToCreate) {
        try {
            improvementToCreate.setResult(Improvement.Result.IN_PROGRESS);
            return repository.save(improvementToCreate);

        } catch (DataAccessException exData) {
            throw new PersistenceException("Error at create improvement", exData);
        }
    }

    public List<Improvement> getAllImprovements() {
        return repository.findAll();
    }

    public Optional<Improvement> getImprovementsById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Improvement updateImprovement(Long idImprovament, String newName, String newDescription) {
        if (idImprovament == null || (newName.isEmpty() && newDescription.isEmpty())) {
            return null;
        }

        Optional<Improvement> toUpdate = repository.findById(idImprovament);
        if (toUpdate.isEmpty()) {
            return null;
        }
        if (!newName.isEmpty())
            toUpdate.get().setName(newName);
        if (!newDescription.isEmpty())
            toUpdate.get().setDescription(newDescription);
        return repository.save(toUpdate.get());
    }

    @Transactional
    public Boolean deleteImprovement(Long idImprovement) {
        Improvement getImprovement = getImprovementsById(idImprovement).orElse(null);
        if (getImprovement == null) {
            return false;
        }
        repository.delete(getImprovement);
        return true;
    }

    @Transactional
    public ResponseEntity registerVoute(Long idImprovement, String vouteValue, String cpf) {
        Voute vouteCreate = new Voute();
        if (vouteValue.equals("Approved"))
            vouteCreate = new Voute(Voute.vouteValue.Approved, cpf);
        if (vouteValue.equals("Rejected"))
            vouteCreate = new Voute(Voute.vouteValue.Rejected, cpf);

        Optional<Improvement> getImprovement = repository.findById(idImprovement);
        if (getImprovement.isPresent() && getImprovement.get().vouteIsUnique(cpf)) {

            getImprovement.get().getVoutes().add(vouteCreate);
            return ResponseEntity.ok().build();
        } else {
            String error = "there is already a vote with the informed cpf";
            throw new ResponseStatusException(HttpStatus.CONFLICT, error);
        }
    }
}
