package br.izaias.valentim.msimprovements.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "tb_improvements")
public class Improvement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String name;
    private String description;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "improvement_id")
    private final Set<Voute> voutes = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private Result result;

    public enum Result {
        IN_PROGRESS,
        CLOSED,
        APPROVED,
        REJECTED
    }

    public Improvement() {
    }
    public Improvement(String name, String description, Result result) {
        this.name = name;
        this.description = description;
        this.result = result;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Voute> getVoutes() {
        return voutes;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Boolean vouteIsUnique(String cpf){
        for (Voute verify : voutes){
            if(verify.getCpf().equals(cpf))
                return false;
        }
        return true;
    }
}
