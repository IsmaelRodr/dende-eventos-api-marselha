package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.Objects;

public class Organizador {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;
    private boolean ativo = true;
    
    // A alteração de mestre: Composição usando a classe opcional Empresa
    private Empresa empresa;

    // Construtor vazio exigido pelo Jackson para receber o JSON
    public Organizador() {}

    public record Credenciais(String email, String senha) {}
    
    // Getters e Setters

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    
    public String getEmail() { return email; }
    private void setEmail(String email){this.email = email;}
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    // Get e Set da Empresa
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Organizador organizador = (Organizador) object;
        // Agora verifica pelo ID e E-mail, tal como o utilizador
        return Objects.equals(id, organizador.id) && 
               Objects.equals(email, organizador.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Organizador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", sexo='" + sexo + '\'' +
                ", email='" + email + '\'' +
                ", empresa=" + (empresa != null ? empresa.getRazaoSocial() : "Nenhuma") +
                '}';
    }
}