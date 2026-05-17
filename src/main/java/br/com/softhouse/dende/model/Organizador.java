package br.com.softhouse.dende.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Organizador {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;
    private boolean ativo = true;
    private Empresa empresa;
    //Ponto a ser revisto
    //private final List<Evento> eventos = new ArrayList<>();

    public Organizador() {}

    public Organizador(Long id, String nome, LocalDate dataNascimento, String sexo,
                       String email, String senha, Empresa empresa) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
        this.empresa = empresa;
    }

    /*
    public List<Evento> getEventos() {
        associar referencias de eventos pertecentes ao organizador
    }*/

    /*
    public void addEvento(Evento evento) {
        associar uma nova referência ao organizador.
    }*/

    /*
    public void removeEvento(Evento evento) {
        remover uma referência do organizador.
    }*/

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Organizador that = (Organizador) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Organizador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", ativo=" + ativo +
                ", empresa=" + (empresa != null ? empresa.getRazaoSocial() : "Nenhuma") +
                '}';
    }
}