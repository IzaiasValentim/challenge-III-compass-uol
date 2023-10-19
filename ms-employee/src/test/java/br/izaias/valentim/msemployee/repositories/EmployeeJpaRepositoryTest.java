package br.izaias.valentim.msemployee.repositories;

import br.izaias.valentim.msemployee.common.Employees;
import br.izaias.valentim.msemployee.entities.Employee;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class EmployeeJpaRepositoryTest {

    @Autowired
    private EmployeeJpaRepository employeeJpaRepository;

    @Test
    public void testGetEmployeeByCpf() {
        Employee employee = Employees.employee_type_valid_cpf;
        employeeJpaRepository.save(employee);

        Employee foundEmployee = employeeJpaRepository.getEmployeeByCpf(employee.getCpf());

        assertEquals("cpf_valido", foundEmployee.getName());
        assertEquals("adm", foundEmployee.getUserRole());
    }
}