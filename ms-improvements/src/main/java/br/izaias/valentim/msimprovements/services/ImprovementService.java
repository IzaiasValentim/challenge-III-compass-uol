package br.izaias.valentim.msimprovements.services;

import br.izaias.valentim.msimprovements.entities.Improvement;
import br.izaias.valentim.msimprovements.entities.Voute;
import br.izaias.valentim.msimprovements.feignClient.MsEmployeeFeign;
import br.izaias.valentim.msimprovements.repositories.ImprovementRepository;
import br.izaias.valentim.msimprovements.services.exceptions.ImprovementNotFoundException;
import br.izaias.valentim.msimprovements.services.exceptions.PersistenceException;
import br.izaias.valentim.msimprovements.utils.ManageSectionOfVotes;
import br.izaias.valentim.msimprovements.utils.messages.ImprovementStatusPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
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
    private final MsEmployeeFeign employeeFeign;
    private final ImprovementStatusPublisher publisherStatus;

    @Autowired
    public ImprovementService(ImprovementRepository repository,
                              ManageSectionOfVotes sectionManager,
                              MsEmployeeFeign employeeFeign,
                              ImprovementStatusPublisher publisherStatus) {

        this.repository = repository;
        this.sectionManager = sectionManager;
        this.employeeFeign = employeeFeign;
        this.publisherStatus = publisherStatus;
    }

    @Transactional
    public Improvement createImprovement(Improvement improvementToCreate) {
        try {
            if (improvementToCreate.getName().isEmpty() || improvementToCreate.getDescription().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NAME AND DESCRIPTION ARE REQUIRED");
            }

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

        validateIfExistEmployeeAndCPF(cpf);
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
            String error = "there is already a vote with the informed cpf or session is closed";
            throw new ResponseStatusException(HttpStatus.CONFLICT, error);
        }
    }

    public void validateIfExistEmployeeAndCPF(String cpf) {
        try {
            employeeFeign.validateEmployeeAndCPF(cpf);
        } catch (FeignException.FeignClientException fEx) {
            if (fEx.status() == HttpStatus.NOT_FOUND.value()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CPF NOT ALLOWED TO VOTE");
            } else if (fEx.status() == HttpStatus.BAD_REQUEST.value()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID - CPF");
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "FEIGN ERROR");
            }
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
            publisherStatus.sendStatus(gerImprovement,approved,rejected);
        } catch (DataAccessException dEx) {

            throw new PersistenceException("ERROR AT SAVE IMPROVEMENT");

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Improvement> getAllImprovements() {
        return repository.findAll();
    }

    public Improvement getImprovementsById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ImprovementNotFoundException("IMPROVEMENT NOT FOUND"));
    }

    @Transactional
    public Improvement updateImprovement(Long idImprovament, String newName, String newDescription) {
        try {
            if (idImprovament == null || (newName.isEmpty() && newDescription.isEmpty())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ENTER ID AND NAME OR DESCRIPTION");
            }
            Optional<Improvement> toUpdate = repository.findById(idImprovament);
            if (toUpdate.isEmpty()) {
                throw new ImprovementNotFoundException("IMPROVEMENT NOT FOUND");
            }
            if (!newName.isEmpty())
                toUpdate.get().setName(newName);
            if (!newDescription.isEmpty())
                toUpdate.get().setDescription(newDescription);
            return repository.save(toUpdate.get());
        } catch (DataAccessException dAex) {
            throw new PersistenceException("ERROR AT UPDATE IMPROVEMENT");
        }

    }

    @Transactional
    public Boolean deleteImprovement(Long idImprovement) {
        Improvement getImprovement = getImprovementsById(idImprovement);
        if (getImprovement == null) {
            return false;
        }
        repository.delete(getImprovement);
        return true;
    }


}
