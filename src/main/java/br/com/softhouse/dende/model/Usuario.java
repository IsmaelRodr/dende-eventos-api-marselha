package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.Objects;

public class Usuario {
    
    private static Long contadorIds = 1L; // Para gerar IDs automáticos (1, 2, 3...)

    private Long id; 
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;

    // Construtor atualizado com as exigências do líder
    public Usuario(String nome, LocalDate dataNascimento, String sexo, String email, String senha) {
        this.id = contadorIds++;
        
        // Validações contra valores nulos
        this.nome = Objects.requireNonNull(nome, "Nome não pode ser nulo");
        this.dataNascimento = Objects.requireNonNull(dataNascimento, "Data de nascimento não pode ser nula");
        this.sexo = Objects.requireNonNull(sexo, "Sexo não pode ser nulo");
        this.email = Objects.requireNonNull(email, "E-mail não pode ser nulo");
        this.senha = Objects.requireNonNull(senha, "Senha não pode ser nula");
    }

    // Construtor vazio exigido pelo Jackson para receber o JSON
    public Usuario() {
        this.id = contadorIds++;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public LocalDate getDataNascimento() { return dataNascimento; }
    // --- NOVO SETTER ADICIONADO ---
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public String getSexo() { return sexo; }
    // --- NOVO SETTER ADICIONADO ---
    public void setSexo(String sexo) { this.sexo = sexo; }
    
    public String getEmail() { return email; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    // --- SEUS MÉTODOS QUE NÃO PODEM FALTAR ---

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