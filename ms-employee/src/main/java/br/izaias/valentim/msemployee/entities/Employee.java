package br.izaias.valentim.msemployee.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "tb_employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 11, nullable = false)
    private String cpf;
    @Column(nullable = false)

    private String name;
    @Column(nullable = false)
    private String userRole;

    public Employee() {
    }

    public Employee(String cpf, String name, String userRole) {
        this.cpf = cpf;
        this.name = name;
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "employee{" +
                "id=" + id +
                ", cpf='" + cpf + '\'' +
                ", name='" + name + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(cpf, employee.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}
