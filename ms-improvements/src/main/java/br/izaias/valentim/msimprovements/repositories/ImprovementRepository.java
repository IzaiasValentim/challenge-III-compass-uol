package br.izaias.valentim.msimprovements.repositories;

import br.izaias.valentim.msimprovements.entities.Improvement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImprovementRepository extends JpaRepository<Improvement, Long> {

}
