package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.Objects;

public class Usuario {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;
    private boolean ativo = true;

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
    private void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    public boolean isAtivo() {
        return ativo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Usuario usuario = (Usuario) object;
        // Adicionei o ID na comparação também, pois agora ele é a identidade do objeto
        return Objects.equals(id, usuario.id) && 
               Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", sexo='" + sexo + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}