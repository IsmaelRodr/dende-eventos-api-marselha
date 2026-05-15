package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Organizador {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;
    private boolean ativo = true;
    // atributo todo parte composição, pois a existência de uma empresa é atrelada a
    // existência de um organizador
    private Empresa empresa;
    // atributo todo parte composição, pois a existência de um evento é atrelado a
    // existência de um organizador
    private final List<Evento> eventos = new ArrayList<>();

    // Construtor padrão
    public Organizador() {}

    // Construtor com parâmetros (argumentos)


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

    //record para armazenar login
    public record Credenciais(String email, String senha) {}

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setEmail(String email){this.email = email;}
    public void setSenha(String senha) { this.senha = senha; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getSexo() { return sexo; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public Empresa getEmpresa() { return empresa; }

    //Booleans
    public boolean isAtivo() { return ativo; }

    //Métodos de coleção

    //adiciona o evento a coleção e associa ao organizador.
    public void addEvento(Evento evento){
        if (evento == null) return;

        if (!this.eventos.contains(evento)){
            this.eventos.add(evento);
            evento.setOrganizador(this);
        }
    }

    //remove o evento da coleção e desassocia ao organziador.
    public void removeEvento(Evento evento){
        if (evento == null) return;

        if (this.eventos.remove(evento)){
            evento.setOrganizador(null);
        }
    }

    //busca os eventos da coleção associada ao organizador
    public List<Evento> getEventos(){
        return List.copyOf(eventos);
    }

    //equals para comparação das instâncias.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Organizador that = (Organizador) obj;
        return id != null && id.equals(that.id);
    }

    //hashcode para identificar unicamente a instância.
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //toString para representar textualmente a instância.
    @Override
    public String toString() {
        return "Organizador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", ativo=" + ativo +
                ", empresa=" + (empresa != null ? empresa.getRazaoSocial() : "Nenhuma") +
                ", totalEventos=" + eventos.size() +
                '}';
    }
}