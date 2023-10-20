package br.izaias.valentim.msimprovements.repositories;

import br.izaias.valentim.msimprovements.entities.Voute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VouteRepository extends JpaRepository<Voute, Long> {
    List<Voute> findAllByCpf(String cpf);
}
