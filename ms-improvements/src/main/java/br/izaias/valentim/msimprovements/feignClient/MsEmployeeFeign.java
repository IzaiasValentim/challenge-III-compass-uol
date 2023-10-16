package br.izaias.valentim.msimprovements.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="ms-employee", path = "/employees/")
public interface MsEmployeeFeign {

    @GetMapping(value = "{cpf}")
    public ResponseEntity<String> validateEmployeeAndCPF(@PathVariable String cpf);
}
