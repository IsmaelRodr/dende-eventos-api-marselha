package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
    private Empresa empresa;
    private final List<Evento> eventos = new ArrayList<>();

    public Organizador() {}

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getEmail() { return email; }
    // setEmail removido – email não pode ser alterado
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public List<Evento> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    public void addEvento(Evento evento) {
        if (evento != null && !this.eventos.contains(evento)) {
            this.eventos.add(evento);
            evento.setOrganizador(this);
        }
    }

    public void removeEvento(Evento evento) {
        this.eventos.remove(evento);
        if (evento != null) {
            evento.setOrganizador(null);
        }
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