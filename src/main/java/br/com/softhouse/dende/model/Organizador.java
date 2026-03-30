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
    // A alteração de mestre: Composição usando a classe opcional Empresa
    private Empresa empresa;
    private final List<Evento> eventos = new ArrayList<>();

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
    public void setEmail(String email){this.email = email;}
    
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

    public void addEvento(Evento evento){
        if (evento == null) return;

        if (!this.eventos.contains(evento)){
            this.eventos.add(evento);
            evento.setOrganizador(this);
        }
    }

    public void removeEvento(Evento evento){
        if (evento == null) return;

        if (this.eventos.remove(evento)){
            evento.setOrganizador(null);
        }
    }

    public List<Evento> getEventos(){
        return List.copyOf(eventos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Organizador that = (Organizador) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

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