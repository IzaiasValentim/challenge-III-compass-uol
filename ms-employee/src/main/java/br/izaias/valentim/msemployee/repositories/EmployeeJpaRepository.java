package br.izaias.valentim.msemployee.repositories;

import br.izaias.valentim.msemployee.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeJpaRepository extends JpaRepository<Employee, Long> {
    Employee getEmployeeByCpf(String cpf);
}
