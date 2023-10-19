package br.izaias.valentim.msemployee.services;

import br.izaias.valentim.msemployee.common.Employees;
import br.izaias.valentim.msemployee.entities.Employee;
import br.izaias.valentim.msemployee.repositories.EmployeeJpaRepository;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

}