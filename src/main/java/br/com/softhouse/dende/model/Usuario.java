package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.Objects;

public class Usuario {

    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;

    public Usuario(
            final String nome,
            final LocalDate dataNascimento,
            final String sexo,
            final String email,
            final String senha
    ) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
    }

    public Usuario() {

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Usuario usuario = (Usuario) object;
        return Objects.equals(nome, usuario.nome) && Objects.equals(dataNascimento, usuario.dataNascimento) && Objects.equals(sexo, usuario.sexo) && Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, dataNascimento, sexo, email);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", sexo='" + sexo + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
