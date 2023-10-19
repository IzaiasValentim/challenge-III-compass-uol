package br.izaias.valentim.msemployee.services;

import br.izaias.valentim.msemployee.common.Employees;
import br.izaias.valentim.msemployee.entities.Employee;
import br.izaias.valentim.msemployee.repositories.EmployeeJpaRepository;
import br.izaias.valentim.msemployee.services.exceptions.EmployeeNotFoundException;
import br.izaias.valentim.msemployee.services.exceptions.InvalidCpfException;
import br.izaias.valentim.msemployee.utils.CpfValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeJpaRepository repository;

    @Mock
    private CpfValidator cpfValidator;

    @Test
    public void TestSucessCreationOfEmployee() {
        Employee employeeToSave = Employees.employee_type_valid_cpf;
        when(repository.getEmployeeByCpf(employeeToSave.getCpf())).thenReturn(null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{employees}").
                buildAndExpand(employeeToSave.getCpf()).toUri();

        ResponseEntity response = employeeService.create(employeeToSave);

        assertEquals(201, response.getStatusCode().value());
        verify(repository, times(1)).save(eq(employeeToSave));

    }

    @Test
    public void testCreateEmployee_InvalidCpf() {
        Employee employeeToSave = Employees.employee_type_invalid_cpf;
        doThrow(new InvalidCpfException("Invalid CPF")).when(cpfValidator).validateCpf(anyString());

        ResponseEntity response = employeeService.create(employeeToSave);

        assertEquals(400, response.getStatusCode().value());
        verify(repository, never()).save(any());
    }

    @Test
    public void testGetAllEmployees() {
        Employee employee1 = new Employee("26553035040", "Employee 01", "Role");
        Employee employee2 = new Employee("14917441030", "Employee 02", "Admin");

        List<Employee> employees = Arrays.asList(employee1, employee2);

        when(repository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.gelAllEmployees();

        assertEquals(2, result.size());
        assertEquals("Employee 01", result.get(0).getName());
        assertEquals("Admin", result.get(1).getUserRole());
    }

    @Test
    public void testGetByCpf_Success() {
        Employee employee = Employees.employee_type_valid_cpf;

        when(cpfValidator.validateCpf(eq(employee.getCpf()))).thenReturn(true);
        when(repository.getEmployeeByCpf(eq(employee.getCpf()))).thenReturn(employee);

        ResponseEntity response = employeeService.getByCpf(employee.getCpf());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(employee, response.getBody());
    }

    @Test
    public void testGetByCpf_CpfNotFound() {
        String nonExistentCpf = "98765432109";

        when(cpfValidator.validateCpf(eq(nonExistentCpf))).thenReturn(true);

        when(repository.getEmployeeByCpf(eq(nonExistentCpf))).
                thenThrow(new EmployeeNotFoundException("EMPLOYEE NOT FOUND"));

        try {
            employeeService.getByCpf(nonExistentCpf);
        } catch (EmployeeNotFoundException e) {
            assertEquals("EMPLOYEE NOT FOUND", e.getMessage());
        }
    }

}