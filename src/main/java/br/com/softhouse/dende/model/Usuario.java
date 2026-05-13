package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Usuario {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;
    private boolean ativo = true;
    private final List<Ingresso> ingressos = new ArrayList<>();

    // Construtor vazio exigido pelo Jackson para receber o JSON
    public Usuario() {}

    public record Credenciais(String email, String senha) {}
    
    // Getters e Setters

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() { return id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public LocalDate getDataNascimento() { return dataNascimento; }
    // --- NOVO SETTER ADICIONADO ---
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public String getSexo() { return sexo; }
    // --- NOVO SETTER ADICIONADO ---
    public void setSexo(String sexo) { this.sexo = sexo; }

    // Exclusivo para Jackson
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    public boolean isAtivo() {
        return ativo;
    }

    public void addIngresso(Ingresso ingresso){
        if (ingresso == null) return;

        if (!this.ingressos.contains(ingresso)){
            this.ingressos.add(ingresso);
        }
    }

    public void removeIngresso(Ingresso ingresso){
        if (ingresso == null) return;

        this.ingressos.remove(ingresso);
    }

    public List<Ingresso> getIngressos() {
        return List.copyOf(ingressos);
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