package br.com.softhouse.dende.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class Usuario{

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;
    private boolean ativo = true;
    //ponto a se considerar.
    //private final List<Ingresso> ingressos = new ArrayList<>();

    public Usuario() {}

    public Usuario(Long id, String nome, LocalDate dataNascimento, String sexo,
                   String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
    }

    /*Implementação retirada.
    public List<Ingresso> getIngressos() {
        A ideia erá a mesma do evento.
        return null
    }*/

    /*
    public void addIngresso(Ingresso ingresso) {
        Aqui a ideia era trabalhar pela otica do comprador,
        associando o ingresso comprado ao evento e ao comprador.
    }*/

    /*
    public void removeIngresso(Ingresso ingresso) {
        Aqui a idei era remover pela otica do comprador, desassociando a referência do comprador.
    }*/

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario that = (Usuario) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}