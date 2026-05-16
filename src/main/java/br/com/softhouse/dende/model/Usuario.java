package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getEmail() { return email; }
    // setEmail removido intencionalmente – email não pode ser alterado
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public List<Ingresso> getIngressos() {
        return Collections.unmodifiableList(ingressos);
    }

    public void addIngresso(Ingresso ingresso) {
        if (ingresso != null && !this.ingressos.contains(ingresso)) {
            this.ingressos.add(ingresso);
        }
    }

    public void removeIngresso(Ingresso ingresso) {
        this.ingressos.remove(ingresso);
    }

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
                ", totalIngressos=" + ingressos.size() +
                '}';
    }
}