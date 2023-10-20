package br.izaias.valentim.msimprovements.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_voutes")
public class Voute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private vouteValue voute;
    private String cpf;

    public enum vouteValue {
        Approved,
        Rejected
    }

    public Voute() {
    }

    public Voute(vouteValue voute, String cpf) {
        this.voute = voute;
        this.cpf = cpf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public vouteValue getVoute() {
        return voute;
    }

    public void setVoute(vouteValue voute) {
        this.voute = voute;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
