package br.izaias.valentim.msimprovements.services;

import br.izaias.valentim.msimprovements.entities.Improvement;
import br.izaias.valentim.msimprovements.entities.Voute;
import br.izaias.valentim.msimprovements.repositories.VouteRepository;
import br.izaias.valentim.msimprovements.services.exceptions.PersistenceException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VouteService {
    private final VouteRepository vouteRepository;

    public VouteService(VouteRepository vouteRepository) {
        this.vouteRepository = vouteRepository;
    }

    @Transactional
    public Voute createVoute(Voute voute) {
        try {
            return vouteRepository.save(voute);
        } catch (DataAccessException exData) {
            throw new PersistenceException("Error at register the voute", exData);
        }
    }

    public Optional<Voute> getVouteById(Long idVoute) {
        return vouteRepository.findById(idVoute);
    }

    public List<Voute> getAllVoutesByCpf(String cpf) {
        return vouteRepository.findAllByCpf(cpf);
    }

}
