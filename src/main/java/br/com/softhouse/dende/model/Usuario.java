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
    private final List<Ingresso> ingressos = new ArrayList<>();

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

    /*
    public List<Ingresso> getIngressos() {
        return List.copyOf(ingressos);
    }*/


    public void adicionarIngresso(Ingresso ingresso) {
        if (!ingressos.contains(ingresso)){
            ingressos.add(ingresso);
            ingresso.setUsuario(this);
        }
    }


    /*public void cancelarIngresso(Ingresso ingresso) {
        if(ingressos.contains(ingresso)){
            ingressos.remove(ingresso);
            ingresso.cancelar();
        }
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