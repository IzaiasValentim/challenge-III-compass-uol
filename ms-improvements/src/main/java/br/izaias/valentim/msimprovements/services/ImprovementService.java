package br.izaias.valentim.msimprovements.services;

import br.izaias.valentim.msimprovements.entities.Improvement;
import br.izaias.valentim.msimprovements.entities.Voute;
import br.izaias.valentim.msimprovements.repositories.ImprovementRepository;
import br.izaias.valentim.msimprovements.services.exceptions.ImprovementNotFoundException;
import br.izaias.valentim.msimprovements.services.exceptions.PersistenceException;
import br.izaias.valentim.msimprovements.utils.ManageSectionOfVotes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@EnableAsync
public class ImprovementService {
    private final ImprovementRepository repository;
    private final ManageSectionOfVotes sectionManager;

    @Autowired
    public ImprovementService(ImprovementRepository repository,
                              ManageSectionOfVotes sectionManager) {

        this.repository = repository;
        this.sectionManager = sectionManager;
    }

    @Transactional
    public Improvement createImprovement(Improvement improvementToCreate) {
        try {
            improvementToCreate.setResult(Improvement.Result.IN_PROGRESS);
            Improvement improvementCreated = repository.save(improvementToCreate);

            sectionManager.execute(improvementCreated);

            return improvementCreated;

        } catch (DataAccessException exData) {
            throw new PersistenceException("Error at create improvement", exData);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public ResponseEntity registerVoute(Long idImprovement, String vouteValue, String cpf) {
        Voute vouteCreate = new Voute();
        if (vouteValue.equals("Approved"))
            vouteCreate = new Voute(Voute.vouteValue.Approved, cpf);
        if (vouteValue.equals("Rejected"))
            vouteCreate = new Voute(Voute.vouteValue.Rejected, cpf);

        Improvement getImprovement = repository.findById(idImprovement).orElse(null);

        if ((getImprovement != null) && (getImprovement.vouteIsUnique(cpf)) &&
                (getImprovement.getResult().equals(Improvement.Result.IN_PROGRESS))) {

            getImprovement.getVoutes().add(vouteCreate);
            repository.save(getImprovement);

            return ResponseEntity.ok().build();

        } else {
            String error = "there is already a vote with the informed cpf";
            throw new ResponseStatusException(HttpStatus.CONFLICT, error);
        }
    }

    @Transactional
    public Improvement closeImprovementSessionOfVote(Improvement improvementToCloseSession) {
        try {
            improvementToCloseSession.setResult(Improvement.Result.CLOSED);

            return repository.save(improvementToCloseSession);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ERROR AT CLOSE SECTION:" + improvementToCloseSession.getName());
        }
    }

    public void processingOfVotingSection(Long improvementToCountVoutesId) {
        try {
            Improvement gerImprovement = repository.findById(improvementToCountVoutesId).orElseThrow(() -> new ImprovementNotFoundException("IMPROVEMENT NOT FOUNT"));

            int approved = 0;
            int rejected = 0;

            Set<Voute> allVoutes = gerImprovement.getVoutes();
            for (Voute voute : allVoutes) {
                if (voute.getVoute().equals(Voute.vouteValue.Approved)) {
                    approved++;

                } else {
                    rejected++;

                }
            }

            if (approved > rejected) {
                gerImprovement.setResult(Improvement.Result.APPROVED);
                repository.save(gerImprovement);

            } else if (approved < rejected) {
                gerImprovement.setResult(Improvement.Result.REJECTED);
                repository.save(gerImprovement);

            } else {
                gerImprovement.setResult(Improvement.Result.TIE);
                repository.save(gerImprovement);

            }
        } catch (DataAccessException dEx) {

            throw new PersistenceException("ERROR AT SAVE IMPROVEMENT");

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


}
