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
    //atributo de associação bidirecional, pois as duas classes conhecem-se, mas não dependem
    //umas das outras.
    private final List<Ingresso> ingressos = new ArrayList<>();

    // Construtor padrão
    public Usuario() {}

    //construtor com parâmetros (argumentos)
    public Usuario(Long id, String nome, LocalDate dataNascimento, String sexo,
                   String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
    }

    //record para login
    public record Credenciais(String email, String senha) {}

    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    public void setNome(String nome) { this.nome = nome; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    //retirado agora que temos o construtor com argumentos.
    //public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getSexo() { return sexo; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    //Booleans
    public boolean isAtivo() {
        return ativo;
    }

    //Métodos de coleção

    //método para adicionar ingressos e associar ao usuario
    public void addIngresso(Ingresso ingresso){
        if (ingresso == null) return;

        if (!this.ingressos.contains(ingresso)){
            this.ingressos.add(ingresso);
        }
    }

    //método para remover ingressos e desassociar ao usuario
    public void removeIngresso(Ingresso ingresso){
        if (ingresso == null) return;

        this.ingressos.remove(ingresso);
    }

    //método para buscar ingressos associados ao usuario
    public List<Ingresso> getIngressos() {
        return List.copyOf(ingressos);
    }

    //equals para comparar as instâncias
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Usuario that = (Usuario) obj;
        return id != null && id.equals(that.id);
    }

    //hashcode para representar unicamente a instância
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //toString para representar textualmente a instância
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